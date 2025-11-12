package edu.sustech.xiangqi.model;

/**
 * 帅/将
 */
public class GeneralPiece extends AbstractPiece {

    public GeneralPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
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
        int rowDiff = targetRow - currentRow;
        int colDiff = targetCol - currentCol;

        // 将/帅的移动规则：
        // 1. 只能走一步
        // 2. 只能在九宫里走
        if (isRed()) {
            // 红方帅（向上走，row减小）

            boolean onTheLeftMargin = currentCol == 3;
            boolean onTheRightMargin = currentCol == 5;
            boolean onTheTopMargin = currentRow == 7;
            boolean onTheBottomMargin = currentRow == 9;
            if (rowDiff == -1 && colDiff == 0 && !onTheTopMargin) {// 向上
                return true;
            } else if (rowDiff == 0 && colDiff == 1 && !onTheRightMargin) {//向右
                return true;
            } else if (rowDiff == 0 && colDiff == -1 && !onTheLeftMargin) {//向左
                return true;
            } else if (rowDiff == 1 && colDiff == 0 && !onTheBottomMargin) {//向下
                return true;
            }
            
        } else {
            // 黑方将（向下走，row增大）
            boolean onTheLeftMargin = currentCol == 3;
            boolean onTheRightMargin = currentCol == 5;
            boolean onTheTopMargin = currentRow == 0;
            boolean onTheBottomMargin = currentRow == 2;
            if (rowDiff == 1 && colDiff == 0 && !onTheBottomMargin) {// 向下
                return true;
            } else if (rowDiff == 0 && colDiff == 1 && !onTheRightMargin) {//向右
                return true;
            } else if (rowDiff == 0 && colDiff == -1 && !onTheLeftMargin) {//向左
                return true;
            } else if (rowDiff == -1 && colDiff == 0 && !onTheTopMargin) {//向上
                return true;
            }
        }
    

        return false;
    }
}
