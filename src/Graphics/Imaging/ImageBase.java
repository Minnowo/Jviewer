package Graphics.Imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class ImageBase 
{
	private byte imageFormat;
	private String mimeType;
	
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

    public abstract void load(File path);
    
    public abstract void load(String path);
    
    protected static BufferedImage loadImage(String path)
    {
		File path_ = new File(path);
		
		if(!path_.exists())
			return null;
		
		try 
		{
		    return ImageIO.read(path_);
		} 
		catch (IOException e) 
		{
			return null;
		}	
    }
}
