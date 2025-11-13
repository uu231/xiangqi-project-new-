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
            frame.setSize(640, 800); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            ChessBoardModel model = new ChessBoardModel();
            GameLogicModel gameLogic = new GameLogicModel(model);
            ChessBoardPanel boardPanel = new ChessBoardPanel(model, gameLogic);
            ControlPanel controlPanel = new ControlPanel(gameLogic, boardPanel);//控制面板

            frame.add(boardPanel, BorderLayout.CENTER); //棋盘放在中间
            frame.add(controlPanel, BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            LoginFrame login = new LoginFrame(frame);
           
        });
    }
}
