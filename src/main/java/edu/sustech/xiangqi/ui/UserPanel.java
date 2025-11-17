package edu.sustech.xiangqi.ui;

import java.awt.Image;
import javax.swing.ImageIcon;

import edu.sustech.xiangqi.model.GameLogicModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UserPanel extends JPanel {
    private final String playerName;
    private final boolean isTopPanel; // 区分上方还是下方面板
    private final GameLogicModel gameLogic;
    private final ChessBoardPanel boardPanel; 
    private Image panelImage;


    public UserPanel(String playerName, boolean isTopPanel, GameLogicModel gameLogic, ChessBoardPanel boardPanel) {
        this.playerName = playerName;
        this.isTopPanel = isTopPanel;
        this.gameLogic = gameLogic;
        this.boardPanel = boardPanel;
        this.panelImage = ImageLoader.loadImage("bg1.jpg");


        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 80)); // 设置面板高度
        setBackground(new Color(245, 245, 245)); 
        setBorder(BorderFactory.createMatteBorder(
                isTopPanel ? 0 : 1, 0, isTopPanel ? 1 : 0, 0, 
                new Color(200, 200, 200))); // 分割线

        // 1. 头像区域 (左侧)
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        infoPanel.setOpaque(false);

        // 模拟头像 
        JLabel avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(isTopPanel ? Color.BLACK : Color.RED); // 上方黑方，下方红方
                g2d.fillOval(0, 0, 48, 48);

            }
        };

        avatarLabel.setPreferredSize(new Dimension(50, 50));

        // 名字
        JLabel nameLabel = new JLabel(playerName);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        nameLabel.setForeground(Color.DARK_GRAY);

        infoPanel.add(avatarLabel);
        infoPanel.add(nameLabel);
        add(infoPanel, BorderLayout.WEST);

        // 2. 右上角菜单 (仅上方面板有)
        if (isTopPanel) {
            JButton menuButton = createHamburgerButton();
            JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            buttonContainer.setOpaque(false);
            buttonContainer.add(menuButton);
            add(buttonContainer, BorderLayout.EAST);
        }
    }

    /**
     * 创建自定义样式的“三道杠”汉堡按钮
     */
    private JButton createHamburgerButton() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 鼠标悬停效果
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                }

                // 绘制三条横线
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int w = getWidth();
                int h = getHeight();
                int gap = 7;
                
                // 中间线
                g2d.drawLine(10, h / 2, w - 10, h / 2);
                // 上面线
                g2d.drawLine(10, h / 2 - gap, w - 10, h / 2 - gap);
                // 下面线
                g2d.drawLine(10, h / 2 + gap, w - 10, h / 2 + gap);

                g2d.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(40, 40));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 点击事件：弹出菜单
        btn.addActionListener(e -> showGameMenu(btn));

        return btn;
    }

    /**
     * 显示弹出菜单
     */
    private void showGameMenu(Component invoker) {
        JPopupMenu popup = new JPopupMenu() {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                // 宽度强制设置
                size.width = 150; 
                return size;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(94, 58, 30, 230)); 
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
            }
        };

        popup.setOpaque(false);
        popup.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int iconSize = 20;
        
        // 美化菜单项字体
        Font menuFont = new Font("微软雅黑", Font.PLAIN, 20);

        // 1. 加载悔棋图标
        Image undoImage = ImageLoader.loadImage("undo.png"); 
        ImageIcon undoIcon = null;
        if (undoImage != null) {
            undoIcon = new ImageIcon(undoImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        }

        // 2. 加载重开图标
        Image restartImage = ImageLoader.loadImage("restart.png"); 
        ImageIcon restartIcon = null;
        if (restartImage != null) {
            restartIcon = new ImageIcon(restartImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        }
        
        // 3. 加载投降图标
        Image surrenderImage = ImageLoader.loadImage("White_Flag.png");
        ImageIcon surrenderIcon = null;
        if (surrenderImage != null) {
            surrenderIcon = new ImageIcon(surrenderImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        }

        
        JMenuItem undoItem = new JMenuItem("悔棋");
        undoItem.setFont(menuFont);
        if (undoIcon != null) {
            undoItem.setIcon(undoIcon); 
        }
        undoItem.addActionListener(e -> {
            boolean undone;
            // (新) 检查是否是 AI 游戏
            if (boardPanel.isAIGame()) {
                // 人机模式下，一次悔两步
                undone = gameLogic.undoMove(); // 1. 悔棋 (AI 的一步)
                if (undone) {
                    gameLogic.undoMove(); // 2. 悔棋 (玩家的上一步)
                }
            } else {
                // 本地对战，一次悔一步
                undone = gameLogic.undoMove();
            }

            if (undone) {
                boardPanel.repaint(); // 刷新棋盘
            } else {
                JOptionPane.showMessageDialog(boardPanel, "无棋可悔");
            }
        });

        JMenuItem restartItem = new JMenuItem("重新开始");
        restartItem.setFont(menuFont);
        if (restartIcon != null) {
            restartItem.setIcon(restartIcon); 
        }
        restartItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(boardPanel, "确定要重开吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                gameLogic.restart();
                boardPanel.repaint();
            }
        });

        JMenuItem surrenderItem = new JMenuItem("认输投降");
        surrenderItem.setFont(menuFont);
        if (surrenderIcon != null) {
            surrenderItem.setIcon(surrenderIcon); 
        }
        surrenderItem.addActionListener(e -> {
            String winner = gameLogic.isRedTurn() ? "黑方获胜" : "红方获胜";
            int result = JOptionPane.showConfirmDialog(boardPanel, "确定要投降吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                GameOverFrame.show(boardPanel, winner, () -> {
                    gameLogic.restart();
                    boardPanel.repaint();
                });
            }
        });

        popup.add(undoItem);
        popup.add(createCustomSeparator());
        popup.add(restartItem);
        popup.add(surrenderItem);

        // 在按钮下方显示
        popup.show(invoker, 0, invoker.getHeight());
    }

    private JSeparator createCustomSeparator() {
        JSeparator separator = new JSeparator();
        // 使用一个较亮的棕色作为分割线颜色
        separator.setForeground(new Color(143, 98, 63, 200));
        separator.setBackground(new Color(143, 98, 63, 200));
        return separator;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (panelImage != null) g2d.drawImage(panelImage, 0, 0, getWidth(), getHeight(), this);
    }
}