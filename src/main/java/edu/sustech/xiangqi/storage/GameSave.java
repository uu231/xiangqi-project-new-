package edu.sustech.xiangqi.storage;
import java.io.Serializable;
import java.time.LocalDateTime;
public class GameSave implements Serializable {
    private String saveName;
    private String username;
    private String boardState;
    private String currentPlayer;
    private LocalDateTime savetime;

    public GameSave(){}
    public GameSave(String saveName, String username, String boardState, String currentPlayer) {
        this.saveName = saveName;
        this.username = username;
        this.boardState = boardState;
        this.currentPlayer = currentPlayer;
        this.savetime = LocalDateTime.now();
    }

    public String getSaveName() {
        return saveName;
    }
    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getBoardState() {
        return boardState;
    }
    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }
    public String getCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public LocalDateTime getSavetime() {
        return savetime;
    }
    public void setSavetime(LocalDateTime savetime) {
        this.savetime = savetime;
    }
}
