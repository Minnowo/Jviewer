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
}
