package edu.sustech.xiangqi.model;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardModel {
    // 储存棋盘上所有的棋子，要实现吃子的话，直接通过pieces.remove(被吃掉的棋子)删除就可以
    private final List<AbstractPiece> pieces;
    private static final int ROWS = 10;
    private static final int COLS = 9;

    public ChessBoardModel() {
        pieces = new ArrayList<>();
        initializePieces();
    }

    public void initializePieces() {
        pieces.clear();
        // 黑方棋子
        pieces.add(new GeneralPiece("将", 0, 4, false));
        pieces.add(new SoldierPiece("卒", 3, 0, false));
        pieces.add(new SoldierPiece("卒", 3, 2, false));
        pieces.add(new SoldierPiece("卒", 3, 4, false));
        pieces.add(new SoldierPiece("卒", 3, 6, false));
        pieces.add(new SoldierPiece("卒", 3, 8, false));
        pieces.add(new CarPiece("车", 0, 0, false));
        pieces.add(new CarPiece("车", 0, 8, false));
        pieces.add(new MaPiece("马", 0, 1, false));
        pieces.add(new MaPiece("马", 0, 7, false));
        pieces.add(new ShiPiece("士", 0, 3, false));
        pieces.add(new ShiPiece("士", 0, 5, false));
        pieces.add(new XiangPiece("象", 0, 2, false));
        pieces.add(new XiangPiece("象", 0, 6, false));
        pieces.add(new PaoPiece("炮", 2, 1, false));
        pieces.add(new PaoPiece("炮", 2, 7, false));


        // 红方棋子
        pieces.add(new GeneralPiece("帅", 9, 4, true));
        pieces.add(new SoldierPiece("兵", 6, 0, true));
        pieces.add(new SoldierPiece("兵", 6, 2, true));
        pieces.add(new SoldierPiece("兵", 6, 4, true));
        pieces.add(new SoldierPiece("兵", 6, 6, true));
        pieces.add(new SoldierPiece("兵", 6, 8, true));
        pieces.add(new CarPiece("车", 9, 0, true));
        pieces.add(new CarPiece("车", 9, 8, true));
        pieces.add(new MaPiece("马", 9, 1, true));
        pieces.add(new MaPiece("马", 9, 7, true));
        pieces.add(new ShiPiece("仕", 9, 3, true));
        pieces.add(new ShiPiece("仕", 9, 5, true));
        pieces.add(new XiangPiece("相", 9, 2, true));
        pieces.add(new XiangPiece("相", 9, 6, true));
        pieces.add(new PaoPiece("炮", 7, 1, true));
        pieces.add(new PaoPiece("炮", 7, 7, true));


    }

    public List<AbstractPiece> getPieces() {
        return pieces;
    }

    public AbstractPiece getPieceAt(int row, int col) {
        for (AbstractPiece piece : pieces) {
            if (piece.getRow() == row && piece.getCol() == col) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    // 同步 movePiece 方法
    public boolean movePiece(AbstractPiece piece, int newRow, int newCol) {
        if (!isValidPosition(newRow, newCol)) {
            return false;
        }
        AbstractPiece targetPiece = getPieceAt(newRow, newCol);
    
        if (targetPiece != null) {
            if (targetPiece.isRed() != piece.isRed()) {
                pieces.remove(targetPiece);  // 这个 remove 是线程安全的
            }
        }

        piece.moveTo(newRow, newCol);
        return true;
    }

    public ChessBoardModel deepClone() {
        ChessBoardModel newModel = new ChessBoardModel();
        newModel.getPieces().clear(); // 清空初始化时的默认棋子
        
        // 复制当前棋盘上的所有棋子
        for (AbstractPiece piece : this.pieces) {
            newModel.addPiece(piece.copy()); // 关键：使用 copy() 创建新对象
        }
        return newModel;
    }
    
    /**
     * 添加一个棋子（用于悔棋和AI模拟）
     */
    public void addPiece(AbstractPiece piece) {
        pieces.add(piece);
    }

    /**
     * 移除一个棋子（用于AI模拟）
     */
    public void removePiece(AbstractPiece piece) {
        pieces.remove(piece);
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }
}
