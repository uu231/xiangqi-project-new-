package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.XiangqiApplication;

import javax.swing.*;
import java.awt.*;

/**
 * 简单的欢迎界面，提供本地对战/人机对战入口，背景为一张图片。
 * 不继承 JFrame，通过组合方式管理窗口。
 */
public class WelcomeFrame {

	private final JFrame window;
	private final Runnable localBattleAction;
	private final Runnable aiBattleAction;

	public WelcomeFrame() {
		this(null, null);
	}

	public WelcomeFrame(Runnable localBattleAction) {
		this(localBattleAction, null);
	}

	public WelcomeFrame(Runnable localBattleAction, Runnable aiBattleAction) {
		this.localBattleAction = localBattleAction;
		this.aiBattleAction = aiBattleAction;
		this.window = buildWindow();
	}

	private JFrame buildWindow() {
		JFrame frame = new JFrame("欢迎来到中国象棋");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 620);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
        final int bannerHeight = (int) (frame.getHeight() / 3.0);

        Image titleImage = ImageLoader.loadImage("splash.png"); // 放在 resources/images/ 里
        JPanel titlePanel;
        if (titleImage != null) {
            Image finalTitleImage = titleImage;
            titlePanel = new JPanel() {
                {
                    setOpaque(false);
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(0, bannerHeight);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    double ratio = (double) finalTitleImage.getWidth(null) / finalTitleImage.getHeight(null);
                    int drawWidth = panelWidth;
                    int drawHeight = (int) (panelWidth / ratio);
                    if (drawHeight > panelHeight) {
                        drawHeight = panelHeight;
                        drawWidth = (int) (panelHeight * ratio);
                    }
                    int x = (panelWidth - drawWidth) / 2;
                    int y = (panelHeight - drawHeight) / 2;
                    g.drawImage(finalTitleImage, x, y, drawWidth, drawHeight, this);
                }
            };
        } else {
            titlePanel = new JPanel();
            titlePanel.setOpaque(false);
            titlePanel.setLayout(new GridLayout(2, 1));
            JLabel titleLabel = new JLabel("中国象棋", SwingConstants.CENTER);
            titleLabel.setFont(new Font("华文行楷", Font.BOLD, 42));
            titleLabel.setForeground(new Color(92, 54, 31));
            JLabel subtitleLabel = new JLabel("请选择对局模式", SwingConstants.CENTER);
            subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            subtitleLabel.setForeground(new Color(110, 84, 60));
            titlePanel.add(titleLabel);
            titlePanel.add(subtitleLabel);
        }

		Image background = ImageLoader.loadImage("bg.jpg");
		BackgroundPanel backgroundPanel = new BackgroundPanel(background);
		backgroundPanel.setLayout(new BorderLayout());
		backgroundPanel.setBorder(BorderFactory.createEmptyBorder(48, 40, 48, 40));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		JButton localButton = createPrimaryButton("本地对战");
		localButton.addActionListener(e -> handleLocalBattle());

		JButton aiButton = createSecondaryButton("人机对战（暂未开放）");
		aiButton.addActionListener(e -> handleAiBattle());

		buttonPanel.add(Box.createVerticalStrut(30));
		buttonPanel.add(localButton);
		buttonPanel.add(Box.createVerticalStrut(18));
		buttonPanel.add(aiButton);
		buttonPanel.add(Box.createVerticalStrut(30));

		JLabel tipLabel = new JLabel("棋逢对手，邀君入局", SwingConstants.CENTER);
		tipLabel.setFont(new Font("仿宋", Font.ITALIC, 16));
		tipLabel.setForeground(new Color(120, 90, 55));

		backgroundPanel.add(titlePanel, BorderLayout.NORTH);
		backgroundPanel.add(buttonPanel, BorderLayout.CENTER);
		backgroundPanel.add(tipLabel, BorderLayout.SOUTH);

		frame.setContentPane(backgroundPanel);
		return frame;
	}

	public void show() {
		window.setVisible(true);
	}

	public void close() {
		window.dispose();
	}

	private void handleLocalBattle() {
		window.dispose();
		Runnable runnable = localBattleAction != null ? localBattleAction : this::launchDefaultLocalBattle;
		runnable.run();
	}

	private void handleAiBattle() {
		if (aiBattleAction != null) {
			aiBattleAction.run();
		} else {
			JOptionPane.showMessageDialog(window,
					"人机对战模式正在开发中，敬请期待！",
					"提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void launchDefaultLocalBattle() {
		SwingUtilities.invokeLater(() -> XiangqiApplication.main(new String[0]));
	}

	private JButton createPrimaryButton(String text) {
		JButton button = new JButton(text);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFont(new Font("微软雅黑", Font.BOLD, 20));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(143, 90, 52));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return button;
	}

	private JButton createSecondaryButton(String text) {
		JButton button = new JButton(text);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		button.setForeground(new Color(92, 54, 31));
		button.setBackground(new Color(255, 255, 255, 220));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(196, 172, 148)),
				BorderFactory.createEmptyBorder(12, 22, 12, 22)
		));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return button;
	}

	private static class BackgroundPanel extends JPanel {
		private final Image background;

		private BackgroundPanel(Image background) {
			this.background = background;
			setOpaque(background == null);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (background != null) {
				g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
			}
		}
	}
}