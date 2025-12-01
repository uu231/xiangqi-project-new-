package edu.sustech.xiangqi.model;

/**
 * 马
 */
public class MaPiece extends AbstractPiece {

    public MaPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public AbstractPiece copy() {
        return new MaPiece(this.getName(), this.getRow(), this.getCol(), this.isRed());
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        // 1. 不能原地不动
        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        // 2. 检查目标位置是否有己方棋子
        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false; // 不能吃自己的棋子
        }

        // 3. 检查是否走日字（行列移动距离必须是 2+1 的组合）
        int rowDiff = Math.abs(targetRow - currentRow);
        int colDiff = Math.abs(targetCol - currentCol);

        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false; // 不是日字形移动
        }

        // 4. 检查"马脚"是否被别住
        // 马走日字时，先直走一格，再斜走一格
        // 需要检查"直走的那一格"是否有棋子

        int blockRow = currentRow;  // 马脚位置的行
        int blockCol = currentCol;  // 马脚位置的列

        if (rowDiff == 2) {
            // 行移动了 2 格，说明是先竖直走 1 格
            // 马脚在竖直方向的中间位置
            blockRow = currentRow + (targetRow - currentRow) / 2;
            // 列不变
            blockCol = currentCol;
        } else if (colDiff == 2) {
            // 列移动了 2 格，说明是先横向走 1 格
            // 马脚在横向方向的中间位置
            blockRow = currentRow;
            // 行不变
            blockCol = currentCol + (targetCol - currentCol) / 2;
        }

        // 检查马脚位置是否有棋子
        if (model.getPieceAt(blockRow, blockCol) != null) {
            return false; // 马脚被别住，不能移动
        }

        return true;
    }
}