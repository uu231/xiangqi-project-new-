package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.GameLogicModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

public class ChessBoardPanel extends JPanel {
    private final ChessBoardModel model;
    private final GameLogicModel gameLogic;
    private Image boardImage;

    /**
     * 单个棋盘格子的尺寸（px）
     */
    private static final int CELL_SIZE = 65;

    /**
     * 棋盘边界与窗口边界的边距
     */
    private static final int MARGIN = 38;

    /**
     * 棋子的半径
     */
    private static final int PIECE_RADIUS = 32;

    public ChessBoardPanel(ChessBoardModel model, GameLogicModel gameLogic) {
        this.model = model;
        this.gameLogic = gameLogic;
        this.boardImage = ImageLoader.loadImage("WOOD.GIF");
        setPreferredSize(new Dimension(
                CELL_SIZE * (ChessBoardModel.getCols() - 1) + MARGIN * 2,
                CELL_SIZE * (ChessBoardModel.getRows() - 1) + MARGIN * 2
        ));
        setBackground(new Color(220, 179, 92));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    private void handleMouseClick(int x, int y) {
        int col = Math.round((float)(x - MARGIN) / CELL_SIZE);
        int row = Math.round((float)(y - MARGIN) / CELL_SIZE);

        if (!model.isValidPosition(row, col)) {
            return;
        }

        AbstractPiece selectedPiece = gameLogic.getSelectedPiece();
        
        if (selectedPiece == null) {
            // 尝试选中棋子
            if (gameLogic.selectPiece(row, col)) {
                // 选中成功，刷新界面
                repaint();
            }
        } else {
            // 尝试移动
            if (gameLogic.tryMove(row, col)) {
                // 移动成功
                repaint();     
                // 检查游戏是否结束
                if (checkGameOver()) {
                    showGameOverDialog();
                }
            } else {
                // 移动失败（非法移动）
                // 如果点击的是己方棋子，则切换选中
                if (gameLogic.selectPiece(row, col)) {
                    repaint();
                } else {
                    gameLogic.cancelSelection();
                    repaint();
                }
            }
        }
    }

    private void showGameOverDialog() {
        GameLogicModel.GameState state = gameLogic.getGameState(); // 假设 gameLogic 有 getGameState
        String winner = null;

        if (state == GameLogicModel.GameState.BLACK_WIN) {
            winner = "黑方获胜！";
        } else if (state == GameLogicModel.GameState.RED_WIN) {
            winner = "红方获胜！";
        } else if (state == GameLogicModel.GameState.RED_WIN_NC) {
            winner = "红方获胜！（困毙）";
        } else if (state == GameLogicModel.GameState.BLACK_WIN_NC) {
            winner = "黑方获胜！（困毙）";
        }

        if (winner != null) {
            // 定义重启逻辑
            Runnable restartAction = () -> {
                gameLogic.restart(); 
                repaint();
            };
            
            // 显示游戏结束对话框
            GameOverFrame.show(this, winner, restartAction);
        }
    }


    public boolean checkGameOver() {
        boolean currentPlayerIsRed = gameLogic.isRedTurn();
        boolean canMove = gameLogic.hasAnyLegalMove(currentPlayerIsRed);
        if (!canMove) {
            return true;
        }
        return false;
    }   

    /**
     * 获取某个棋子的所有合法移动位置
     */
    private List<Point> getValidMoves(AbstractPiece piece) {
        List<Point> validMoves = new ArrayList<>();
        
        if (piece == null) return validMoves;
        
        // 遍历整个棋盘，找出所有合法移动
        for (int r = 0; r < ChessBoardModel.getRows(); r++) {
            for (int c = 0; c < ChessBoardModel.getCols(); c++) {
                if (piece.canMoveTo(r, c, model)) {
                    validMoves.add(new Point(c, r)); 
                }
            }
        }
        
        return validMoves;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (boardImage != null) g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        drawPieces(g2d);
        
        // 如果有选中的棋子，高亮所有合法移动位置
        AbstractPiece selectedPiece = gameLogic.getSelectedPiece();
        if (selectedPiece != null) {
            drawValidMoveHints(g2d, selectedPiece);
        }
    }

    /**
     * 绘制合法移动位置的提示
     */
    private void drawValidMoveHints(Graphics2D g, AbstractPiece selectedPiece) {
        List<Point> validMoves = getValidMoves(selectedPiece);
        
        for (Point move : validMoves) {
            int x = MARGIN + move.x * CELL_SIZE; // move.x 是列
            int y = MARGIN + move.y * CELL_SIZE; // move.y 是行
            
            AbstractPiece targetPiece = model.getPieceAt(move.y, move.x);
            
            if (targetPiece == null) {
                // 空位：画半透明的小圆点
                g.setColor(new Color(0, 255, 0, 150));
                g.fillOval(x - 8, y - 8, 16, 16);
            } else {
                // 敌方棋子：画半透明的圆圈
                g.setColor(new Color(255, 0, 0, 100));
                g.setStroke(new BasicStroke(3));
                g.drawOval(x - PIECE_RADIUS - 5, y - PIECE_RADIUS - 5, 
                          (PIECE_RADIUS) * 2-3, (PIECE_RADIUS) * 2-3);
            }
        }
    }

    /**
     * 根据棋子获取对应的图片
     */
    private Image getImageForPiece(AbstractPiece piece) {
        String name = piece.getName();
        boolean isRed = piece.isRed();
        String imageName = "";

        if (name.equals("帅")) imageName = "r_j";
        else if (name.equals("将")) imageName = "b_j";
        else if (name.equals("车") && isRed) imageName = "r_c";
        else if (name.equals("车") && !isRed) imageName = "b_c";
        else if (name.equals("马") && isRed) imageName = "r_m";
        else if (name.equals("马") && !isRed) imageName = "b_m";
        else if (name.equals("相")) imageName = "r_x";
        else if (name.equals("象")) imageName = "b_x";
        else if (name.equals("仕")) imageName = "r_s";
        else if (name.equals("士")) imageName = "b_s";
        else if (name.equals("炮") && isRed) imageName = "r_p";
        else if (name.equals("炮") && !isRed) imageName = "b_p";
        else if (name.equals("兵")) imageName = "r_z";
        else if (name.equals("卒")) imageName = "b_z";


        return ImageLoader.loadImage(imageName + ".png");
    }


    private void drawPieces(Graphics2D g) {
        AbstractPiece selectedPiece = gameLogic.getSelectedPiece();
        
        for (AbstractPiece piece : model.getPieces()) {

            int x = MARGIN + piece.getCol() * CELL_SIZE;
            int y = MARGIN + piece.getRow() * CELL_SIZE;
            boolean isSelected = (piece == selectedPiece);

            drawSinglePiece(g, piece, x, y, isSelected);
        }
    }

    private void drawSinglePiece(Graphics2D g, AbstractPiece piece, int x, int y, boolean isSelected) {
        
        Image img = getImageForPiece(piece);
        int imgSize = PIECE_RADIUS * 2; // 棋子直径
        
        if (img != null) {
            // 绘制图片
            g.drawImage(img, x - PIECE_RADIUS+3, y - PIECE_RADIUS+1, imgSize+1, imgSize, this);
        } else {
            g.setColor(new Color(245, 222, 179));
            g.fillOval(x - PIECE_RADIUS, y - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
            
            // 绘制边框
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawOval(x - PIECE_RADIUS, y - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);

            // 如果被选中，绘制蓝色边框
            if (isSelected) {
                drawCornerBorders(g, x, y);
            }

            // 绘制棋子文字
            if (piece.isRed()) {
                g.setColor(new Color(200, 0, 0));
            } else {
                g.setColor(Color.BLACK);
            }
            g.setFont(new Font("楷体", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(piece.getName());
            int textHeight = fm.getAscent();
            g.drawString(piece.getName(), x - textWidth / 2, y + textHeight / 2 - 2);
        }

        // 绘制选中框 (保持不变)
        if (isSelected) {
            drawCornerBorders(g, x, y);
        }
    }

    private void drawCornerBorders(Graphics2D g, int centerX, int centerY) {
        g.setColor(new Color(0, 100, 255));
        g.setStroke(new BasicStroke(3));

        int cornerSize = 32;
        int lineLength = 12;

        // 左上角
        g.drawLine(centerX - cornerSize, centerY - cornerSize,
                centerX - cornerSize + lineLength, centerY - cornerSize);
        g.drawLine(centerX - cornerSize, centerY - cornerSize,
                centerX - cornerSize, centerY - cornerSize + lineLength);

        // 右上角
        g.drawLine(centerX + cornerSize, centerY - cornerSize,
                centerX + cornerSize - lineLength, centerY - cornerSize);
        g.drawLine(centerX + cornerSize, centerY - cornerSize,
                centerX + cornerSize, centerY + cornerSize - lineLength);

        // 左下角
        g.drawLine(centerX - cornerSize, centerY + cornerSize,
                centerX - cornerSize + lineLength, centerY + cornerSize);
        g.drawLine(centerX - cornerSize, centerY + cornerSize,
                centerX - cornerSize, centerY + cornerSize - lineLength);

        // 右下角
        g.drawLine(centerX + cornerSize, centerY + cornerSize,
                centerX + cornerSize - lineLength, centerY + cornerSize);
        g.drawLine(centerX + cornerSize, centerY + cornerSize,
                centerX + cornerSize, centerY + cornerSize - lineLength);
    }
}