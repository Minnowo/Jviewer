package Util;

import java.util.Comparator;

public class Comparators 
{
	public static int naturalCompare(String a, String b) 
	{
        a = a.toLowerCase();
        b = b.toLowerCase();
	    
	    int aLength = a.length();
	    int bLength = b.length();
	    int minSize = Math.min(aLength, bLength);
	    char aChar, bChar;
	    boolean aNumber, bNumber;
	    boolean asNumeric = false;
	    int lastNumericCompare = 0;
	    
	    for (int i = 0; i < minSize; i++) 
	    {
	        aChar = a.charAt(i);
	        bChar = b.charAt(i);
	        aNumber = aChar >= '0' && aChar <= '9';
	        bNumber = bChar >= '0' && bChar <= '9';
	        
	        if (asNumeric) 
	        {
	            if (aNumber && bNumber) 
	            {
	                if (lastNumericCompare == 0)
	                    lastNumericCompare = aChar - bChar;
	            } 
	            else if (aNumber)
	            {
	            	return 1;
	            }
	            else if (bNumber)
	            {
	            	return -1;
	            }
	            else if (lastNumericCompare == 0) 
	            {
	                if (aChar != bChar)
	                    return aChar - bChar;
	                
	                asNumeric = false;
	            } 
	            else
	            {
	            	return lastNumericCompare;
	            }
	        }
	        else if (aNumber && bNumber) 
	        {
	            asNumeric = true;
	            if (lastNumericCompare == 0)
	                lastNumericCompare = aChar - bChar;
	        } 
	        else if (aChar != bChar)
	        {
	        	return aChar - bChar;
	        }
	    }
	    
	    if (asNumeric)
	    {
	    	if (aLength > bLength && a.charAt(bLength) >= '0' && a.charAt(bLength) <= '9') // as number
	            return 1;  // a has bigger size, thus b is smaller
	        if (bLength > aLength && b.charAt(aLength) >= '0' && b.charAt(aLength) <= '9') // as number
	            return -1;  // b has bigger size, thus a is smaller
	        if (lastNumericCompare == 0)
	          return aLength - bLength;
	        
	        return lastNumericCompare;
	    }
	        
	    return aLength - bLength;
	}
	
	
	public static class NaturalOrderComparator implements Comparator
	{
	    public int compare(Object o1, Object o2)
	    {
	        return naturalCompare(o1.toString(), o2.toString());
	    }
	    
	}
}
