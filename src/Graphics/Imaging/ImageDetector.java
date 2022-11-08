package Graphics.Imaging;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import Graphics.Imaging.Enums.ImageFormat;

public class ImageDetector 
{
	public static final int MAX_HEADER_LENGTH = 8;
	
    public static final byte[] BMP_BYTE_IDENTIFIER = new byte[] { 0x42, 0x4D };

    public static final byte[] ICO_BYTE_IDENTIFIER = new byte[] { 0x00, 0x00, 0x01, 0x00 };

    public static final byte[] JPEG_BYTE_IDENTIFIER = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
    
    public static final byte[] PNG_BYTE_IDENTIFIER  = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

    public static final byte[] TIFF_BYTE_IDENTIFIER_LE = new byte[] { 0x49, 0x49, 0x2A, 0x00 };

    public static final byte[] TIFF_BYTE_IDENTIFIER_BE = new byte[] { 0x4D, 0x4D, 0x00, 0x2A };

    public static final byte[] WEBP_BYTE_IDENTIFIER = new byte[] { 0x52, 0x49, 0x46, 0x46 };

    public static final byte[] GIF_BYTE_IDENTIFIER_1 = new byte[] { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61 };

    public static final byte[] GIF_BYTE_IDENTIFIER_2 = new byte[] { 0x47, 0x49, 0x46, 0x38, 0x37, 0x61 };

    public static final byte[] PSD_BYTE_IDENTIFIER = new byte[] { 0x38, 0x42, 0x50, 0x53 };
    
    static class ImageFormatHeader
    {
    	byte[] header;
    	byte imageFormat;
    	
    	public ImageFormatHeader(byte[] h, byte i) {
			this.header = h;
			this.imageFormat = i;
		}
    }
    
    public static final ImageFormatHeader[] HEADER_MAP = new ImageFormatHeader[] 
	{
    		new ImageFormatHeader(BMP_BYTE_IDENTIFIER, ImageFormat.BMP),
    		new ImageFormatHeader(ICO_BYTE_IDENTIFIER, ImageFormat.ICO),
    		new ImageFormatHeader(PNG_BYTE_IDENTIFIER, ImageFormat.PNG),
    		new ImageFormatHeader(JPEG_BYTE_IDENTIFIER, ImageFormat.JPG),
    		new ImageFormatHeader(TIFF_BYTE_IDENTIFIER_LE, ImageFormat.TIFF),
    		new ImageFormatHeader(TIFF_BYTE_IDENTIFIER_BE, ImageFormat.TIFF),
    		new ImageFormatHeader(WEBP_BYTE_IDENTIFIER, ImageFormat.WEBP),
    		new ImageFormatHeader(GIF_BYTE_IDENTIFIER_1, ImageFormat.GIF),
    		new ImageFormatHeader(GIF_BYTE_IDENTIFIER_2, ImageFormat.GIF),
    		new ImageFormatHeader(PSD_BYTE_IDENTIFIER, ImageFormat.PSD),
    };    

    public static boolean startsWith(byte[] thisBytes, byte[] thatBytes)
    {
    	int shortest = thisBytes.length;
    	
    	if(thatBytes.length < shortest)
    		
    		shortest = thatBytes.length;
    	
    	for (int i = 0; i < shortest; i += 1)
            
    		if (thatBytes[i] != thisBytes[i])
                return false;

        return true;
    }
    
    public static byte getImageFormat(String path)
    {
    	File f = new File(path);
    	
    	try 
    	{
			DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
			
			byte[] magicBytes = new byte[MAX_HEADER_LENGTH];
			
			for(int i = 0; i < MAX_HEADER_LENGTH; i++)
			{
                 magicBytes[i] = is.readByte();

                 for (ImageFormatHeader ifh : HEADER_MAP)
                 {
                     if (startsWith(magicBytes, ifh.header))
                     {
                         return ifh.imageFormat;
                     }
                 }
			}
		} 
    	catch (FileNotFoundException e) 
    	{
			return ImageFormat.UNKNOWN;	
		} 
    	catch (IOException e) 
    	{
    		return ImageFormat.UNKNOWN;
		}
    	
    	return ImageFormat.UNKNOWN;
    }
}
