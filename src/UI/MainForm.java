package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import UI.ImageDisplay.GraphicsFrame;

public class MainForm extends JFrame 
{

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public MainForm() {
		setTitle("Hello World");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 830, 545);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(278, 11, 526, 484);
		contentPane.add(panel);
		panel.setLayout(null);
		
		GraphicsFrame graphicsFrame = new GraphicsFrame();
		graphicsFrame.setBounds(10, 11, 506, 462);
		panel.add(graphicsFrame);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				File f = new File(textField.getText());
				
				if (f.exists())
				{
					System.out.println("loading: " + f.getPath());
					ImageIcon i = new ImageIcon(textField.getText());
					graphicsFrame.loadImage(i.getImage());
				}
				else 
				{
					System.out.println("loading: 02.jpg");
					ImageIcon i = new ImageIcon("D:\\02.jpg");
					graphicsFrame.loadImage(i.getImage());
				}
			}
		});
		btnNewButton.setBounds(10, 472, 258, 23);
		contentPane.add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(10, 11, 258, 450);
		contentPane.add(textField);
		textField.setColumns(10);

		
	}
}
