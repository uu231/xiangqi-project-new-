package edu.sustech.xiangqi.model;

/**
 * 车
 */
public class CarPiece extends AbstractPiece {

    public CarPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }
        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null) {
            if (targetPiece.isRed() == this.isRed()) {
                return false; // 不能吃自己的棋子
            } 
        }

        int rowDiff = targetRow - currentRow;
        int colDiff = Math.abs(targetCol - currentCol);

        // 车的移动规则：
        // 1. 只能单方向移动
        if (rowDiff != 0 && colDiff != 0) return false;

        // 2. 循环检查途中是否有格挡
        if (currentRow == targetRow) {
            // 横向移动
            int start = Math.min(currentCol, targetCol) + 1;
            int end = Math.max(currentCol, targetCol);
            for (int c = start; c < end; c++) {
                if (model.getPieceAt(currentRow, c) != null) {
                    return false; // 路径被阻挡
                }
            }
        } else {
            // 纵向移动
            int start = Math.min(currentRow, targetRow) + 1;
            int end = Math.max(currentRow, targetRow);
            for (int r = start; r < end; r++) {
                if (model.getPieceAt(r, currentCol) != null) {
                    return false; // 路径被阻挡
                }
            }
        }

        return true;
    }
}
