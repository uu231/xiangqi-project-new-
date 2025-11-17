package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.GameLogicModel;
import edu.sustech.xiangqi.model.Move;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class AIEngine {
    private final GameLogicModel gameLogic;
    private final ChessBoardPanel boardPanel;
    private final int searchDepth; // AI 搜索深度

    public AIEngine(GameLogicModel gameLogic, ChessBoardPanel boardPanel, int searchDepth) {
        this.gameLogic = gameLogic;
        this.boardPanel = boardPanel;
        this.searchDepth = searchDepth;
    }

    /**
     * 启动 AI 思考并执行移动
     */
    public void performComputerMove() {
        // 禁用棋盘，防止玩家在AI思考时点击
        boardPanel.setBoardEnabled(false);
        boardPanel.setAISimulating(true); 
        boardPanel.repaint(); // 立即重绘，显示 "思考中..."

        // 使用 SwingWorker 在后台线程计算
        SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() throws Exception {
                return findBestMove(searchDepth, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            @Override
            protected void done() {
                try {
                    Move bestMove = get();
                    if (bestMove != null) {
                        gameLogic.selectPiece(bestMove.getMovedPiece().getRow(), bestMove.getMovedPiece().getCol());
                        gameLogic.tryMove(bestMove.getToRow(), bestMove.getToCol());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    boardPanel.setAISimulating(false); // 告诉棋盘模拟结束
                    boardPanel.clearAIPiecesSnapshot();
                    boardPanel.setBoardEnabled(true);
                    boardPanel.repaint(); 
                    
                    // 检查游戏是否结束
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
    private Move findBestMove(int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        List<Move> legalMoves = gameLogic.getAllLegalMoves(gameLogic.isRedTurn());
        
        // 简单的走法排序：优先尝试吃子
        Collections.shuffle(legalMoves); 
        legalMoves.sort((m1, m2) -> (m2.getEatPiece() != null ? 1 : 0) - (m1.getEatPiece() != null ? 1 : 0));

        Move bestMove = null;

        // AI 是黑方 
        int bestValue = Integer.MAX_VALUE;
        for (Move move : legalMoves) {
            // 1. 执行模拟走法
            gameLogic.selectPiece(move.getMovedPiece().getRow(), move.getMovedPiece().getCol());
            gameLogic.tryMove(move.getToRow(), move.getToCol());

            // 2. 递归调用 (下一层是红方, isMaximizingPlayer = true)
            int value = minimax(depth - 1, true, alpha, beta);
            
            // 3. 撤销走法
            gameLogic.undoMove();

            // 4. 更新 (黑方找最小值)
            if (value < bestValue) {
                bestValue = value;
                bestMove = move;
            }
            beta = Math.min(beta, bestValue);
            if (beta <= alpha) break; // Alpha 剪枝
        }
        return bestMove;
    }

    /**
     * Minimax 递归函数
     */
    private int minimax(int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        // (新) 修复：必须先检查深度
        if (depth == 0) {
            // 到达搜索底部，直接评估当前局面
            return gameLogic.evaluateBoard(); 
        }

        // 检查游戏是否结束 (将死/困毙)
        if (gameLogic.checkAndUpdateGameState()) { 
            GameLogicModel.GameState state = gameLogic.getGameState();
            if (state == GameLogicModel.GameState.RED_WIN) return 1000000; 
            if (state == GameLogicModel.GameState.BLACK_WIN) return -1000000;
            if (state == GameLogicModel.GameState.RED_WIN_NC || state == GameLogicModel.GameState.BLACK_WIN_NC) return 0;
        }

        // 递归
        List<Move> legalMoves = gameLogic.getAllLegalMoves(gameLogic.isRedTurn());

        if (legalMoves.isEmpty()) {
             return 0; // 困毙，和棋
        }
        
        if (isMaximizingPlayer) { // 红方 (Max)
            int bestValue = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                gameLogic.selectPiece(move.getMovedPiece().getRow(), move.getMovedPiece().getCol());
                gameLogic.tryMove(move.getToRow(), move.getToCol());
                
                bestValue = Math.max(bestValue, minimax(depth - 1, false, alpha, beta));
                gameLogic.undoMove();
                
                alpha = Math.max(alpha, bestValue);
                if (beta <= alpha) break; // Beta 剪枝
            }
            return bestValue;
        } else { // 黑方 (Min)
            int bestValue = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                gameLogic.selectPiece(move.getMovedPiece().getRow(), move.getMovedPiece().getCol());
                gameLogic.tryMove(move.getToRow(), move.getToCol());
                
                bestValue = Math.min(bestValue, minimax(depth - 1, true, alpha, beta));
                gameLogic.undoMove();

                beta = Math.min(beta, bestValue);
                if (beta <= alpha) break; // Alpha 剪枝
            }
            return bestValue;
        }
    }
}