package Graphics.Imaging;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import Configuration.ImageMagick;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import Graphics.Imaging.Exceptions.RequiresMagickException;

public abstract class IMAGE 
{
    protected final static Logger logger = Logger.getLogger(IMAGE.class.getName());
    
	protected BufferedImage image;
	protected byte imageFormat;
	protected String mimeType;
	
	public IMAGE(byte imageFormat, String mime)
	{
		this.imageFormat = imageFormat;
		this.mimeType = mime;
	}
	
    public byte GetImageFormat()
    {
    	return this.imageFormat;
    }

    public String GetMimeType()
    {
    	return this.mimeType;
    }

    public BufferedImage getImage()
    {
    	return this.image;
    }
    
    public abstract void load(File path);
    
    public abstract void load(String path);
    
    public abstract void save(String path);
    
    public abstract void save(File path);
    
    
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
		// bmp loads the fastest, but draws to the screen slowly
		// png loads the slowest, but keeps transparency and draws fast, also most memory
		// jpg loads faster than png, but removes transparency,
		final int chosenFormatIndex = 0;
		final String[] stdoutDecodeFormat = { "bmp", "png", "jpg" };
		final String stdoutDFormat = stdoutDecodeFormat[chosenFormatIndex];
		
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
		op.addImage(stdoutDFormat + ":-"); 


		// set up command
		ConvertCmd convert = new ConvertCmd();
		Stream2BufferedImage s2b = new Stream2BufferedImage();
		convert.setOutputConsumer(s2b);

		// run command and extract BufferedImage from OutputConsumer
		convert.run(op);

		BufferedImage b = s2b.getImage();
		
		// https://stackoverflow.com/a/659533
		// optimize the image for drawing to the screen, only need this for bmp, but could do it always to ensure fast rendering
		if(stdoutDFormat.compareTo("bmp") == 0)
		{
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    GraphicsDevice device = env.getDefaultScreenDevice();
		    GraphicsConfiguration config = device.getDefaultConfiguration();
		    BufferedImage buffy = config.createCompatibleImage(b.getWidth(), b.getHeight(), Transparency.TRANSLUCENT);
		    
		    Graphics g = buffy.getGraphics();
		    g.drawImage(b, 0, 0, b.getWidth(), b.getHeight(), null);
		    g.dispose();
		    
		    return buffy;
		}
		

		return b;
	}
}
