package edu.sustech.xiangqi.ui;

import javax.swing.*;
import java.awt.*;

public class GameOverFrame {

    /**
     * 创建一个游戏结束的对话框
     * @param parentFrame 父窗口，对话框将显示在它之上
     * @param winner      获胜方信息，例如 "红方获胜！"
     * @param onRestart   一个 Runnable 对象，包含了点击“重来”按钮时要执行的逻辑
     */
    public static void show(Component parentFrame, String winner, Runnable onRestart) {
        // 使用 JDialog 
        JDialog ggDialog = new JDialog(SwingUtilities.getWindowAncestor(parentFrame), "游戏结束", Dialog.ModalityType.APPLICATION_MODAL);
        ggDialog.setSize(250, 250); 
        ggDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 只关闭对话框

        // 使用垂直布局
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 添加边距

        // 结果标签
        JLabel resultLabel = new JLabel(winner, SwingConstants.CENTER);
        resultLabel.setFont(new Font("楷体", Font.BOLD, 18));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐

        // 重来按钮
        JButton restartButton = new JButton("再来一局");
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT); 

        panel.add(resultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // 添加垂直间距
        panel.add(restartButton);

        restartButton.addActionListener(e -> {
            // 1. 执行外部传入的重启逻辑
            if (onRestart != null) {
                onRestart.run();
            }
            // 2. 关闭当前对话框
            ggDialog.dispose();
        });

        ggDialog.add(panel);
        ggDialog.setLocationRelativeTo(parentFrame); // 相对于父窗口居中
        ggDialog.setVisible(true);
    }
}