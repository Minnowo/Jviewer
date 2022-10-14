import java.awt.EventQueue;

import javax.swing.JFrame;

import Graphics.ImageUtil;
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
