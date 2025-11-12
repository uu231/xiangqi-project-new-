package edu.sustech.xiangqi.model;

/**
 * 象
 */
public class XiangPiece extends AbstractPiece {

    public XiangPiece(String name, int row, int col, boolean isRed) {
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
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false; // 不能吃自己的棋子
        }

        int rowDiff = targetRow - currentRow;
        int colDiff = Math.abs(targetCol - currentCol);

        // 象的移动规则：
        // 1. 田字形
        // 2. 不能过河
        // 3. 不能别象眼
        int midRowPosition = (currentRow+targetRow)/2;
        int midColPosition = (currentCol+targetCol)/2;
        AbstractPiece targetPiece1 = model.getPieceAt(midRowPosition, midColPosition);
        if (targetPiece1 != null) {
            return false;
        }
        if (isRed()) {
            // 红方象（向上走，row减小）
            if (rowDiff == -2 && colDiff == 2 && targetRow >= 5) return true; 
            return rowDiff == 2 && colDiff == 2;
        } else {
            // 黑方象（向下走，row增大）
            if (rowDiff == 2 && colDiff == 2 && targetRow <= 4) return true;
            return rowDiff == -2 && colDiff == 2;
        }
        
    }
}
