import java.awt.EventQueue;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;

import UI.MainForm;
import Util.ParamRunnable;
import Util.SingleInstanceChecker;
import Util.Logging.LogUtil;


public class Program extends JFrame 
{
	protected static final Logger logger = LogUtil.getLogger(Program.class.getName());

	public static MainForm frame;

	
	
	public static void main(String[] args) 
	{
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
							String s = "D:\\tmp\\JViewer\\magick";
							
							ProcessStarter.setGlobalSearchPath(s);
							
							logger.log(Level.ALL, "debug mode enabled, setting ProcessStarter global search path to %s".formatted(s));
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
