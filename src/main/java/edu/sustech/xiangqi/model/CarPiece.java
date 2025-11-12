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

        int rowDiff = targetRow - currentRow;
        int colDiff = Math.abs(targetCol - currentCol);

        // 车的移动规则：
        // 1. 只能单方向移动
        if (isRed()) {
            // 红方车（向上走，row减小）
            if (rowDiff != 0 && colDiff == 0) return true; // 向上或向下
            return rowDiff == 0 && colDiff != 0;  // 向左或向右
        } else {
            // 黑方车（向下走，row增大）
            if (rowDiff != 0 && colDiff == 0) return true; // 向上或向下
            return rowDiff == 0 && colDiff != 0;  // 向左或向右
        }
    }
}
