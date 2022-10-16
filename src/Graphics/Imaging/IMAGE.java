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
import Graphics.ImageUtil;
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
    
    
}
