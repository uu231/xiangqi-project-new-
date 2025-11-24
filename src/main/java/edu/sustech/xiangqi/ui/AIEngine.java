package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.GameLogicModel;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.Move;
import edu.sustech.xiangqi.model.OpeningBook;

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
                // 1.克隆游戏环境（数据模型 + 逻辑模型）

                
                ChessBoardModel sandboxModel; 
                boolean currentRedTurn;
                List<String> currentHistory;
                synchronized(gameLogic.getModel()) { 
                    sandboxModel = gameLogic.getModel().deepClone();
                    currentRedTurn = gameLogic.isRedTurn();
                    currentHistory = gameLogic.getFenHistory();
                }
                
                GameLogicModel sandboxLogic = new GameLogicModel(sandboxModel);

                sandboxLogic.setRedTurn(currentRedTurn);
                sandboxLogic.setFenHistory(currentHistory);

                // 查询开局库 
                String currentFen = sandboxModel.getFen();
                // 方便复制 FEN 串去手写开局库
                System.out.println("当前FEN: " + currentFen); 
                
                int[] bookMoveCoords = OpeningBook.getBookMove(currentFen);
                if (bookMoveCoords != null) {
                    System.out.println(">>> 命中开局库！秒下！");
                    // 将坐标转换为合法的 Move 对象返回
                    return findMoveByCoords(sandboxLogic, bookMoveCoords);
                }
                
                // 2. 在沙盒逻辑上跑 AI，完全不会影响 UI
                return findBestMove(sandboxLogic, searchDepth, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            @Override
            protected void done() {
                try {
                    Move bestMove = get(); // 获取计算结果（这里的 Move 包含的是沙盒里的棋子对象）
                    if (bestMove != null) {
                        // 3. 将沙盒的计算结果，映射回主棋盘
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
        List<Move> legalMoves = logic.getAllLegalMoves(logic.isRedTurn());

        Collections.shuffle(legalMoves);
        legalMoves.sort((m1, m2) -> (m2.getEatPiece() != null ? 1 : 0) - (m1.getEatPiece() != null ? 1 : 0));

        Move bestMove = null;
        int bestValue = Integer.MAX_VALUE; // AI (黑方) 找最小值

        for (Move move : legalMoves) {
            if (logic.isProhibitedMove(move)) {
                continue;
            }
            logic.performMoveUnchecked(move);
        
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
     */
    private int minimax(GameLogicModel logic, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        if (depth == 0) {
            // 评估沙盒 logic 的局面
            return logic.evaluateBoard();
        }

        // 检查沙盒 logic 的状态
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

                // 传递 logic
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

                // 传递 logic
                bestValue = Math.min(bestValue, minimax(logic, depth - 1, true, alpha, beta));
                logic.undoMoveUnchecked();

                beta = Math.min(beta, bestValue);
                if (beta <= alpha) break;
            }
            return bestValue;
        }
    }


    private Move findMoveByCoords(GameLogicModel logic, int[] coords) {
        int fromR = coords[0];
        int fromC = coords[1];
        int toR = coords[2];
        int toC = coords[3];
        
        // 必须从当前所有合法走法中匹配，以确保走法合法
        List<Move> legalMoves = logic.getAllLegalMoves(logic.isRedTurn());
        for (Move move : legalMoves) {
            AbstractPiece p = move.getMovedPiece();
            if (p.getRow() == fromR && p.getCol() == fromC && 
                move.getToRow() == toR && move.getToCol() == toC) {
                return move;
            }
        }
        System.err.println("开局库走法在当前局面不合法！");
        return null; // 开局库走法不合法
    }
}