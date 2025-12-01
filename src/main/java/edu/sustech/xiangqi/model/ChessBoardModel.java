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
     * 生成当前棋盘局面的简易 FEN 串
     * 格式示例：rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR
     * 大写=红方，小写=黑方，数字=连续空位数
     */
    public String getFen() {
        StringBuilder sb = new StringBuilder();
        // 创建一个临时二维数组来映射棋盘
        String[][] boardMap = new String[ROWS][COLS];
        
        for (AbstractPiece piece : pieces) {
            String code = "";
            // 简易映射：红方大写，黑方小写
            if (piece.isRed()) {
                switch (piece.getName()) {
                    case "帅": code = "K"; break;
                    case "车": code = "R"; break;
                    case "马": code = "N"; break;
                    case "炮": code = "C"; break;
                    case "相": code = "B"; break;
                    case "仕": code = "A"; break;
                    case "兵": code = "P"; break;
                }
            } else {
                switch (piece.getName()) {
                    case "将": code = "k"; break;
                    case "车": code = "r"; break;
                    case "马": code = "n"; break;
                    case "炮": code = "c"; break;
                    case "象": code = "b"; break;
                    case "士": code = "a"; break;
                    case "卒": code = "p"; break;
                }
            }
            boardMap[piece.getRow()][piece.getCol()] = code;
        }

        // 遍历生成字符串
        for (int i = 0; i < ROWS; i++) {
            int emptyCount = 0;
            for (int j = 0; j < COLS; j++) {
                if (boardMap[i][j] == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        sb.append(emptyCount);
                        emptyCount = 0;
                    }
                    sb.append(boardMap[i][j]);
                }
            }
            if (emptyCount > 0) {
                sb.append(emptyCount);
            }
            if (i < ROWS - 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    /**
     * 清空棋盘并加载 FEN 串对应的残局
     * FEN 示例: "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR"
     */
    public void loadFen(String fen) {
        pieces.clear(); // 1. 清空现有棋子
        
        String[] rows = fen.split("/");
        if (rows.length != ROWS) {
            throw new IllegalArgumentException("无效的 FEN 串: 行数不对");
        }

        for (int r = 0; r < ROWS; r++) {
            String rowStr = rows[r];
            int c = 0;
            for (int i = 0; i < rowStr.length(); i++) {
                char ch = rowStr.charAt(i);
                if (Character.isDigit(ch)) {
                    // 数字代表连续的空位
                    c += Character.getNumericValue(ch);
                } else {
                    // 字母代表棋子
                    // 大写=红方, 小写=黑方
                    boolean isRed = Character.isUpperCase(ch);
                    String name = getPieceNameFromChar(ch);
                    if (name != null) {
                        AbstractPiece piece = createPiece(name, r, c, isRed);
                        if (piece != null) {
                            pieces.add(piece);
                        }
                    }
                    c++;
                }
            }
        }
    }

    // 字符转棋子名辅助方法
    private String getPieceNameFromChar(char ch) {
        char lower = Character.toLowerCase(ch);
        switch (lower) {
            case 'k': return Character.isUpperCase(ch) ? "帅" : "将";
            case 'r': return "车";
            case 'n': return "马";
            case 'c': return "炮";
            case 'b': return Character.isUpperCase(ch) ? "相" : "象";
            case 'a': return Character.isUpperCase(ch) ? "仕" : "士";
            case 'p': return Character.isUpperCase(ch) ? "兵" : "卒";
            default: return null;
        }
    }

    // 棋子工厂方法
    private AbstractPiece createPiece(String name, int r, int c, boolean isRed) {
        switch (name) {
            case "将": return new GeneralPiece("将", r, c, isRed);
            case "帅": return new GeneralPiece("帅", r, c, isRed);
            case "车": return new CarPiece("车", r, c, isRed);
            case "马": return new MaPiece("马", r, c, isRed);
            case "炮": return new PaoPiece("炮", r, c, isRed);
            case "相": case "象": return new XiangPiece(name, r, c, isRed);
            case "仕": case "士": return new ShiPiece(name, r, c, isRed);
            case "兵": case "卒": return new SoldierPiece(name, r, c, isRed);
            default: return null;
        }
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
