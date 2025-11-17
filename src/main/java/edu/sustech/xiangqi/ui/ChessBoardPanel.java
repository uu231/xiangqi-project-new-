package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.GameLogicModel;
import edu.sustech.xiangqi.model.Move;

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
    private Image selectFrameImage;

    private AIEngine aiEngine;
    private boolean isAIGame = false; // 游戏模式
    private boolean boardEnabled = true; // 控制棋盘是否可点击
    private boolean isAISimulating = false;

    /** 存储AI思考时，界面应该显示的棋子列表快照 */
    private List<AbstractPiece> piecesSnapshotForAI = null;
    /** 存储AI思考时，界面应该显示的上一步移动快照 */
    private Move lastMoveSnapshotForAI = null;

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
        this.selectFrameImage = ImageLoader.loadImage("r_box.png");
        this.aiEngine = new AIEngine(this.gameLogic, this, 4);
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
        // 如果棋盘被禁用 (AI正在思考)，则不响应
        if (!boardEnabled) {
            return;
        }

        // 如果是AI局，且轮到黑方(AI)走棋，则不响应
        if (isAIGame && !gameLogic.isRedTurn()) {
            return;
        }

        int col = Math.round((float)(x - MARGIN) / CELL_SIZE);
        int row = Math.round((float)(y - MARGIN) / CELL_SIZE);

        if (!model.isValidPosition(row, col)) {
            return;
        }

        AbstractPiece selectedPiece = gameLogic.getSelectedPiece();

        if (selectedPiece == null) {
            // 尝试选中棋子
            if (gameLogic.selectPiece(row, col)) {
                repaint();
            }
        } else {
            // 尝试移动
            if (gameLogic.tryMove(row, col)) {
                repaint();     

                // 检查游戏是否结束
                if (checkGameOver()) {
                    showGameOverDialog();
                } else if (isAIGame && !gameLogic.isRedTurn()) {
                    // **** 使用 model 对象加锁 ****
                    synchronized (model) {
                        // 必须在AI启动前，复制一份当前的棋子列表
                        this.piecesSnapshotForAI = new ArrayList<>(model.getPieces()); //
                    } // **** 解锁 ****

                    // 必须在AI启动前，获取最后一步（即玩家刚走的这一步）
                    this.lastMoveSnapshotForAI = gameLogic.getLastMove(); //

                    // (新) 如果是AI局，且轮到AI(黑方)走棋
                    aiEngine.performComputerMove(); //
                }
            } else {
                // 移动失败（非法移动）
                if (gameLogic.selectPiece(row, col)) {
                    repaint();
                } else {
                    gameLogic.cancelSelection();
                    repaint();
                }
            }
        }
    }

    private void drawPiecesFromList(Graphics2D g, List<AbstractPiece> pieces) {
        if (pieces == null) return;
        
        // **** 使用 model 对象加锁 ****
        // 保护浅快照在绘制期间不被AI线程修改
        synchronized (model) {
            for (AbstractPiece piece : pieces) { 
                int x = MARGIN + piece.getCol() * CELL_SIZE; //
                int y = MARGIN + piece.getRow() * CELL_SIZE; //
                drawSinglePiece(g, piece, x, y, false);
            }
        } // **** 解锁 ****
    }

    public void clearAIPiecesSnapshot() {
        this.piecesSnapshotForAI = null;
        this.lastMoveSnapshotForAI = null;
    }

    public void showGameOverDialog() {
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
        // (新) 我们让 gameLogic 检查并更新状态
        return gameLogic.checkAndUpdateGameState();
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

        // 绘制棋盘背景
        if (boardImage != null) g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        
        // 获取当前选中的棋子
        AbstractPiece selectedPiece = gameLogic.getSelectedPiece();
        
        if (isAISimulating) {
            // 1. AI 正在思考：绘制【快照】中的棋子
            drawPiecesFromList(g2d, this.piecesSnapshotForAI);

            // 2. 绘制半透明遮罩
            g2d.setColor(new Color(0, 0, 0, 100)); // 半透明黑色
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // 3. 使用【快照】中的 lastMove 在遮罩上重新绘制提示
            if (this.lastMoveSnapshotForAI != null) {
                AbstractPiece piece = this.lastMoveSnapshotForAI.getMovedPiece();
                int x = MARGIN + piece.getCol() * CELL_SIZE;
                int y = MARGIN + piece.getRow() * CELL_SIZE;
                
                // 重新绘制这个棋子 (不带选中框)
                drawSinglePiece(g2d, piece, x, y, false);
            
                int fromX = MARGIN + this.lastMoveSnapshotForAI.getFromCol() * CELL_SIZE;
                int fromY = MARGIN + this.lastMoveSnapshotForAI.getFromRow() * CELL_SIZE;
                g.setColor(new Color(255, 255, 255, 200));
                g.fillOval(fromX - 8, fromY - 8, 16, 16);
            }
            
            // 4. 绘制 "思考中" 文字
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "AI 正在思考...";
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);

        } else {
            // AI 没在思考，正常绘制【实时】棋子
            drawPieces(g2d); 
            
            // 正常绘制【实时】提示
            Move lastMove = gameLogic.getLastMove(); // 获取实时的
            
            if (selectedPiece != null) {
                drawValidMoveHints(g2d, selectedPiece);
            }
            if (lastMove != null) {
                drawLastMoveHints(g2d);
            }
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
                g.drawOval(x - PIECE_RADIUS - 1, y - PIECE_RADIUS - 3, 
                          (PIECE_RADIUS) * 2-3, (PIECE_RADIUS) * 2-3);
            }
        }
    }

    /**
     * 绘制上一步move
     */
    private void drawLastMoveHints(Graphics2D g) {
        Move lastMove = gameLogic.getLastMove();
        if (lastMove == null) return; 

        int fromX = MARGIN + lastMove.getFromCol() * CELL_SIZE;
        int fromY = MARGIN + lastMove.getFromRow() * CELL_SIZE;
        
        AbstractPiece movedPiece = lastMove.getMovedPiece();
        int toX = MARGIN + movedPiece.getCol() * CELL_SIZE;
        int toY = MARGIN + movedPiece.getRow() * CELL_SIZE;
        
        // 在起始点画半透明的小圆点
        g.setColor(new Color(255, 255, 255, 200));
        g.fillOval(fromX - 8, fromY - 8, 16, 16);

        // 在结束点画半透明的圆圈
        g.setColor(new Color(255, 255, 255, 200));
        g.setStroke(new BasicStroke(3));
        g.drawOval(toX - PIECE_RADIUS-1, toY - PIECE_RADIUS-3, 
                    PIECE_RADIUS * 2-3, PIECE_RADIUS * 2-3);    
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
        
        // **** 使用 model 对象加锁 ****
        // 保护实时绘制
        List<AbstractPiece> piecesSnapshot;
        synchronized (model) { 
            piecesSnapshot = new ArrayList<>(model.getPieces()); //
            
            for (AbstractPiece piece : piecesSnapshot) { 
                int x = MARGIN + piece.getCol() * CELL_SIZE; //
                int y = MARGIN + piece.getRow() * CELL_SIZE; //
                boolean isSelected = (piece == selectedPiece);

                drawSinglePiece(g, piece, x, y, isSelected);
            }
        } // **** 解锁 ****
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


            if (isSelected && selectFrameImage != null) {
                // 让选择框比棋子稍大一点
                int selectRadius = PIECE_RADIUS + 1; 
                int selectSize = selectRadius * 2;
                
                // 以(x, y)为中心绘制选中框图片
                g.drawImage(selectFrameImage, x - selectRadius, y - selectRadius, selectSize, selectSize, this);
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

        if (isSelected && selectFrameImage != null) {
            // 让选择框比棋子稍大一点
            int selectRadius = PIECE_RADIUS + 1; 
            int selectSize = selectRadius * 2;
            
            // 以(x, y)为中心绘制选中框图片
            g.drawImage(selectFrameImage, x - selectRadius, y - selectRadius, selectSize, selectSize, this);
        }
    }

    /**
     * 设置游戏模式
     */
    public void setGameMode(boolean isAIGame) {
        this.isAIGame = isAIGame;
        // 切换模式时自动重启游戏
        gameLogic.restart();
        repaint();
    }


    /**
     * 设置棋盘是否响应点击
     */
    public void setBoardEnabled(boolean enabled) {
        this.boardEnabled = enabled;
    }

    /**
     * 设置 AI 是否正在模拟 (防止UI重绘)
     */
    public void setAISimulating(boolean simulating) {
        this.isAISimulating = simulating;
    }

    public boolean isAIGame() {
        return this.isAIGame;
    }
    

}