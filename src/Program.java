import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import UI.GraphicsFrame;


public class Program extends JFrame 
{

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Program frame = new Program();
				 
				    frame.setVisible(true);
				    
				   
		
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Program() {
		setTitle("Hello World");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("This is a label");
		lblNewLabel.setBounds(123, 40, 139, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(61, 91, 267, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		ImageIcon icon = new ImageIcon("D:\\0_IMAGE\\APPLE_WORM\\LoadingScreen.jpg");
		GraphicsFrame gf = new GraphicsFrame();
		gf.loadImage(icon.getImage());
		
		gf.setBounds(50,0, icon.getIconWidth(), icon.getIconHeight());
		contentPane.add(gf);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				setTitle(textField.getText());
				gf.setDrawImage(!gf.getDrawImage());
//				gf.invalidate();
			}
		});
		btnNewButton.setBounds(84, 209, 89, 23);
		contentPane.add(btnNewButton);
		
		
	}
}
