/**
 * 
 */
package it.unical.logic_santos.editor.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author Agostino
 *
 */
public class ImagePreview extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int IMAGE_WIDTH = 100;
	public static final int IMAGE_HEIGHT = 100;
	
	BufferedImage imagePreview=null;
	
	public ImagePreview(final String imageName) {
		try {
			imagePreview = ImageIO.read(new File(imageName));
			this.setSize(new Dimension(ImagePreview.IMAGE_WIDTH, ImagePreview.IMAGE_HEIGHT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(imagePreview, 0, 0, null);
	}
	
	
}
