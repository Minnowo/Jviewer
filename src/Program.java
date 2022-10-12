import java.awt.EventQueue;

import javax.swing.JFrame;

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
				    
				    
		
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
}
