package com.fenglin.springboottest.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图形验证码服务：4 位随机码，PNG 图片以 base64 下发
 * 内存存储，5 分钟过期，一次性校验（验证后立即销毁）
 */
@Service
public class CaptchaService {

    private static final long EXPIRE_MILLIS = 5 * 60 * 1000L;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 去掉易混淆字符
    private static final int WIDTH = 132;
    private static final int HEIGHT = 46;

    private final Random random = new Random();
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    private static class Entry {
        final String code;
        final long expireAt;

        Entry(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    /**
     * 生成验证码，返回 captchaId 和 base64 图片（data URL）
     */
    public Map<String, String> generate() {
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        String code = sb.toString();
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        store.put(captchaId, new Entry(code, System.currentTimeMillis() + EXPIRE_MILLIS));

        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("image", "data:image/png;base64," + Base64.getEncoder().encodeToString(render(code)));
        return result;
    }

    /**
     * 校验验证码（一次性，无论对错都销毁）
     */
    public boolean verify(String captchaId, String input) {
        if (captchaId == null || input == null) {
            return false;
        }
        Entry entry = store.remove(captchaId);
        return entry != null
                && entry.expireAt >= System.currentTimeMillis()
                && entry.code.equalsIgnoreCase(input.trim());
    }

    /**
     * 用 AWT 绘制验证码图片（粉色系 + 干扰线）
     */
    private byte[] render(String code) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景
        g.setColor(new Color(255, 245, 248));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 干扰线
        Color[] lineColors = {
                new Color(255, 168, 201), new Color(201, 163, 255),
                new Color(158, 197, 255), new Color(127, 227, 184)
        };
        for (int i = 0; i < 6; i++) {
            g.setColor(lineColors[random.nextInt(lineColors.length)]);
            g.setStroke(new BasicStroke(1.4f));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        // 噪点
        for (int i = 0; i < 40; i++) {
            g.setColor(lineColors[random.nextInt(lineColors.length)]);
            g.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2, 2);
        }

        // 字符：逐个随机旋转、偏移
        Color[] textColors = {
                new Color(231, 90, 146), new Color(232, 64, 79),
                new Color(150, 90, 220), new Color(70, 140, 220)
        };
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int charWidth = 26;
        int startX = (WIDTH - charWidth * code.length()) / 2 + 4;
        for (int i = 0; i < code.length(); i++) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(textColors[random.nextInt(textColors.length)]);
            int x = startX + i * charWidth;
            int y = (HEIGHT + fm.getAscent() - fm.getDescent()) / 2 + random.nextInt(7) - 3;
            g2.rotate(Math.toRadians(random.nextInt(41) - 20), x, y - 10);
            g2.drawString(String.valueOf(code.charAt(i)), x, y);
            g2.dispose();
        }

        g.dispose();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("验证码图片生成失败", e);
        }
    }
}
