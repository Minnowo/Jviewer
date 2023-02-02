package Util;

import java.io.File;

public class StringUtil 
{
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
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) 
	{
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) 
	    {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
