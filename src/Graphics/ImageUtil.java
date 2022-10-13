package Graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil 
{
	public static BufferedImage loadImage(String path)
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
