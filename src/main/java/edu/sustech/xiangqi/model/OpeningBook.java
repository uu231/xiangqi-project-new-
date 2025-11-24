package edu.sustech.xiangqi.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OpeningBook {
    // Key: 棋盘局面的 FEN 串
    // Value: 推荐走法数组 {fromRow, fromCol, toRow, toCol}
    // String 数组存储走法："起点行,起点列,终点行,终点列"
    private static final Map<String, String[]> book = new HashMap<>();

    static {
        init();
    }

    /**
     *在这里手动录入你的开局库
     */
    private static void init() {
        // ==========================================AI为红
        String startFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR";
        book.put(startFen, new String[]{
            "7,7,7,4", // 炮二平五
            "7,1,7,4"  // 炮八平五
        });

        // ==========================================AI为黑
        // 假设红方走了炮二平五，现在的局面 FEN 是：
        String fenAfterPao2Ping5 = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR";
        book.put(fenAfterPao2Ping5, new String[]{
            "0,7,2,6", // 马8进7 (屏风马)
            "2,7,2,4"  // 炮8平5 (顺手炮)
        });

        book.put("rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/4C2C1/9/RNBAKABNR", new String[]{
            "0,1,2,2", 
            "2,1,2,4"  
        });

        book.put("rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/2N1C2C1/9/R1BAKABNR", new String[]{
            "0,1,2,2", 
            "2,1,2,3"  
        });

        book.put("rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C1N2/9/RNBAKAB1R", new String[]{
            "0,8,0,7", 
            "3,6,4,6",
            "3,2,4,2" 
        });

        book.put("rnbakab1r/9/1c4nc1/p3p1p1p/2p6/9/P1P1P1P1P/1C2C1N2/9/RNBAKABR1", new String[]{
            "0,8,0,7"
        });

        book.put("rnbakab1r/9/1c4nc1/p3p1p1p/2p6/6P2/P1P1P3P/1C2C1N2/9/RNBAKAB1R", new String[]{
            "0,1,2,2", 
            "0,8,0,7"
        });

        book.put("rnbakabnr/9/1c5c1/p1p1p1p1p/9/2P6/P3P1P1P/1C5C1/9/RNBAKABNR", new String[]{
            "2,1,2,2", 
            "0,2,2,4",
            "3,6,4,6"  
        });

        book.put("rnbakabnr/9/1c5c1/p1p1p1p1p/9/6P2/P1P1P3P/1C5C1/9/RNBAKABNR", new String[]{
            "2,7,2,6", 
            "0,6,2,4",
            "3,2,4,2"  
        });

        book.put("rnbakabnr/9/1c5c1/p1p1p3p/6p2/2P6/P3P1P1P/1CN4C1/9/R1BAKABNR", new String[]{
            "0,7,2,6"
        });

        book.put("r1bakabnr/9/1cn4c1/p1p1p1p1p/9/9/P1P1P1P1P/2N1C2C1/9/R1BAKABNR", new String[]{
            "0,7,2,6", 
            "0,0,0,1",
            "3,2,4,2"  
        });

        book.put("r1bakab1r/9/1cn3nc1/p1p1p1p1p/9/9/P1P1P1P1P/2N1C2C1/9/1RBAKABNR", new String[]{
            "0,0,0,1"
        });
    }

    /**
     * 查询开局库
     * @return 返回 {fromRow, fromCol, toRow, toCol} 或者 null
     */
    public static int[] getBookMove(String fen) {
        if (book.containsKey(fen)) {
            String[] moves = book.get(fen);
            // 随机选择一种走法，让 AI 更多变
            String moveStr = moves[new Random().nextInt(moves.length)];
            String[] parts = moveStr.split(",");
            return new int[]{
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
            };
        }
        return null;
    }
}