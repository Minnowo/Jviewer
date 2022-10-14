package Graphics.Imaging.Enums;

public class ImageFormat 
{
	public static final byte UNKNOWN = -1;
	
	
	public static final byte PNG = 0;

	
    public static final byte JPG = 1;
    
    
    public static final byte BMP = 2;    
    
    
    public static final byte TIFF = 3;
    
    
    public static final byte GIF = 4;
    
    
    /*
     * requires imagemagick
     */
    public static final byte WEBP = 5;
    
    public static final byte PSD = 6;
    
    
    public static boolean hasNativeSupport(byte format)
    {
    	switch (format) 
    	{
	    	case PNG:
	    	case JPG:
	    	case BMP:
	    	case GIF:
	    	case TIFF:
	    		return true;
		}
    	
    	return false;
    }
    
    
    public static String getFileExtension(byte format)
    {
    	switch (format)
    	{
    		default:
    			return "";
	    	case PNG:
	    		return "png";
	    	case BMP:
	    		return "bmp";
	    	case JPG:
	    		return "jpg";
	    	case GIF:
	    		return "gif";
	    	case TIFF:
	    		return "tiff";
	    	case WEBP:
	    		return "webp";
	    	case PSD:
	    		return "psd";
		}
    }
    
    public static byte getFromFileExtension(String ext)
    {
    	
    	if(ext.startsWith("."))
    		ext = ext.substring(1);
    	
    	
    	switch (ext.toLowerCase())
    	{
	    	default:
	    		return UNKNOWN;
	    		
			case "png": 
				return PNG;
			
	    	case "bmp":
	    		return BMP;
	    		
	    	case "tiff":
	    		return TIFF;
	    		
	    	case "gif":
	    		return GIF;
	    	
			case "jpg":
			case "jpe":
			case "jpeg":
			case "jfif":
				return JPG;
				
			case "webp":
				return WEBP;
				
			case "psd":
				return PSD;
		}
    }
}
