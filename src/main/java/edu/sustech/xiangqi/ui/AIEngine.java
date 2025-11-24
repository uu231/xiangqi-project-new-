package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.GameLogicModel;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.Move;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AIEngine {
    private final GameLogicModel gameLogic;
    private final ChessBoardPanel boardPanel;
    private final int searchDepth; // AI 搜索深度

    public AIEngine(GameLogicModel gameLogic, ChessBoardPanel boardPanel, int searchDepth) {
        this.gameLogic = gameLogic;
        this.boardPanel = boardPanel;
        this.searchDepth = searchDepth;
    }

    public void performComputerMove() {
        boardPanel.setBoardEnabled(false);
        boardPanel.setAISimulating(true);
        boardPanel.repaint();

        SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() throws Exception {
                // 1. 【关键】克隆游戏环境（数据模型 + 逻辑模型）
                // 注意：这里需要在主线程(EDT)之外做，但为了保证克隆时的原子性，
                // 最好在 execute 之前或这里加锁瞬间克隆一下，或者利用 ChessBoardModel 的 synchronized
                
                ChessBoardModel sandboxModel; 
                boolean currentRedTurn;
                synchronized(gameLogic.getModel()) { // 假设 gameLogic 提供了 getModel()
                    sandboxModel = gameLogic.getModel().deepClone();
                    currentRedTurn = gameLogic.isRedTurn();
                }
                
                // 创建一个专属于 AI 的逻辑控制器，绑定到沙盒棋盘上
                GameLogicModel sandboxLogic = new GameLogicModel(sandboxModel);

                sandboxLogic.setRedTurn(currentRedTurn);
                
                // 2. 在沙盒逻辑上跑 AI，完全不会影响 UI
                // 注意：findBestMove 需要修改，让它接受 sandboxLogic 作为参数
                return findBestMove(sandboxLogic, searchDepth, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            @Override
            protected void done() {
                try {
                    Move bestMove = get(); // 获取计算结果（这里的 Move 包含的是沙盒里的棋子对象）
                    if (bestMove != null) {
                        // 3. 将沙盒的计算结果，映射回主棋盘
                        // 因为 bestMove 里的 Piece 是沙盒里的对象，不能直接用。
                        // 我们只需要坐标：
                        int fromRow = bestMove.getFromRow();
                        int fromCol = bestMove.getFromCol();
                        int toRow = bestMove.getToRow();
                        int toCol = bestMove.getToCol();

                        // 在主线程执行真实移动
                        gameLogic.selectPiece(fromRow, fromCol);
                        gameLogic.tryMove(toRow, toCol);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    // 恢复 UI
                    boardPanel.setAISimulating(false);
                    boardPanel.setBoardEnabled(true);
                    boardPanel.repaint();
                    if (boardPanel.checkGameOver()) {
                        boardPanel.showGameOverDialog();
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * 查找最佳走法 (Alpha-Beta 剪枝的入口)
     * AI是黑方，所以是 "MinimizingPlayer" (isMaximizingPlayer = false)
     */
    private Move findBestMove(GameLogicModel logic, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        // ✅ 修正：使用参数 logic，而不是全局的 gameLogic 或 Logic
        List<Move> legalMoves = logic.getAllLegalMoves(logic.isRedTurn());

        Collections.shuffle(legalMoves);
        legalMoves.sort((m1, m2) -> (m2.getEatPiece() != null ? 1 : 0) - (m1.getEatPiece() != null ? 1 : 0));

        Move bestMove = null;
        int bestValue = Integer.MAX_VALUE; // AI (黑方) 找最小值

        for (Move move : legalMoves) {
            logic.performMoveUnchecked(move);

            // ✅ 修正：将沙盒 logic 传递给递归函数
            int value = minimax(logic, depth - 1, true, alpha, beta);

            logic.undoMoveUnchecked();

            if (value < bestValue) {
                bestValue = value;
                bestMove = move;
            }
            beta = Math.min(beta, bestValue);
            if (beta <= alpha) break;
        }
        return bestMove;
    }

    /**
     * Minimax 递归函数
     * 注意参数：增加了 GameLogicModel logic
     */
    private int minimax(GameLogicModel logic, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        if (depth == 0) {
            // ✅ 修正：评估沙盒 logic 的局面
            return logic.evaluateBoard();
        }

        // ✅ 修正：检查沙盒 logic 的状态
        if (logic.checkAndUpdateGameState()) {
            GameLogicModel.GameState state = logic.getGameState();
            if (state == GameLogicModel.GameState.RED_WIN) return 1000000;
            if (state == GameLogicModel.GameState.BLACK_WIN) return -1000000;
            return 0;
        }

        List<Move> legalMoves = logic.getAllLegalMoves(logic.isRedTurn());

        if (legalMoves.isEmpty()) {
            return 0;
        }

        if (isMaximizingPlayer) { // 红方 (Max)
            int bestValue = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                logic.performMoveUnchecked(move);

                // ✅ 修正：传递 logic
                bestValue = Math.max(bestValue, minimax(logic, depth - 1, false, alpha, beta));
                logic.undoMoveUnchecked();

                alpha = Math.max(alpha, bestValue);
                if (beta <= alpha) break;
            }
            return bestValue;
        } else { // 黑方 (Min)
            int bestValue = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                logic.performMoveUnchecked(move);

                // ✅ 修正：传递 logic
                bestValue = Math.min(bestValue, minimax(logic, depth - 1, true, alpha, beta));
                logic.undoMoveUnchecked();

                beta = Math.min(beta, bestValue);
                if (beta <= alpha) break;
            }
            return bestValue;
        }
    }
}