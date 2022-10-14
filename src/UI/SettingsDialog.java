package UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.im4java.process.ProcessStarter;

import Configuration.ImageMagick;

public class SettingsDialog  extends JDialog 
{
	private JTabbedPane tabbedPane;
	private JPanel tabPage1, tabPage2 ;
	private JTextField textField;
	private JPanel panel;
	private JLabel lblNewLabel;
	private JTextArea textArea;
	private JPanel panel_1;
	private JCheckBox chckbxNewCheckBox;
	
	public SettingsDialog()
	{
		this.setTitle("Hello World");
		this.setSize(572, 568);
		this.setModal(true);
		this.setLocationRelativeTo(null);
		setupComponents();
	}

	
	public void showDialog()
	{
		this.setVisible(true);
		
		ImageMagick.useImageMagick = chckbxNewCheckBox.isSelected();
		ProcessStarter.setGlobalSearchPath(textArea.getText().replaceAll("\\r?\\n", ";"));
	}
	
	
	protected void setupComponents()
	{
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		
		tabPage1 = new JPanel();
		tabPage2 = new JPanel();
		
		tabbedPane.addTab("Page 1", tabPage1);
		tabbedPane.addTab("Magick Settings", tabPage2);
		tabPage2.setLayout(new BoxLayout(tabPage2, BoxLayout.Y_AXIS));
		
		panel_1 = new JPanel();
		tabPage2.add(panel_1);
		
		chckbxNewCheckBox = new JCheckBox("Use Image Magick");
		chckbxNewCheckBox.setSelected(ImageMagick.useImageMagick);
		panel_1.add(chckbxNewCheckBox);
		
		panel = new JPanel();
		tabPage2.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{157, 0};
		gbl_panel.rowHeights = new int[]{22, 96, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblNewLabel = new JLabel("<html>Image Magick Search Path<br/>If Image Magick Is Not Added To Path, Put It's Directory Here<br/>Use 1 Line Per Directory</html>");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		textArea = new JTextArea();
		
		if(ProcessStarter.getGlobalSearchPath() != null)
		{
			textArea.setText(ProcessStarter.getGlobalSearchPath().replace(';', '\n'));
		}
		
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		panel.add(textArea, gbc_textArea);
		
	}
}
