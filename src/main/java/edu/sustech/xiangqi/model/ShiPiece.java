package edu.sustech.xiangqi.model;

/**
 * 士/仕
 */
public class ShiPiece extends AbstractPiece {

    public ShiPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public AbstractPiece copy() {
        return new ShiPiece(this.getName(), this.getRow(), this.getCol(), this.isRed());
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        // 1. 检查目标位置是否有己方棋子
        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false;
        }

        // 2. 检查是否走斜线一步
        if (Math.abs(targetRow - currentRow) != 1 || Math.abs(targetCol - currentCol) != 1) {
            return false;
        }

        // 3. 检查目标位置是否在九宫格内
        if (targetCol < 3 || targetCol > 5) {
            return false;
        }

        if (isRed()) {
            // 红方九宫 (row 7, 8, 9)
            if (targetRow < 7 || targetRow > 9) {
                return false;
            }
        } else {
            // 黑方九宫 (row 0, 1, 2)
            if (targetRow < 0 || targetRow > 2) {
                return false;
            }
        }

        return true;
    }
}