package Configuration;

import Graphics.Imaging.Enums.ImageFormat;

public class ImageMagick 
{
	public interface ImageDecodeFormat
	{
		public static final byte BMP = 0;
		
		public static final byte PNG = 1;
		
		public static final byte JPG = 2;
		
		public static final byte TIFF = 3;
		
		public static final byte GIF = 4;
	}
	
	public static boolean useImageMagick = true;
	

	/**
	 * the {@link ImageDecodeFormat} which determines what decoded image type magick will return
	 */
	public static byte Image_Decode_Format = ImageDecodeFormat.BMP;
	
	/**
	 * the decode formats java can read 
	 */
	public static final String[] STDOUT_DECODE_FORMATS = { "bmp", "png", "jpg", "tiff", "gif" };
	
	
	public static String getImageDecodeFormat()
	{
		switch (Image_Decode_Format) 
		{
			default:
			case ImageDecodeFormat.BMP: 
				return STDOUT_DECODE_FORMATS[ImageDecodeFormat.BMP];
				
			case ImageDecodeFormat.PNG: 
				return STDOUT_DECODE_FORMATS[ImageDecodeFormat.PNG];
				
			case ImageDecodeFormat.JPG: 
				return STDOUT_DECODE_FORMATS[ImageDecodeFormat.JPG];
				
			case ImageDecodeFormat.TIFF: 
				return STDOUT_DECODE_FORMATS[ImageDecodeFormat.TIFF];
				
			case ImageDecodeFormat.GIF: 
				return STDOUT_DECODE_FORMATS[ImageDecodeFormat.GIF];
		}
	}
	
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
