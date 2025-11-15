package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.*;
import edu.sustech.xiangqi.ui.*;

import javax.swing.*;
import java.awt.*; 
import java.util.Arrays; 

public class XiangqiApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("中国象棋");
            frame.setSize(560, 800); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            ChessBoardModel model = new ChessBoardModel();
            GameLogicModel gameLogic = new GameLogicModel(model);
            ChessBoardPanel boardPanel = new ChessBoardPanel(model, gameLogic);
            UserPanel topUserPanel = new UserPanel("黑方 (电脑/对手)", true, gameLogic, boardPanel);
            UserPanel bottomUserPanel = new UserPanel("红方 (我)", false, gameLogic, boardPanel);

            frame.add(topUserPanel, BorderLayout.NORTH);  // 顶部
            frame.add(boardPanel, BorderLayout.CENTER);   // 中间棋盘
            frame.add(bottomUserPanel, BorderLayout.SOUTH); // 底部
            frame.pack();
            frame.setLocationRelativeTo(null);

            LoginFrame login = new LoginFrame(frame);

           
        });
    }
}
