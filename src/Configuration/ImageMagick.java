package Configuration;

import Graphics.Imaging.Enums.ImageFormat;

public class ImageMagick 
{
	public static boolean useImageMagick = true;
	
	
	public static boolean readRequiresMergeLayers(byte format)
	{
		switch (format) 
		{
			case ImageFormat.PSD:
				return true;
		}
		
		return false;
	}
}
