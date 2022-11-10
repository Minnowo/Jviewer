package UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.im4java.process.ProcessStarter;

import Configuration.GUISettings;
import Configuration.ImageMagick;
import Util.Logging.LogUtil;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class SettingsDialog  extends JDialog 
{
	protected static final Logger logger = LogUtil.getLogger(SettingsDialog.class.getName());
	
	private JTabbedPane tabbedPane;
	private JPanel tabPage1, tabPage2 ;
	private JTextField textField;
	private JPanel panel;
	private JTextArea textArea;
	private JPanel panel_1;
	private JCheckBox chckbxNewCheckBox;
	private JTextField textField_1;
	private JCheckBox chckbxNewCheckBox_1;
	private JButton btnNewButton;
	private JTextArea textArea_1;
	
	public SettingsDialog()
	{
		this.setTitle("Hello World");
		this.setSize(590, 546);
		this.setModal(true);
		this.setLocationRelativeTo(null);
		setupComponents();
	}

	
	public void showDialog()
	{
		this.setVisible(true);
		
		// update the changed settings here
		ImageMagick.useImageMagick = chckbxNewCheckBox.isSelected();
		this.logger.log(Level.INFO, "updated setting 'UseImageMagick' set value '%s'".formatted(ImageMagick.useImageMagick));
		
		ProcessStarter.setGlobalSearchPath(textArea.getText().replaceAll("\\r?\\n", File.pathSeparator));
		this.logger.log(Level.INFO, "updated setting 'MagickGlobalSearchPath' set value '%s'".formatted(ProcessStarter.getGlobalSearchPath()));
		
		GUISettings.WRAP_TABS = chckbxNewCheckBox_1.isSelected();
		this.logger.log(Level.INFO, "updated setting 'WrapTabs' set value '%s'".formatted(GUISettings.WRAP_TABS));
	}
	
	
	protected void setupComponents()
	{
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		
		tabPage1 = new JPanel();
		tabPage2 = new JPanel();
		
		tabbedPane.addTab("General Settings", tabPage1);
		tabPage1.setLayout(null);
		
		btnNewButton = new JButton("New button");
		btnNewButton.setBounds(35, 228, 89, 23);
		tabPage1.add(btnNewButton);
		
		chckbxNewCheckBox_1 = new JCheckBox("Folding Tabs");
		chckbxNewCheckBox_1.setBounds(22, 32, 130, 42);
		tabPage1.add(chckbxNewCheckBox_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(219, 45, 273, 217);
		tabPage1.add(textField_1);
		textField_1.setColumns(10);
		tabbedPane.addTab("Magick Settings", tabPage2);
		tabPage2.setLayout(new BoxLayout(tabPage2, BoxLayout.Y_AXIS));
		
		panel_1 = new JPanel();
		tabPage2.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		panel_1.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{157, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 22, 96, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		
		textArea_1 = new JTextArea();
		textArea_1.setFont(new Font("Consolas", Font.PLAIN, 13));
		textArea_1.setWrapStyleWord(true);
		textArea_1.setLineWrap(true);
		textArea_1.setText("Set the environment-variable IM4JAVA_TOOLPATH.\nThis variable should contain a list of directories to search for your tools.\nThey should be separated by your platform-pathdelemiter (on *NIX typically \":\", on Windows \";\")\nYou can also add paths to the box below (1 line per directory).\nDetected path delimeter \"%s\"".formatted(File.pathSeparator));
		textArea_1.setEditable(false);
		GridBagConstraints gbc_textArea_1 = new GridBagConstraints();
		gbc_textArea_1.insets = new Insets(0, 0, 5, 0);
		gbc_textArea_1.fill = GridBagConstraints.BOTH;
		gbc_textArea_1.gridx = 0;
		gbc_textArea_1.gridy = 0;
		panel.add(textArea_1, gbc_textArea_1);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		panel.add(textArea, gbc_textArea);
		
		chckbxNewCheckBox = new JCheckBox("Use Image Magick");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 10;
		panel.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
		chckbxNewCheckBox.setSelected(ImageMagick.useImageMagick);
		
		if(ProcessStarter.getGlobalSearchPath() != null)
		{
			textArea.setText(ProcessStarter.getGlobalSearchPath().replace(';', '\n'));
		}
		
	}
}
