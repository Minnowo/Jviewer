package nyaa.alice.jviewer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.im4java.process.ProcessStarter;
import org.tinylog.Logger;

import nyaa.alice.jviewer.data.StringComparator;
import nyaa.alice.jviewer.system.GeneralSettings;
import nyaa.alice.jviewer.system.ImageMagickSettings;

public class SettingsDialog extends JDialog
{
    private JTabbedPane tabbedPane;
    private JPanel tabPage1, tabPage2, tabPage3;
    private JTextField textField;
    private JPanel panel;
    private JTextArea textArea;
    private JPanel panel_1;
    private JCheckBox chckbxNewCheckBox;
    private JCheckBox chckbxNewCheckBox_1;
    private JTextArea textArea_1;
    private JCheckBox chckbxNewCheckBox_1_1;
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
        ImageMagickSettings.useImageMagick = chckbxNewCheckBox.isSelected();
        Logger.info("Updated setting 'UseImageMagick' set value '{}'", ImageMagickSettings.useImageMagick);

        ProcessStarter.setGlobalSearchPath(textArea.getText().replaceAll("\\r?\\n", File.pathSeparator));
        Logger.info("updated setting 'MagickGlobalSearchPath' set value '{}'",
                ProcessStarter.getGlobalSearchPath());

        GeneralSettings.WRAP_TABS = chckbxNewCheckBox_1.isSelected();
        Logger.info("updated setting 'WrapTabs' set value '{}'", GeneralSettings.WRAP_TABS);

        GeneralSettings.CENTER_IMAGE_ON_RESIZE = chckbxNewCheckBox_1_1.isSelected();
        Logger.info("updated setting 'CenterImageOnResize' set value '{}'",
                GeneralSettings.CENTER_IMAGE_ON_RESIZE);

        if (chckbxNewCheckBox_2.isSelected())
        {
            GeneralSettings.FILENAME_COMPARATOR = StringComparator.NATURAL_SORT_WIN_EXPLORER;
            Logger.info("updated setting 'FilenameComparator' set value 'WindowsExplorerCompare'");
        }
        else
        {
            GeneralSettings.FILENAME_COMPARATOR = StringComparator.NATURAL_SORT;
            Logger.info("updated setting 'FilenameComparator' set value 'NaturalOrderCompare'");
        }
    }

    protected void setupComponents()
    {
        getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane);

        tabPage1 = new JPanel();
        tabPage2 = new JPanel();
        tabPage3 = new JPanel();

        tabbedPane.addTab("General Settings", tabPage1);
        tabPage1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        chckbxNewCheckBox_1 = new JCheckBox("Folding Tabs");
        chckbxNewCheckBox_1.setSelected(GeneralSettings.WRAP_TABS);
        tabPage1.add(chckbxNewCheckBox_1);

        chckbxNewCheckBox_1_1 = new JCheckBox("Center Image On Resize");
        chckbxNewCheckBox_1_1.setSelected(GeneralSettings.CENTER_IMAGE_ON_RESIZE);
        tabPage1.add(chckbxNewCheckBox_1_1);

        chckbxNewCheckBox_2 = new JCheckBox("Use Windows Explorer Compare (Slower)");

        tabPage1.add(chckbxNewCheckBox_2);
        tabbedPane.addTab("Magick Settings", tabPage2);
        tabPage2.setLayout(new BoxLayout(tabPage2, BoxLayout.Y_AXIS));

        tabbedPane.addTab("Keybindings", tabPage3);
        
        panel_1 = new JPanel();
        tabPage2.add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));

        panel = new JPanel();
        panel_1.add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 157, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 22, 96, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        textArea_1 = new JTextArea();
        textArea_1.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea_1.setWrapStyleWord(true);
        textArea_1.setLineWrap(true);
        textArea_1.setText(String.format(
                "Set the environment-variable IM4JAVA_TOOLPATH.\nThis variable should contain a list of directories to search for your tools.\nThey should be separated by your platform-pathdelemiter (on *NIX typically \":\", on Windows \";\")\nYou can also add paths to the box below (1 line per directory).\nDetected path delimeter \"%s\"",
                File.pathSeparator));
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
        chckbxNewCheckBox.setSelected(ImageMagickSettings.useImageMagick);

        if (ProcessStarter.getGlobalSearchPath() != null)
        {
            textArea.setText(ProcessStarter.getGlobalSearchPath().replace(';', '\n'));
        }

    }
}
