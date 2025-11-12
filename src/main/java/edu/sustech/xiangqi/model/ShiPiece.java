package edu.sustech.xiangqi.model;

/**
 * 士
 */
public class ShiPiece extends AbstractPiece {

    public ShiPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        // TODO: 实现士的移动规则
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }
        int rowDiff = targetRow - currentRow;
        int colDiff = targetCol - currentCol;

        // 仕的移动规则：
        // 1. 只能走一步/斜向
        // 2. 只能在九宫里走
        if (isRed()) {
            // 红方仕（向上走，row减小）

            boolean onTheLeftTopCorner = currentCol == 3 && currentRow == 7;
            boolean onTheRightTopCorner = currentCol == 5 && currentRow == 7;
            boolean onTheLeftBottomCorner = currentCol == 3 && currentRow == 9;
            boolean onTheRightBottomCorner = currentCol == 5 && currentRow == 9;
            boolean onTheMiddle = currentCol == 4 && currentRow == 8;
            if (rowDiff == -1 && colDiff == -1 && (onTheRightBottomCorner || onTheMiddle)) {
                return true;
            } else if (rowDiff == -1 && colDiff == 1 && (onTheLeftBottomCorner || onTheMiddle)) {
                return true;
            } else if (rowDiff == 1 && colDiff == -1 && (onTheRightTopCorner || onTheMiddle)) {
                return true;
            } else if (rowDiff == 1 && colDiff == 1 && (onTheLeftTopCorner || onTheMiddle)) {
                return true;
            }
            
        } else {
            // 黑方仕（向下走，row增大）
            boolean onTheLeftTopCorner = currentCol == 3 && currentRow == 0;
            boolean onTheRightTopCorner = currentCol == 5 && currentRow == 0;
            boolean onTheLeftBottomCorner = currentCol == 3 && currentRow == 2;
            boolean onTheRightBottomCorner = currentCol == 5 && currentRow == 2;
            boolean onTheMiddle = currentCol == 4 && currentRow == 1;
            if (rowDiff == -1 && colDiff == -1 && (onTheRightBottomCorner || onTheMiddle)) {
                return true;
            } else if (rowDiff == -1 && colDiff == 1 && (onTheLeftBottomCorner || onTheMiddle)) {
                return true;
            } else if (rowDiff == 1 && colDiff == -1 && (onTheRightTopCorner || onTheMiddle)) {
                return true;
            } else if (rowDiff == 1 && colDiff == 1 && (onTheLeftTopCorner || onTheMiddle)) {
                return true;
            }
        }
    

        return false;
    }
}
