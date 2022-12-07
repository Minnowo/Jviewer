package Util.Logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerWrapper
{
	private static final Logger Logger = LogUtil.getLogger(LoggerWrapper.class.getName());
	
	public static void warning(String message)
	{
		Logger.warning(message);
	}
	
	public static void warning(String message, Exception e)
	{
		Logger.log(Level.WARNING, message, e);
	}
	
	public static void info(String message)
	{
		Logger.info(message);
	}
}
