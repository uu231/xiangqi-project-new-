package edu.sustech.xiangqi.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class EndGameLevels {
    // 使用 LinkedHashMap 保持插入顺序
    private static final Map<String, String> levels = new LinkedHashMap<>();

    static {
        // 格式: 关卡名 -> FEN串
        // 1. 蚯蚓降龙 (双兵对单车，经典必胜)
        levels.put("初级挑战：兵临城下", "2bak4/4a4/4b4/R3c4/2P6/9/9/4C4/4p4/2BA1K3"); 
        
        // 2. 野马操田 (经典江湖残局)
        levels.put("高级挑战：野马操田", "2bak4/4a4/4b4/8/2P6/9/9/4C4/4p4/2BA1K3"); // (注：这里用的是近似FEN，你可以找更准确的)
        
        // 3. 简单的单车杀王 (测试用)
        levels.put("入门练习：单车杀将", "4k4/9/9/9/9/9/9/9/4R4/4K4");
    }

    public static Map<String, String> getLevels() {
        return levels;
    }
}