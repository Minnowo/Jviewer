package Util;

import java.awt.Color;

public class ColorUtil 
{
	public static Color invertColor(Color c)
	{
		return new Color(255 - c.getRed(), 255 - c.getBlue(), 255 - c.getGreen(), 255);
	}
}
