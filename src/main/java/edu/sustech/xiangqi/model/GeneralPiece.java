package edu.sustech.xiangqi.model;

/**
 * 帅/将
 */
public class GeneralPiece extends AbstractPiece {

    public GeneralPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public AbstractPiece copy() {
        return new GeneralPiece(this.getName(), this.getRow(), this.getCol(), this.isRed());
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        // TODO: 实现将/帅的移动规则
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }
        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false; // 不能吃自己的棋子
        }
        if (Math.abs(targetRow - currentRow) + Math.abs(targetCol - currentCol) != 1) {
            return false;
        } else {
            // 检查是否在九宫格内
            if (isRed()) {
                if (targetRow < 7 || targetCol < 3 || targetCol > 5) return false;
            } else {
                if (targetRow > 2 || targetCol < 3 || targetCol > 5) return false;
            }
        }
        int row = -1;
        int col = -1;//找到对面将/帅的位置
        for (int r = 0; r < ChessBoardModel.getRows(); r++) {
            for (int c = 0; c < ChessBoardModel.getCols(); c++) {
                AbstractPiece piece = model.getPieceAt(r, c);
                if (piece != null && isRed() && piece.getName().equals("将")) {
                    row = r;
                    col = c;
                } else if (piece != null && !isRed() && piece.getName().equals("帅")) {
                    row = r;
                    col = c;
                }
            }
        }
        if (col == targetCol) { // 如果目标位置和对方的将在同一列
            int start = Math.min(targetRow, row) + 1;
            int end = Math.max(targetRow, row);
            boolean blocked = false; // 检查中间是否有棋子
            for (int i = start; i < end; i++) {
                if (model.getPieceAt(i, col) != null) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                return false; // 如果中间没有棋子格挡，则此移动非法
            }
        }
        
        return true; 
    }
}
