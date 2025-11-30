package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.GameLogicModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;
import edu.sustech.xiangqi.ui.LoginFrame;
import edu.sustech.xiangqi.ui.UserPanel;
import edu.sustech.xiangqi.ui.WelcomeFrame;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.*;

public class XiangqiApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
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

            Runnable startLocalGame = () -> {
                boardPanel.setGameMode(false); // false = 关闭AI
                frame.setVisible(true);
            };

            Runnable startAIGame = () -> {
                boardPanel.setGameMode(true); // true = 开启AI
                frame.setVisible(true);
            };

            // 4. 创建欢迎界面，并传入启动逻辑
            WelcomeFrame welcome = new WelcomeFrame(startLocalGame, startAIGame);
            
            // 5. 创建登录界面，并让它在成功后显示欢迎界面
            // 把 welcome::show 作为回调传给 LoginFrame
            LoginFrame login = new LoginFrame(welcome::show);

        });
    }
}
