import java.awt.EventQueue;
import java.util.Map;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;

import UI.MainForm;


public class Program extends JFrame 
{
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					if(ProcessStarter.getGlobalSearchPath() == null)
					{
						ProcessStarter.setGlobalSearchPath(System.getenv("PATH"));
					}
					
//					this is a commit
					
					if(Configuration.GUISettings.DEBUG_MODE)
					{
						ProcessStarter.setGlobalSearchPath("D:\\tmp\\JViewer\\magick");
					}
					
					
					MainForm frame = new MainForm();
				 
				    frame.setVisible(true);
				    
//				    Test.gui();
//				    System.out.println(Arrays.toString( ImageIO.getWriterMIMETypes()));
		
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
}
