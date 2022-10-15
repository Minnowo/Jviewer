package Graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageUtil 
{
	/**
	 * rotates an image by k*90 amount, otherwise returns null
	 * 90  = rotate right  
	 * 180 = flip
	 * 270 = rotate left 
	 */
	public static BufferedImage roateMod90(BufferedImage img, int degree)
	{
		final int w = img.getWidth();
	    final int h = img.getHeight();
	    final double rads = Math.toRadians(degree);
	    
		if(degree %  180 == 0)
		{
		    final BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		    final Graphics2D g2d = rotated.createGraphics();
		    final AffineTransform at = new AffineTransform();
	
		    at.rotate(rads, w / 2, h / 2);
		    g2d.setTransform(at);
		    g2d.drawImage(img, 0, 0, null);
		    g2d.dispose();
	
		    return rotated;
		}
		
		if(degree % 90 == 0)
		{		
		    final BufferedImage rotated = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
		    final Graphics2D g2d = rotated.createGraphics();
		    final AffineTransform at = new AffineTransform();
		    
		    at.translate((h - w) / 2, (w - h) / 2);
	
		    at.rotate(rads, w / 2, h / 2);
		    g2d.setTransform(at);
		    g2d.drawImage(img, 0, 0, null);
		    g2d.dispose();
	
		    return rotated;
		}
			
		return null;
	}

	
	public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) 
	{
	    final double rads = Math.toRadians(angle);
	    final double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
	    final int w = img.getWidth();
	    final int h = img.getHeight();
	    final int newWidth = (int) Math.floor(w * cos + h * sin);
	    final int newHeight = (int) Math.floor(h * cos + w * sin);

	    final BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	    final Graphics2D g2d = rotated.createGraphics();
	    final AffineTransform at = new AffineTransform();
	    
	    at.translate((newWidth - w) / 2, (newHeight - h) / 2);

	    int x = w / 2;
	    int y = h / 2;

	    at.rotate(rads, x, y);
	    g2d.setTransform(at);
	    g2d.drawImage(img, 0, 0, null);
	    g2d.dispose();

	    return rotated;
	}
	
	public static void flipHorizontal(BufferedImage image)
	{
		// this graphics2D thing works like 30% of the time??? 
		// i think the width / height of the image affects it 
//		Graphics2D g2 = image.createGraphics();
//
//		g2.drawImage(image, image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
//		
//		g2.dispose();
		

		// this is waaaay slower but at least it's consitent 
		final int w = image.getWidth();
	    final int h = image.getHeight();
	    final int hw = image.getWidth() / 2;
	    
	    for (int y = 0; y < h; y++)
	        for (int x = 0; x < hw; x++)
	        {
	            int tmp = image.getRGB(x, y);
	            image.setRGB(x, y, image.getRGB(w - x - 1, y));
	            image.setRGB(w - x - 1, y, tmp);
	        }
	}
	
	public static void flipVertical(BufferedImage image)
	{
		// this is supposed to work but it only flips half the image for some reason
		// also it works sometimes ????
//		Graphics2D g2 = image.createGraphics();
//		
//		g2.drawImage(image,  0, image.getHeight(), image.getWidth(), -image.getHeight(), null);
//
//		g2.dispose();

		// this is waaaay slower but at least it's consitent 
	    final int w = image.getWidth();
	    final int h = image.getHeight();
	    final int hh = image.getHeight() / 2;
	    
	    for (int x = 0; x < w; x++)
	        for (int y = 0; y < hh; y++)
	        {
	            int tmp = image.getRGB(x, y);
	            image.setRGB(x, y, image.getRGB(x, h - y - 1));
	            image.setRGB(x, h - y - 1, tmp);
	        }
	}
}










