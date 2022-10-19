import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;

import UI.MainForm;


public class Program extends JFrame 
{
	protected static final Logger logger = Logger.getLogger(Program.class.getName());

	public static void main(String[] args) 
	{
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s %n");
		
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

					
					MainForm frame = new MainForm();
				 
				    frame.setVisible(true);		
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
}
