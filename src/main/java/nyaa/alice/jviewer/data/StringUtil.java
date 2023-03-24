package nyaa.alice.jviewer.data;

import java.io.File;

public class StringUtil
{
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public static String getFileExtension(String name, boolean includeDot)
	{
		int lastIndexOf = name.lastIndexOf(".");
	    
	    if (lastIndexOf == -1) 
	    {
	        return ""; // empty extension
	    }
	    
	    if(includeDot)
	    {
	    	return name.substring(lastIndexOf).toLowerCase();
	    }
	    
	    return name.substring(lastIndexOf + 1).toLowerCase();
	}
	
	public static String getFileExtension(String name)
	{
		return getFileExtension(name, true);
	}
	
	public static String getFileExtension(File file)
	{
		return getFileExtension(file.getName(), true);
	}
	
	public static String getFileExtension(File file, boolean includeDot) 
	{
	    return getFileExtension(file.getName(), includeDot);   
	}
	
    public static String bytesToHex(byte[] bytes) 
    {
        char[] hexChars = new char[bytes.length * 2];
        
        for (int j = 0; j < bytes.length; j++) 
        {
            int v = bytes[j] & 0xFF;
            // hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j << 1] = HEX_ARRAY[v >>> 4];
            // hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[(j << 1) + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] bytesFromHex(String s)
    {
        if(s == null || s.length() == 0 || s.length() % 2 != 0)
        {
            return new byte[0];
        }

        int len = s.length();

        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    public static String combinePath(String... args)
    {
        return String.join(File.separator, args);
    }
}
