package edu.sustech.xiangqi.ui;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundPlayer {

    // (注意) 我们需要持有 MediaPlayer 的引用，否则它可能在播放完之前被GC回收
    private static MediaPlayer selectPlayer;
    private static MediaPlayer movePlayer;

    // 静态初始化块，用于预加载声音
    static {
        // 确保 JavaFX 已经通过 XiangqiApplication.java 中的 new JFXPanel() 初始化
        Platform.runLater(() -> {
            try {
                URL selectUrl = SoundPlayer.class.getResource("/sounds/select.mp3"); // 假设你的文件叫 select.wav
                URL moveUrl = SoundPlayer.class.getResource("/sounds/move.mp3"); // 假设你的文件叫 move.wav

                if (selectUrl != null) {
                    selectPlayer = new MediaPlayer(new Media(selectUrl.toExternalForm()));
                } else {
                    System.err.println("Warning: /sounds/select.wav not found.");
                }

                if (moveUrl != null) {
                    movePlayer = new MediaPlayer(new Media(moveUrl.toExternalForm()));
                } else {
                    System.err.println("Warning: /sounds/move.wav not found.");
                }
                
            } catch (Exception e) {
                System.err.println("Failed to load sounds: " + e.getMessage());
            }
        });
    }

    public static void playSelectSound() {
        if (selectPlayer != null) {
            Platform.runLater(() -> {
                selectPlayer.stop(); // 停止当前播放，重新开始
                selectPlayer.play(); // 播放
            });
        }
    }

    public static void playMoveSound() {
        if (movePlayer != null) {
             Platform.runLater(() -> {
                movePlayer.stop();
                movePlayer.play();
            });
        }
    }
}