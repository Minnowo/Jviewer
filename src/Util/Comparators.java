package Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comparators 
{
	
public static final WindowsExplorerComparator NATSORT = new WindowsExplorerComparator();

	public static int naturalCompare(String a, String b) 
	{
//		return NATSORT.compare(a, b);
        a = a.toLowerCase();
        b = b.toLowerCase();
//	    
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
	
	
	 public static class WindowsExplorerComparator implements Comparator<String> {

	        private static final Pattern splitPattern = Pattern.compile("\\d+|\\.|\\s");

	        @Override
	        public int compare(String str1, String str2) {
	            Iterator<String> i1 = splitStringPreserveDelimiter(str1).iterator();
	            Iterator<String> i2 = splitStringPreserveDelimiter(str2).iterator();
	            while (true) {
	                //Til here all is equal.
	                if (!i1.hasNext() && !i2.hasNext()) {
	                    return 0;
	                }
	                //first has no more parts -> comes first
	                if (!i1.hasNext() && i2.hasNext()) {
	                    return -1;
	                }
	                //first has more parts than i2 -> comes after
	                if (i1.hasNext() && !i2.hasNext()) {
	                    return 1;
	                }

	                String data1 = i1.next();
	                String data2 = i2.next();
	                int result;
	                try {
	                    //If both datas are numbers, then compare numbers
	                    result = Long.compare(Long.valueOf(data1), Long.valueOf(data2));
	                    //If numbers are equal than longer comes first
	                    if (result == 0) {
	                        result = -Integer.compare(data1.length(), data2.length());
	                    }
	                } catch (NumberFormatException ex) {
	                    //compare text case insensitive
	                    result = data1.compareToIgnoreCase(data2);
	                }

	                if (result != 0) {
	                    return result;
	                }
	            }
	        }

	        private List<String> splitStringPreserveDelimiter(String str) {
	            Matcher matcher = splitPattern.matcher(str);
	            List<String> list = new ArrayList<String>();
	            int pos = 0;
	            while (matcher.find()) {
	                list.add(str.substring(pos, matcher.start()));
	                list.add(matcher.group());
	                pos = matcher.end();
	            }
	            list.add(str.substring(pos));
	            return list;
	        }
}}
