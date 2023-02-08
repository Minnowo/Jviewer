package nyaa.alice.jviewer.drawing;

import java.awt.Color;

public class ColorUtil
{
    public static Color invertColor(Color c)
    {
        return new Color(255 - c.getRed(), 255 - c.getBlue(), 255 - c.getGreen(), 255);
    }
    
    public static int toARGB(Color c)
    {
        int a = c.getAlpha();
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return (a << 24) + (r << 16) + (g << 8) + b;
    }
    
    public static Color fromARGB(int c)
    {
        int a = ((c >> 24) & 0xFF);
        int r = ((c >> 16) & 0xFF);
        int g = ((c >> 8) & 0xFF);
        int b = ((c & 0xFF));
        return new Color(r, g, b, a);
    }
    
    public static int getDistance(Color c1, Color c2)
    {
        int redDifference = c1.getRed() - c2.getRed();
        int greenDifference = c1.getGreen() - c2.getGreen();
        int blueDifference = c1.getBlue() - c2.getBlue();

        return redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference;
    }
    
    public static int getDistance(int c1, int c2)
    {
        int redDifference = ((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF);
        int greenDifference = ((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF);
        int blueDifference = ((c1 ) & 0xFF) - ((c2 ) & 0xFF);

        return redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference;
    }
    
    
    /**
     * Finds the closest color in the given array.
     * @param current The color to search
     * @param colors The available colors.
     * @return The index of the closest color in the array.
     */
    public static int FindNearestColor(Color current, Color[] colors)
    {
        int shortestDistance = Integer.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < colors.length; i++)
        {
            Color match = colors[i];
            
            int distance = ColorUtil.getDistance(current, match);

            if (distance < shortestDistance)
            {
                index = i;
                shortestDistance = distance;
            }
        }

        return index;
    }
    
    /**
     * Color format ARGB
     * Finds the closest color in the given array.
     * @param current The color to search
     * @param colors The available colors.
     * @return The index of the closest color in the array.
     */
    public static int FindNearestColor(int current, int[] colors)
    {
        int shortestDistance = Integer.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < colors.length; i++)
        {
            int match = colors[i];
            
            int distance = ColorUtil.getDistance(current, match);

            if (distance < shortestDistance)
            {
                index = i;
                shortestDistance = distance;
            }
        }

        return index;
    }
}
