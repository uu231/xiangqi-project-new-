package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.GameLogicModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ControlPanel extends JPanel {
    private final GameLogicModel gameLogic;
    private final ChessBoardPanel chessBoardPanel;

    public ControlPanel(GameLogicModel gameLogic, ChessBoardPanel chessBoardPanel) {
        this.gameLogic = gameLogic;
        this.chessBoardPanel = chessBoardPanel;
        initComponents();
    }

    /**
     *  获取父窗口
     * 每次调用时动态获取，确保引用始终有效
     */
    private Component getParentWindow() {
        Component parent = SwingUtilities.getWindowAncestor(this);
        return parent != null ? parent : chessBoardPanel;
    }

    private void initComponents() {
        // 面板基础设置
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "ControlPanel", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("宋体", Font.PLAIN, 14)));
        setBackground(new Color(240, 217, 181));

        // 创建组件
        JButton undoButton = new JButton("悔棋");
        JButton restartButton = new JButton("重新开始");
        JButton surrenderButton = new JButton("投降");

        // 样式设置
        Font buttonFont = new Font("宋体", Font.BOLD, 16);
        undoButton.setFont(buttonFont);
        restartButton.setFont(buttonFont);
        surrenderButton.setFont(buttonFont);

        // 添加按钮逻辑
        undoButton.addActionListener(e -> {
            boolean undo = gameLogic.undoMove();
            if (undo) {
                chessBoardPanel.repaint(); // 悔棋后刷新棋盘
            } else {
                System.out.println("无棋可悔");
            }

        });

        restartButton.addActionListener(e -> {
            // 增加确认对话框，防止误触
            int result = JOptionPane.showConfirmDialog(
                    getParentWindow(), // 对话框父组件
                    "确定要重新开始游戏吗？",
                    "确认",
                    JOptionPane.YES_NO_OPTION
                );
            if (result == JOptionPane.YES_OPTION) {
                gameLogic.restart();
                chessBoardPanel.repaint(); // 重启后刷新棋盘
            }
        });

        surrenderButton.addActionListener(e -> {
            String player = gameLogic.isRedTurn() ? "Red" : "Black";
            String winner = gameLogic.isRedTurn() ? "黑方获胜" : "红方获胜";
            int result = JOptionPane.showConfirmDialog(
                getParentWindow(),
                "确定要投降吗？当前玩家：" + player,
                "确认",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                if (winner != null) {
                    Runnable restartAction = () -> {
                        gameLogic.restart(); 
                        chessBoardPanel.repaint();
                    };
                    // 显示游戏结束对话框
                    GameOverFrame.show(chessBoardPanel, winner, restartAction);
                }
            }
            
        });

        // 组合组件
        add(undoButton);
        add(restartButton);
        add(surrenderButton);
    }
}