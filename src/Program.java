import java.awt.EventQueue;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;

import UI.MainForm;
import Util.ParamRunnable;
import Util.SingleInstanceChecker;


public class Program extends JFrame 
{
	protected static final Logger logger = Logger.getLogger(Program.class.getName());

	public static MainForm frame;
	
	
	public static void main(String[] args) 
	{
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s %n");
		
		
		// TODO: setup proper logging to a file, cause rn it doesn't log anything and it's annoying
		
//		System.setProperty("java.util.logging.FileHandler.pattern", Paths.get(System.getProperty("user.home"), ".local", "Jviewer", "logs.log").toString());
		

		ParamRunnable ru = new ParamRunnable() 
		{	
			@Override
			protected void runWithParams(Object... args) 
			{
				if(frame == null)
					return;
				
				StringJoiner sb = new StringJoiner(" ");
				
				for(Object o : args)
				{
					if(o instanceof String)
					{
						frame.handleStartArgument((String)o);
						
						sb.add("'" + (String)o + "'");
					}
				}
				
				logger.info("recieved %d arguments [%s]".formatted(args.length, sb.toString()));
				
				frame.bringToFront();
			}
		};
		
	    // ENSURE SINGLE INSTANCE
	    if (!SingleInstanceChecker.INSTANCE.isOnlyInstance(ru, true, args)) 
	    {
	        System.exit(0);
	    }

	    // launch rest of application here
//	    System.out.println("Application starts properly because it's the only instance.");
		
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					if(ProcessStarter.getGlobalSearchPath() == null)
					{
						logger.log(Level.INFO, "could not find environmental variable IM4JAVA_TOOLPATH, using PATH instead");
						
						ProcessStarter.setGlobalSearchPath(System.getenv("PATH"));
						
						if(Configuration.GUISettings.DEBUG_MODE)
						{
							ProcessStarter.setGlobalSearchPath("D:\\tmp\\JViewer\\magick");
						}
					}
					else 
					{
						logger.log(Level.INFO, "global magick search path set [" + ProcessStarter.getGlobalSearchPath() + "]");
					}

					
					frame = new MainForm();
					
				    frame.setVisible(true);
				    
				    frame.handleStartArguments(args);
				    
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
}
