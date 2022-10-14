package Graphics.Imaging;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;

import javax.imageio.ImageIO;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import Configuration.ImageMagick;

public abstract class IMAGE 
{
	private byte imageFormat;
	private String mimeType;
	
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

    public abstract void load(File path);
    
    public abstract void load(String path);
    
    
    
    
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
			catch (IOException | InterruptedException | IM4JavaException e1) 
			{
				e1.printStackTrace();
			}
		}
	
		
		try 
		{
		    return ImageIO.read(path_);
		} 
		catch (IOException e) 
		{
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
		
		IMOperation op = new IMOperation();

		// input image path
		op.addImage(path);
		
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
