package artGame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Provides methods to help with drawing text on screen
 * @author Tim King 300282037
 *
 */
public class FontHandler {
	
	/**
	 * Paints the given String onto the given BufferedImage in the Color col
	 * @param old Image to overlay text onto
	 * @param s String to draw
	 * @param col Color to use for text
	 * @return
	 */
    public static BufferedImage process(BufferedImage old, String s, Color col) {
        int w = old.getWidth();
        int h = old.getHeight();
        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(old, 0, 0, null);
        g2d.setPaint(col);
        g2d.setFont(new Font("Arial", Font.ITALIC, 24));
 
        FontMetrics fm = g2d.getFontMetrics();
        int x = img.getWidth() - fm.stringWidth(s) - 5;
        int y = fm.getHeight();
        g2d.drawString(s, x, y);
        g2d.dispose();
        return img;
    }
	
}

