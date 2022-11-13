package Graphics;

import java.awt.Color;
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
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GrayFilter;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import Configuration.ImageMagick;
import Graphics.Imaging.IMAGE;
import Graphics.Imaging.ImageBase;
import Graphics.Imaging.ImageDetector;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import Graphics.Imaging.Gif.GIF;
import Graphics.Imaging.Gif.GIF2;
import Graphics.Imaging.Gif.GifEncoder;
import Util.Logging.LogUtil;

public class ImageUtil 
{	
	protected final static Logger logger = LogUtil.getLogger(ImageUtil.class.getName());
	
	
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
	public static BufferedImage rotateImageByDegrees(Image src, double angle) throws IllegalArgumentException, ImagingOpException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
	    final double rads = Math.toRadians(angle);
	    final double sin = Math.abs(Math.sin(rads)); 
	    final double cos = Math.abs(Math.cos(rads));
	    final int w = src.getWidth(null);
	    final int h = src.getHeight(null);
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
	
	public static void mirrorHorizontal2(BufferedImage src) throws IllegalArgumentException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
		final int w = src.getWidth();
	    final int h = src.getHeight();
	    final int hw = w / 2;
	    
	    DataBufferInt db = (DataBufferInt)src.getRaster().getDataBuffer();
	    
	    for (int y = 0; y < h; y++)
	        for (int x = 0; x < hw; x++)
	        {
	        	int i1 = x + w*y;
	        	int i2 = (w - x - 1) + w*y;

	        	int tmp = db.getElem(i1);
	            db.setElem(i1, db.getElem(i2));
	            db.setElem(i2, tmp);
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
	
	/*
	 * much faster implementation of mirrorVertical
	 */
	public static void mirrorVertical2(BufferedImage src) throws IllegalArgumentException 
	{
		if (src == null)
			throw new IllegalArgumentException("src cannot be null");
		
	    final int w = src.getWidth();
	    final int h = src.getHeight();
	    final int hh = h / 2;
	    
	    DataBufferInt db = (DataBufferInt)src.getRaster().getDataBuffer();
	    
	    for (int x = 0; x < w; x++)
	        for (int y = 0; y < hh; y++)
	        {
	        	int i1 = x + w*y;
	        	int i2 = x + w*(h - y - 1);

	        	int tmp = db.getElem(i1);
	            db.setElem(i1, db.getElem(i2));
	            db.setElem(i2, tmp);
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
	
	

    public static boolean saveImage(ImageBase buf, String path) throws ImageUnsupportedException
    {
    	return saveImage(buf, new File(path));
    }
    
    public static boolean saveImage(ImageBase buf, File path) throws ImageUnsupportedException
    {
    	if(buf == null)
    		return false; 
    	
    	final String ext = Util.StringUtil.getFileExtension(path, false);
    	final byte imgFormat = ImageFormat.getFromFileExtension(ext);
    	
    	logger.info("saving image %s, detected file to save as (%d) (%s)".formatted(path, imgFormat, ext));
    	
    	// TODO: fix this 
    	switch (imgFormat)
    	{
    		case ImageFormat.GIF:
    			
    			if(buf.GetImageFormat() == ImageFormat.GIF)
    			{
    				return buf.save(path);
    			}
    			
    			// TODO: allow saving with magick aswell
				GifEncoder ge = new GifEncoder();
				
				ge.start(path.getAbsolutePath());
				ge.setRepeat(0);
				ge.setTransparent(null);
				ge.addFrame(buf.getBuffered());
				ge.finish();
    			
    			return true;
    			
    		default:
    			
    			return buf.save(path);
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
    
    
    public static ImageBase loadImage(File path)
    {
    	return loadImage(path.getAbsolutePath());
    }
    
    public static ImageBase loadImage(String path)
	{
    	final byte imageff = ImageDetector.getImageFormat(path);

		logger.log(Level.INFO, "detected image format (%d) (%s)".formatted(imageff, ImageFormat.getMimeType(imageff)));
		
		switch (imageff)
		{
			case ImageFormat.GIF:
	
				return new GIF(path);
//				return new GIF2(path);
			
			default:
			
				return new IMAGE(path, imageff);
		}
	}
	

    public static BufferedImage loadImageWithMagick(String path, Byte imageFormat ) throws IOException, InterruptedException, IM4JavaException
	{
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

		if(s2b.getImage() == null)
			throw new IM4JavaException("null image recieved from magick");
		
		BufferedImage b = ImageUtil.createOptimalImageFrom(s2b.getImage());

		return b;
	}
 		
	public static BufferedImage loadImageWithMagick(String path ) throws IOException, InterruptedException, IM4JavaException
	{
		return loadImageWithMagick(path, ImageDetector.getImageFormat(path));
	}

	public static BufferedImage convertGreyscaleFast(BufferedImage src)
	{
		ImageFilter filter = new GrayFilter(true, 50);  
		ImageProducer producer = new FilteredImageSource(src.getSource(), filter);  
		Image mage = Toolkit.getDefaultToolkit().createImage(producer);
		
		return (BufferedImage)mage;
	}

	/**
	 * gets the inverted color of the given argb pixel
	 */
	public static int getInversePixel(int rgb)
	{
        int a = ((rgb >> 24) & 0xFF);
        int r = ((rgb >> 16) & 0xFF);
        int g = ((rgb >> 8) & 0xFF);
        int b = ((rgb & 0xFF));
        
        return (a << 24) + ((255 - r) << 16) + ((255 - g) << 8) + (255 - b);
	}
	
	public static void convertInverse1(BufferedImage img)
	{
		int[] rgbs = new int[img.getWidth() * img.getHeight()];
		
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgbs, 0, img.getWidth());
	    
		for(int i = 0; i < rgbs.length; i++)
		{
			int rgb = rgbs[i];
	        int a = ((rgb >> 24) & 0xFF);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));
	        
	        rgbs[i] = (a << 24) + ((255 - r) << 16) + ((255 - g) << 8) + (255 - b);
		}
		
		img.setRGB(0, 0, img.getWidth(), img.getHeight(), rgbs, 0, img.getWidth());
	}
		
	public static void convertInverse2(BufferedImage img)
	{
		for (int x = 0; x < img.getWidth(); ++x)
	    for (int y = 0; y < img.getHeight(); ++y)
	    {
	        int rgb = img.getRGB(x, y);
	        int a = ((rgb >> 24) & 0xFF);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));
	        
	        img.setRGB(x, y, (a << 24) + ((255 - r) << 16) + ((255 - g) << 8) + (255 - b));
	    }
	}
	
	/**
	 * a much faster implementation of convertInverse1 and convertInverse2<br>
	 * only works on argb images if there is transparency, otherwise works on everything
	 */
	public static void convertInverse3(BufferedImage img)
	{
		// since we're re-writing the inverted color back in argb format, it will do it wrong 
		// when there is transparency and it's not argb, so use the other method, which handles the color convertion
		if(img.getTransparency() == Transparency.TRANSLUCENT && img.getType() != BufferedImage.TYPE_INT_ARGB)
		{
			logger.log(Level.INFO, "trying to invert none argb/rgb image, fallingback to convertIverse");
			convertInverse1(img);
			return;
		}

		DataBufferInt db = (DataBufferInt)img.getRaster().getDataBuffer();
		
		for(int i = 0; i < img.getWidth() * img.getHeight(); i++)
		{
			int rgb = db.getElem(i);
			
			// argb holds alpha of 0 with just the whole color as 0 
			// so skip it, otherwise it changes the color to white
//	        if(rgb == 0 && img.getType() == BufferedImage.TYPE_INT_ARGB)
//	        	continue;
	        
	        int a = ((rgb >> 24) & 0xFF);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));
	        
			db.setElem(i, (a << 24) + ((255 - r) << 16) + ((255 - g) << 8) + (255 - b));
		}
	}
	
	static final double gsrm = 0.3; // 0.21
	
	static final double gsgm = 0.59; // 0.71
	
	static final double gsbm = 0.11; // 0.071
	
	/**
	 * gets the greyscale color for the given argb pixel
	 */
	public static int getGreyPixel(int argb)
	{
		int a = ((argb >> 24) & 0xFF);
        int r = ((argb >> 16) & 0xFF);
        int g = ((argb >> 8) & 0xFF);
        int b = ((argb & 0xFF));
        
        byte grey = (byte)((r * gsrm) + (g * gsgm) + (b * gsbm));
        
        return  (a << 24) + (grey << 16) + (grey << 8) + grey;
	}

	public static void convertGreyscale(BufferedImage img)
	{
	    for (int x = 0; x < img.getWidth(); ++x)
	    for (int y = 0; y < img.getHeight(); ++y)
	    {
	        int rgb = img.getRGB(x, y);
	        int a = ((rgb >> 24) & 0xFF);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));

	        byte grey = (byte)((r * gsrm) + (g * gsgm) + (b * gsbm));
	        
	        img.setRGB(x, y, (a << 24) + (grey << 16) + (grey << 8) + grey);
	    }
	}
	
	/**
	 * a much faster implementation of convertGreyscale
	 */
	public static void convertGreyscale2(BufferedImage img)
	{
		// for some reason this has no issues for any image format??? 
		// but the invert image does, so wtf 
		DataBufferInt db = (DataBufferInt)img.getRaster().getDataBuffer();
		
		for(int i = 0; i < img.getWidth() * img.getHeight(); i++)
		{
			int rgb = db.getElem(i);
	        int a = ((rgb >> 24) & 0xFF);
	        int r = ((rgb >> 16) & 0xFF);
	        int g = ((rgb >> 8) & 0xFF);
	        int b = ((rgb & 0xFF));

	        byte grey = (byte)((r * gsrm) + (g * gsgm) + (b * gsbm));
			
			db.setElem(i, (a << 24) + (grey << 16) + (grey << 8) + grey);
			
		}
	}
}










