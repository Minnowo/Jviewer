package Util.Logging;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil 
{
	public static final Path LOG_FILE_DIRECTORY = Paths.get(System.getProperty("user.home"), ".local", "Jviewer");
	
	public static FileHandler fh;
	
	public static ConsoleHandler ch;
	
	
	static 
	{
		LOG_FILE_DIRECTORY.toFile().mkdirs();
		
		try 
		{
			ch = new ConsoleHandler();
			ch.setFormatter(new CustomFormatter());
			
			fh = new FileHandler(Paths.get(LOG_FILE_DIRECTORY.toString(), "logs.log").toString(), true);
			fh.setFormatter(new CustomFormatter()); // set formatter
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	
	public static Logger getLogger(String name)
	{
		Logger logger = Logger.getLogger(name);  
	    
	    try 
	    {        
	    	logger.setUseParentHandlers(false);
	    	
	    	logger.addHandler(ch);
	    	
	        logger.addHandler(fh);
	        
	        logger.setLevel(Level.ALL);
	    } 
	    catch (SecurityException e) 
	    {  
	        e.printStackTrace();  
	    } 

	    logger.info(name + " logger instance created");
	    
	    return logger;  
	}
}
