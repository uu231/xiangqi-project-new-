package edu.sustech.xiangqi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogicModel {
    private boolean redTurn;
    private AbstractPiece selectedPiece;
    private ChessBoardModel model;
    private GameState gameState;
    private final Stack<Move> moveHistory;

    public enum GameState {
        PLAYING,
        RED_WIN,
        BLACK_WIN,
        RED_WIN_NC,
        BLACK_WIN_NC
    }
    public GameLogicModel(ChessBoardModel model) {
        this.redTurn = true;
        this.selectedPiece = null;
        this.model = model;
        this.moveHistory = new Stack<>();
        initGame();
    }

    private void initGame() {
        this.gameState = GameState.PLAYING;
        this.moveHistory.clear();
    }

    public boolean selectPiece(int row, int col) {
        AbstractPiece piece = model.getPieceAt(row, col);
        if (piece == null || (piece.isRed() != redTurn)) {//如果未选中棋子，或者选中的不是该回合的颜色
            return false;
        } else {
            selectedPiece = piece;//选择棋子
            return true;
        }
    }

    public boolean tryMove(int targetRow, int targetCol) {
            if (gameState != GameState.PLAYING) {
                System.out.println("游戏已结束，不能再移动棋子。");
                return false;
            }
            if (selectedPiece == null) {
                return false;
            }
            // 1. 基础移动规则检查
            if (!selectedPiece.canMoveTo(targetRow, targetCol, model)) {
                return false;
            }
            
            // 2. 模拟移动，检查是否会导致自己被将军（“送将”）
            int originalRow = selectedPiece.getRow();
            int originalCol = selectedPiece.getCol();
            AbstractPiece capturedPiece = model.getPieceAt(targetRow, targetCol);
            if (capturedPiece != null) {
                model.getPieces().remove(capturedPiece);
            }
            selectedPiece.moveTo(targetRow, targetCol);
            
            //  检查在模拟移动后，自己是否被将军
            boolean willBeInCheck = isChecked(selectedPiece.isRed());
            
            // 是否会导致将帅碰面
            boolean willFaceGenerals = isGeneralFacing();

            // 恢复棋盘
            selectedPiece.moveTo(originalRow, originalCol);
            if (capturedPiece != null) {
                model.getPieces().add(capturedPiece);
            }

            //  如果模拟移动会导致自己被将军，则此移动非法
            if (willBeInCheck) {
                System.out.println("非法移动：不能送将！");
                return false;
            }
            
            // 如果模拟移动会导致将帅碰面，则此移动非法
            if (willFaceGenerals) {
                System.out.println("非法移动：不能让将帅碰面！");
                return false;
            }

            AbstractPiece pieceToMove = selectedPiece;//记录移动
            int fromRow = pieceToMove.getRow();
            int fromCol = pieceToMove.getCol();
            AbstractPiece pieceToCapture = model.getPieceAt(targetRow, targetCol);
            Move move = new Move(pieceToMove, fromRow, fromCol, targetRow, targetCol, pieceToCapture);
            // 3. 如果所有检查都通过，执行真正的移动
            boolean moved = model.movePiece(selectedPiece, targetRow, targetCol);
            if (moved) {
                moveHistory.push(move);
                selectedPiece = null;

                changeTurn();
                checkAndUpdateGameState();
            }
            return moved;
    }

    public boolean undoMove() {//悔棋逻辑
        if (moveHistory.isEmpty()) {
            return false;
        }
        Move lastMove = moveHistory.pop();
        AbstractPiece movedPiece = lastMove.getMovedPiece();
        int currentRow = movedPiece.getRow();
        int currentCol = movedPiece.getCol();
        AbstractPiece eatPiece = lastMove.getEatPiece();
        movedPiece.moveTo(lastMove.getFromRow(), lastMove.getFromCol());
        if (eatPiece != null) {//如果有子被吃就恢复
            model.getPieces().add(eatPiece);
        }
        changeTurn();

        gameState = GameState.PLAYING;
        selectedPiece = null;
        return true;
    }

    public boolean checkAndUpdateGameState() {
        // 检查轮到走棋的一方是否还有合法的移动
        boolean canMove = hasAnyLegalMove(redTurn);

        if (!canMove) {
            if (isChecked(redTurn)) {
                // 被将军且无路可走 -> 将死
                gameState = redTurn ? GameState.BLACK_WIN : GameState.RED_WIN;
            } else {
                // 没有被将军但无路可走 -> 困毙
                gameState = redTurn ? GameState.BLACK_WIN_NC : GameState.RED_WIN_NC;
            }
            return true; // 游戏结束
        }
        gameState = GameState.PLAYING; // 确保游戏在悔棋后能继续
        return false; // 游戏继续
    }

    /**
     * 检查当前棋盘状态是否为“将帅碰面”
     */
    private boolean isGeneralFacing() {
        AbstractPiece redGeneral = null;
        AbstractPiece blackGeneral = null;

        // 找到将和帅
        for (AbstractPiece piece : model.getPieces()) {
            if (piece.getName().equals("帅")) {
                redGeneral = piece;
            } else if (piece.getName().equals("将")) {
                blackGeneral = piece;
            }
            if (redGeneral != null && blackGeneral != null) {
                break;
            }
        }

        if (redGeneral == null || blackGeneral == null) {
            return false; // 游戏异常状态
        }

        // 检查是否在同一列
        if (redGeneral.getCol() != blackGeneral.getCol()) {
            return false;
        }

        // 检查中间是否有棋子
        int commonCol = redGeneral.getCol();
        int startRow = Math.min(redGeneral.getRow(), blackGeneral.getRow()) + 1;
        int endRow = Math.max(redGeneral.getRow(), blackGeneral.getRow());

        for (int r = startRow; r < endRow; r++) {
            if (model.getPieceAt(r, commonCol) != null) {
                return false; // 被阻挡
            }
        }

        // 在同一列且中间无阻挡
        return true;
    }

    public boolean isChecked(boolean isRed) {
        int targetRow = -1, targetCol = -1;
        for (int r = 0; r < ChessBoardModel.getRows(); r++) {
            for (int c = 0; c < ChessBoardModel.getCols(); c++) {
                AbstractPiece piece = model.getPieceAt(r, c);
                if (piece != null && isRed && piece.getName().equals("帅")) {
                    targetRow = r;
                    targetCol = c;
                } else if (piece != null && !isRed && piece.getName().equals("将")) {
                    targetRow = r;
                    targetCol = c;
                }
            }
        }
        for (int r = 0; r < ChessBoardModel.getRows(); r++) {
            for (int c = 0; c < ChessBoardModel.getCols(); c++) {
                AbstractPiece piece = model.getPieceAt(r, c);
                if (piece != null && piece.isRed() != isRed) {
                    if (piece.canMoveTo(targetRow, targetCol, model)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    public boolean hasAnyLegalMove(boolean isRed) {
        List<AbstractPiece> piecesCopy = new ArrayList<>(model.getPieces());
        // 1. 获取该方的所有棋子
        for (AbstractPiece piece : piecesCopy) {
            if (piece.isRed() == isRed) {
                // 2. 为该棋子尝试移动到棋盘上的所有位置
                for (int r = 0; r < ChessBoardModel.getRows(); r++) {
                    for (int c = 0; c < ChessBoardModel.getCols(); c++) {
                        // 3. 检查移动是否“真正合法”
                        if (isMoveLegal(piece, r, c)) {
                            return true; 
                        }
                    }
                }
            }
        }
        // 遍历完所有棋子和所有位置，都没找到合法移动
        return false;

    }

    public boolean isMoveLegal(AbstractPiece piece, int targetRow, int targetCol) {
        if (!piece.canMoveTo(targetRow, targetCol, model)) {
            return false;
        }

        int originalRow = piece.getRow();
        int originalCol = piece.getCol();
        AbstractPiece capturedPiece = model.getPieceAt(targetRow, targetCol);

        if (capturedPiece != null) {
            model.getPieces().remove(capturedPiece);
        }
        piece.moveTo(targetRow, targetCol);

        // 检查自己是否被将军
        boolean inCheck = isChecked(piece.isRed());

        boolean facing = isGeneralFacing();

        // 恢复棋盘
        piece.moveTo(originalRow, originalCol);
        if (capturedPiece != null) {
            model.getPieces().add(capturedPiece);
        }

        // 如果移动后不会被将军，则移动合法
        return !inCheck && !facing;
    }

    public Move getLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        return moveHistory.peek();
    }
   
    public void cancelSelection() {
        selectedPiece = null;
    }

    public AbstractPiece getSelectedPiece() {
        return selectedPiece;
    }

    public boolean isRedTurn() {
        return redTurn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void changeTurn() {
        redTurn = !redTurn;
    }

    public void restart() {
        // 重新初始化棋盘和游戏逻辑
        model.initializePieces(); // 假设 ChessBoardModel 有此方法来重置棋子
        initGame();
        this.redTurn = true;
        this.selectedPiece = null;
    }

    /**
     * AI调用：获取当前回合方所有合法的走法
     */
    public List<Move> getAllLegalMoves(boolean isRedPlayer) {
        List<Move> legalMoves = new ArrayList<>();
        List<AbstractPiece> piecesCopy = new ArrayList<>(model.getPieces());

        for (AbstractPiece piece : piecesCopy) {
            if (piece.isRed() == isRedPlayer) {
                for (int r = 0; r < ChessBoardModel.getRows(); r++) {
                    for (int c = 0; c < ChessBoardModel.getCols(); c++) {
                        // 使用 isMoveLegal 检查，它包含了防送将和防飞将
                        if (isMoveLegal(piece, r, c)) {
                            legalMoves.add(new Move(
                                    piece,
                                    piece.getRow(),
                                    piece.getCol(),
                                    r,
                                    c,
                                    model.getPieceAt(r, c)
                            ));
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    /**
     * AI调用：评估当前棋盘分数
     * 正数代表红方优势，负数代表黑方优势
     */
    public int evaluateBoard() {
        int totalScore = 0;
        for (AbstractPiece piece : model.getPieces()) {
            int value = getPieceValue(piece);
            totalScore += (piece.isRed() ? value : -value);
        }
        return totalScore;
    }

    /**
     * AI调用：获取单个棋子的基础价值
     * (这是一个非常基础的估值, 你可以调整这些分数)
     */
    private int getPieceValue(AbstractPiece piece) {
        if (piece == null) return 0;

        // 基础分
        int value = 0;
        switch (piece.getName()) {
            case "帅": case "将": value = 10000; break;
            case "车": value = 900; break;
            case "炮": value = 450; break;
            case "马": value = 400; break;
            case "相": case "象": value = 200; break;
            case "仕": case "士": value = 200; break;
            case "兵": value = 100; break;
            case "卒": value = 100; break;
        }

        // 位置分 (简单示例)
        if (piece.getName().equals("兵")) {
            // 红兵过河
            if (piece.isRed() && piece.getRow() < 5) value += 100;
        } else if (piece.getName().equals("卒")) {
            // 黑卒过河
            if (!piece.isRed() && piece.getRow() > 4) value += 100;
        }

        return value;
    }

}