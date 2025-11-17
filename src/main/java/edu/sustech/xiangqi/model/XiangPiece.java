package edu.sustech.xiangqi.model;

/**
 * 象/相
 */
public class XiangPiece extends AbstractPiece {

    public XiangPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
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

        int rowDiff = targetRow - currentRow;
        int colDiff = targetCol - currentCol;

        // 2. 必须走 "田" 字 (斜走2格)
        if (Math.abs(rowDiff) != 2 || Math.abs(colDiff) != 2) {
            return false;
        }

        // 3. 检查 "象眼" (田字中心) 是否被堵住
        int midRow = currentRow + rowDiff / 2;
        int midCol = currentCol + colDiff / 2;
        if (model.getPieceAt(midRow, midCol) != null) {
            return false; // 象眼被塞
        }

        // 4. 检查是否过河
        if (isRed()) {
            // 红方象 (row 5-9)
            if (targetRow < 5) return false;
        } else {
            // 黑方象 (row 0-4)
            if (targetRow > 4) return false;
        }

        return true;
    }
}