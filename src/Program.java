import java.awt.EventQueue;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;

import UI.MainForm;
import UI.Test;


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
