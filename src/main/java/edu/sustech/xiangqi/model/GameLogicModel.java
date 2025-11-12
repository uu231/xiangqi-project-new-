package edu.sustech.xiangqi.model;

public class GameLogicModel {
    private boolean redTurn;
    private AbstractPiece selectedPiece;
    private ChessBoardModel model;

    public GameLogicModel(ChessBoardModel model) {
        this.redTurn = true;
        this.selectedPiece = null;
        this.model = model;
    }

    private void initGame() {


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
        if (selectedPiece == null) return false;

        // 检查移动是否合法（委托棋子规则）
        if (!selectedPiece.canMoveTo(targetRow, targetCol, model)) {
            return false;
        }
        // 委托 model 执行移动（model.movePiece 应该负责吃子并返回是否成功）
        boolean moved = model.movePiece(selectedPiece, targetRow, targetCol);

        if (moved) {
            // 移动成功后清除选中并切换回合
            selectedPiece = null;
            changeTurn();
        }

        return moved;
    }

    public boolean checkGameOver() {
        return false;
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

    public void changeTurn() {
        redTurn = !redTurn;
    }

}