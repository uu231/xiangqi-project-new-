package edu.sustech.xiangqi.ui;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {

    private static final Map<String, Image> imageCache = new HashMap<>();

    /**
     * 加载图片，并使用缓存
     */
    public static Image loadImage(String imageName) {
        // 检查缓存
        if (imageCache.containsKey(imageName)) {
            return imageCache.get(imageName);
        }

        // 从 resources/images/ 目录加载
        try (InputStream is = ImageLoader.class.getResourceAsStream("/images/" + imageName)) {
            if (is == null) {
                throw new IOException("Image not found: " + imageName);
            }
            Image image = ImageIO.read(is);
            // 放入缓存
            imageCache.put(imageName, image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 加载失败
        }
    }
}