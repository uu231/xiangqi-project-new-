package edu.sustech.xiangqi.model;

/**
 * 马
 */
public class MaPiece extends AbstractPiece {

    public MaPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        int rowDiff = Math.abs(targetRow - currentRow);
        int colDiff = Math.abs(targetCol - currentCol);

        // 马的移动规则：
        // 1. 日字形（先不考虑蹩脚
        if (isRed()) {
            // 红方马（向上走，row减小）
            if (rowDiff == 1 && colDiff == 2) return true; 
            return rowDiff == 2 && colDiff == 1;  
        } else {
            // 黑方马（向下走，row增大）
            if (rowDiff == 1 && colDiff == 2) return true; 
            return rowDiff == 2 && colDiff == 1;  
        }
    }
}
