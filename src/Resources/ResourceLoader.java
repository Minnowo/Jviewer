package Resources;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Graphics.ImageUtil;
import Util.Logging.LoggerWrapper;

public class ResourceLoader 
{
	public static final ResourceLoader INSTANCE = new ResourceLoader();
	
	
	public static Image loadIconResource(final String RESOURCE_PATH)
	{
		try (InputStream in = INSTANCE.getClass().getResourceAsStream(RESOURCE_PATH))
		{
			if(in == null)
			{
				LoggerWrapper.warning("could not load resource: " + RESOURCE_PATH);
				return null;
			}	
				
			BufferedImage buff = ImageIO.read(in);

			if(buff == null)
			{
				LoggerWrapper.warning("could not load image from resource: " + RESOURCE_PATH);
				return null;
			}	
			
			return ImageUtil.createOptimalImageFrom(buff);
		}
		catch (IOException e) 
		{
			LoggerWrapper.warning(String.format("Error loading resource %s", RESOURCE_PATH), e);
		}
		
		return null;
	}
	
}
