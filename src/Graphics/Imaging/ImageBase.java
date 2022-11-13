package Graphics.Imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import Util.Logging.LogUtil;

public abstract class ImageBase
{
	protected final static Logger logger = LogUtil.getLogger(ImageBase.class.getName());

	protected byte imageFormat;
	protected int width;
	protected int height;
	protected boolean error = false;
	
	public ImageBase(byte imageFormat)
	{
		this.imageFormat = imageFormat;
	}
	
    public byte GetImageFormat()
    {
    	return this.imageFormat;
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
    
    public abstract boolean err();
    
    public abstract boolean load(File path);
    
    public abstract boolean load(String path);
    
    public abstract boolean save(String path) throws ImageUnsupportedException ;
    
    public abstract boolean save(File path) throws ImageUnsupportedException ;
    
	public abstract void flush();
	
	public abstract void rotate(Byte r);
	
	public abstract void rotateByDegrees(double degree);
	
	public abstract void convertGreyscale();
	
	public abstract void convertInverse();
}
