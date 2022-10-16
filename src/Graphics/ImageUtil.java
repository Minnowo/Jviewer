package Graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import Configuration.ImageMagick;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import Graphics.Imaging.Exceptions.RequiresMagickException;

public class ImageUtil 
{	
	protected final static Logger logger = Logger.getLogger(ImageUtil.class.getName());
	
	
	/**
	 * Gets the optimal image type for the given transparency
	 * @param transparency
	 * @return
	 */
	public static int getOptimalType(int transparency)
	{
		if(transparency == Transparency.OPAQUE)
			return BufferedImage.TYPE_INT_RGB;
		
		
		// return BufferedImage.TYPE_INT_ARGB_PRE;
		return BufferedImage.TYPE_INT_ARGB;
	}
	
	public static BufferedImage createOptimalImage2(BufferedImage src, int width, int height) throws IllegalArgumentException, NullPointerException
	{
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("width [" + width + "] and height [" + height + "] must be > 0");
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice device = env.getDefaultScreenDevice();
	    GraphicsConfiguration config = device.getDefaultConfiguration();

	    return config.createCompatibleImage(width, height, src.getTransparency());
	}
	
	
	public static BufferedImage createOptimalImage(BufferedImage src, int width, int height) throws IllegalArgumentException, NullPointerException
	{
		if (width <= 0 || height <= 0)
			
			throw new IllegalArgumentException("width [" + width + "] and height [" + height + "] must be > 0");

		return new BufferedImage(width, height, getOptimalType(src.getTransparency()));
	}
	
	public static BufferedImage createOptimalImageFrom(BufferedImage src) throws IllegalArgumentException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), getOptimalType(src.getTransparency()));

		Graphics g = result.getGraphics();
		
		g.drawImage(src, 0, 0, null);
		
		g.dispose();

		return result;
	}
	
	public static BufferedImage createOptimalImageFrom2(BufferedImage src) throws IllegalArgumentException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
		BufferedImage result = createOptimalImage2(src, src.getWidth(), src.getHeight());

		Graphics g = result.getGraphics();
		
		g.drawImage(src, 0, 0, null);
		
		g.dispose();

		return result;
	}
	
	/**
	 * Rotates by the given amount filling the rest with transparency<br/>
	 * @param src
	 */
	public static BufferedImage rotateImageByDegrees(BufferedImage src, double angle) throws IllegalArgumentException, ImagingOpException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
	    final double rads = Math.toRadians(angle);
	    final double sin = Math.abs(Math.sin(rads)); 
	    final double cos = Math.abs(Math.cos(rads));
	    final int w = src.getWidth();
	    final int h = src.getHeight();
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
	    g2d.drawImage(src, 0, 0, null);
	    g2d.dispose();

	    return rotated;
	}
	
	
	/**
	 * Mirrors the image horizontally over the y axis without creating a new image via the use of setRGB and getRGB<br/>
	 * This is really slow compared to graphics mirroring
	 * @param src
	 */
	public static void mirrorHorizontal(BufferedImage src) throws IllegalArgumentException
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
		final int w = src.getWidth();
	    final int h = src.getHeight();
	    final int hw = w / 2;
	    
	    for (int y = 0; y < h; y++)
	        for (int x = 0; x < hw; x++)
	        {
	            int tmp = src.getRGB(x, y);
	            src.setRGB(x, y, src.getRGB(w - x - 1, y));
	            src.setRGB(w - x - 1, y, tmp);
	        }
	}
	
	
	/**
	 * Mirrors the image vertically over the x axis without creating a new image via the use of setRGB and getRGB<br/>
	 * This is really slow compared to graphics mirroring
	 * @param src
	 */
	public static void mirrorVertical(BufferedImage src) throws IllegalArgumentException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
	    final int w = src.getWidth();
	    final int h = src.getHeight();
	    final int hh = h / 2;
	    
	    for (int x = 0; x < w; x++)
	        for (int y = 0; y < hh; y++)
	        {
	            int tmp = src.getRGB(x, y);
	            src.setRGB(x, y, src.getRGB(x, h - y - 1));
	            src.setRGB(x, h - y - 1, tmp);
	        }
	}


	
	public static BufferedImage rotate(BufferedImage src, byte rotation) throws IllegalArgumentException, ImagingOpException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		

		final AffineTransform tx = new AffineTransform();
		
		int newWidth = src.getWidth();
		int newHeight = src.getHeight();
		
		switch (rotation) 
		{
			default:
				throw new IllegalArgumentException("invalid rotation");
				
			case Rotation.CW_90:

				newWidth = src.getHeight();
				newHeight = src.getWidth();
	
				tx.translate(newWidth, 0);
				tx.quadrantRotate(1);
	
				break;

			case Rotation.CCW_90:
				
				newWidth = src.getHeight();
				newHeight = src.getWidth();
	
				tx.translate(0, newHeight);
				tx.quadrantRotate(3);
				break;

			case Rotation.CW_180:
				tx.translate(newWidth, newHeight);
				tx.quadrantRotate(2);
				break;
	
			case Rotation.MIRROR_HORIZONTAL:
				tx.translate(newWidth, 0);
				tx.scale(-1d, 1d);
				break;
	
			case Rotation.MIRROR_VERTICAL:
				tx.translate(0, newHeight);
				tx.scale(1d, -1d);
				break;
		}

		BufferedImage result = createOptimalImage(src, newWidth, newHeight);
		
		Graphics2D g2 = (Graphics2D) result.createGraphics();

		g2.drawImage(src, tx, null);
		
		g2.dispose();

		return result;
	}
	
	

    public static void saveImage(BufferedImage buf, String path) throws ImageUnsupportedException
    {
    	saveImage(buf, new File(path));
    }
    
    public static void saveImage(BufferedImage buf, File path) throws ImageUnsupportedException
    {
    	if(buf == null)
    		return; 
    	
    	if(ImageMagick.useImageMagick)
    	{
    		try 
    		{
				saveImageWithMagick(buf, path);
				return;
			} 
    		catch (IOException | InterruptedException | IM4JavaException e) 
    		{
    			logger.log(Level.WARNING, "Failed to save image %s using ImageMagick:\nMessage: %s".formatted(path.getAbsolutePath(), e.getMessage()), e);
			}
    	}
    	
    	final String ext = Util.StringUtil.getFileExtension(path, false);
    	final byte imgFormat = ImageFormat.getFromFileExtension(ext);
    	
    	if(imgFormat == -1)
    		throw new ImageUnsupportedException("The image format '%s' is was not recognized".formatted(ext));
    	
    	if(imgFormat == ImageFormat.WEBP)
    		throw new RequiresMagickException("Saving with the WebP image format requires the use of ImageMagick");
    	
    	try 
    	{
    		ImageIO.write(buf, ImageFormat.getFileExtension(imgFormat), path);
    	}
    	catch (IOException e) 
    	{
            logger.log(Level.WARNING, "Failed to save image %s with ImageIO.write:\nMessage: %s".formatted(path.getAbsolutePath(), e.getMessage()), e);
    	}
	}
    
    
    
    public static void saveImageWithMagick(BufferedImage buf, File path) throws IOException, InterruptedException, IM4JavaException
    {
    	saveImageWithMagick(buf, path.getAbsolutePath());
    }
    
    
    public static void saveImageWithMagick(BufferedImage buf, String path) throws IOException, InterruptedException, IM4JavaException
    {
    	if(buf == null)
    		return;
    	
    	IMOperation op = new IMOperation();
    	op.addImage();                        // input
    	op.addImage(path);                    // output

    	ConvertCmd convert = new ConvertCmd();
   
		convert.run(op , buf);
    }
    
    
    public static BufferedImage loadImage(File path)
    {
    	return loadImage(path.getAbsolutePath());
    }
    
    public static BufferedImage loadImage(String path)
	{
		File path_ = new File(path);
		
		if(!path_.exists())
			return null;
		
		if(ImageMagick.useImageMagick) 
		{
			try 
			{
				return loadImageWithMagick(path);
			} 
			catch (IOException | InterruptedException | IM4JavaException e) 
			{
				logger.log(Level.WARNING, "Failed to read image %s using ImageMagick:\nMessage: %s".formatted(path, e.getMessage()), e);
			}
		}
	
		
		try 
		{
		    return ImageIO.read(path_);
		} 
		catch (IOException e) 
		{
			logger.log(Level.WARNING, "Failed to read image %s using ImageIO.read:\nMessage: %s".formatted(path, e.getMessage()), e);
			return null;
		}
	}
	

	
 		
	public static BufferedImage loadImageWithMagick(String path ) throws IOException, InterruptedException, IM4JavaException
	{
		// TODO: read the header of the file instead of the extension
		final String ext = Util.StringUtil.getFileExtension(new File(path));
		final byte imageFormat = ImageFormat.getFromFileExtension(ext);
		
		IMOperation op = new IMOperation();

		if(ImageMagick.readRequiresMergeLayers(imageFormat))
		{
			// input image path
			op.addImage(path + "[0]");			
		}
		else 
		{
			// input image path
			op.addImage(path);
		}

		// set image output type into stdout, (bmp seems fastest but slow to render)
		op.addImage(ImageMagick.getImageDecodeFormat() + ":-"); 


		// set up command
		ConvertCmd convert = new ConvertCmd();
		Stream2BufferedImage s2b = new Stream2BufferedImage();
		convert.setOutputConsumer(s2b);

		// run command and extract BufferedImage from OutputConsumer
		convert.run(op);

		BufferedImage b = ImageUtil.createOptimalImageFrom2(s2b.getImage());

		return b;
	}

	public static BufferedImage convertGreyscaleFast(BufferedImage src)
	{
		ImageFilter filter = new GrayFilter(true, 50);  
		ImageProducer producer = new FilteredImageSource(src.getSource(), filter);  
		Image mage = Toolkit.getDefaultToolkit().createImage(producer);
		
		return (BufferedImage)mage;
	}

	public static void convertInverse(BufferedImage img)
	{
	    for (int x = 0; x < img.getWidth(); ++x)
	    for (int y = 0; y < img.getHeight(); ++y)
	    {
	        int rgb = img.getRGB(x, y);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));
	        
	        img.setRGB(x, y, ((255 - r) << 16) + ((255 - g) << 8) + (255 - b));
	    }
	}
	

	public static void convertGreyscale(BufferedImage img)
	{
		final double gsrm = 0.3; // 0.21
		
		final double gsgm = 0.59; // 0.71
		
		final double gsbm = 0.11; // 0.071
		
	    for (int x = 0; x < img.getWidth(); ++x)
	    for (int y = 0; y < img.getHeight(); ++y)
	    {
	        int rgb = img.getRGB(x, y);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));

	        byte grey = (byte)((r * gsrm) + (g * gsgm) + (b * gsbm));
	        
	        img.setRGB(x, y, (grey << 16) + (grey << 8) + grey);
	    }
	}
}










