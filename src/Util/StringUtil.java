package Util;

import java.io.File;

public class StringUtil 
{
	public static String getFileExtension(File file)
	{
		return getFileExtension(file, true);
	}
	
	public static String getFileExtension(File file, boolean includeDot) 
	{
	    String name = file.getName();
	    
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
}
