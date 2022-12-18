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
import Util.Comparators;
import Util.Logging.LogUtil;
import Util.Logging.LoggerWrapper;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.FlowLayout;

public class SettingsDialog  extends JDialog 
{
	private JTabbedPane tabbedPane;
	private JPanel tabPage1, tabPage2 ;
	private JTextField textField;
	private JPanel panel;
	private JTextArea textArea;
	private JPanel panel_1;
	private JCheckBox chckbxNewCheckBox;
	private JCheckBox chckbxNewCheckBox_1;
	private JTextArea textArea_1;
	private JCheckBox chckbxNewCheckBox_1_1 ;
	private JCheckBox chckbxNewCheckBox_2;
	
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
		LoggerWrapper.log(Level.CONFIG, String.format("updated setting 'UseImageMagick' set value '%s'", ImageMagick.useImageMagick));
		
		ProcessStarter.setGlobalSearchPath(textArea.getText().replaceAll("\\r?\\n", File.pathSeparator));
		LoggerWrapper.log(Level.CONFIG, String.format("updated setting 'MagickGlobalSearchPath' set value '%s'", ProcessStarter.getGlobalSearchPath()));
		
		GUISettings.WRAP_TABS = chckbxNewCheckBox_1.isSelected();
		LoggerWrapper.log(Level.CONFIG, String.format("updated setting 'WrapTabs' set value '%s'", GUISettings.WRAP_TABS));
		
		GUISettings.CENTER_IMAGE_ON_RESIZE = chckbxNewCheckBox_1_1.isSelected();
		LoggerWrapper.log(Level.CONFIG, String.format("updated setting 'CenterImageOnResize' set value '%s'", GUISettings.CENTER_IMAGE_ON_RESIZE));
		
		if(chckbxNewCheckBox_2.isSelected())
		{
			GUISettings.FILENAME_COMPARATOR = Comparators.NATURAL_SORT_WIN_EXPLORER;
			LoggerWrapper.log(Level.CONFIG, "updated setting 'FilenameComparator' set value 'WindowsExplorerCompare'");
		}
		else 
		{
			GUISettings.FILENAME_COMPARATOR = Comparators.NATURAL_SORT;
			LoggerWrapper.log(Level.CONFIG, "updated setting 'FilenameComparator' set value 'NaturalOrderCompare'");
		}
	}
	
	
	protected void setupComponents()
	{
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		
		tabPage1 = new JPanel();
		tabPage2 = new JPanel();
		
		tabbedPane.addTab("General Settings", tabPage1);
		tabPage1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		chckbxNewCheckBox_1 = new JCheckBox("Folding Tabs");
		chckbxNewCheckBox_1.setSelected(GUISettings.WRAP_TABS);
		tabPage1.add(chckbxNewCheckBox_1);
		
		chckbxNewCheckBox_1_1 = new JCheckBox("Center Image On Resize");
		chckbxNewCheckBox_1_1.setSelected(GUISettings.CENTER_IMAGE_ON_RESIZE);
		tabPage1.add(chckbxNewCheckBox_1_1);
		
		chckbxNewCheckBox_2 = new JCheckBox("Use Windows Explorer Compare (Slower)");

		tabPage1.add(chckbxNewCheckBox_2);
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
		textArea_1.setText(String.format("Set the environment-variable IM4JAVA_TOOLPATH.\nThis variable should contain a list of directories to search for your tools.\nThey should be separated by your platform-pathdelemiter (on *NIX typically \":\", on Windows \";\")\nYou can also add paths to the box below (1 line per directory).\nDetected path delimeter \"%s\"", File.pathSeparator));
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
