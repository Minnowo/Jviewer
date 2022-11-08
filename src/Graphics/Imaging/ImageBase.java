package Graphics.Imaging;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.logging.Logger;

import Graphics.Rotation;
import Util.Logging.LogUtil;

public abstract class ImageBase
{
	protected final static Logger logger = LogUtil.getLogger(ImageBase.class.getName());

	protected byte imageFormat;
	protected String mimeType;
	protected int width;
	protected int height;
	
	public ImageBase(byte imageFormat, String mime)
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

    public int getWidth()
    {
    	return this.width;
    }
    
    public int getHeight()
    {
    	return this.height;
    }
    
    
    public abstract BufferedImage getBuffered();
    
    public abstract void load(File path);
    
    public abstract void load(String path);
    
    public abstract void save(String path);
    
    public abstract void save(File path);
    
	public abstract void flush();
	
	public abstract void rotate(Byte r);
	
	public abstract void rotateByDegrees(double degree);
	
	public abstract void convertGreyscale();
	
	public abstract void convertInverse();
}
