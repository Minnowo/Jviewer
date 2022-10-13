package UI;

import java.awt.*;
import javax.swing.*;

public class Test {

    static JFrame frame;
    static JPanel panel, panelData;
    static JMenuBar menuBar;
    static JMenu menuFile, menuSetting, menuOption;
    static JMenuItem itemNew, itemOpen, itemSave, itemPrint, itemOption, itemClose, itemView, itemReports;
    static JToolBar toolBar;
    static Icon iconMenu = UIManager.getIcon("html.pendingImage");
    static JButton barSave, barEdit, barClear, barDelete;
    static ButtonGroup group;
    static JRadioButtonMenuItem subFont1, subFont2, subFont3, subFont4, subFont5;
    static JCheckBoxMenuItem checkPrefer;
    static JLabel label;
    static JTextField textFirst, textMiddle, textLast;
    static JCheckBox checkGender;


    public static void gui() {

        frame = new JFrame("Complete GridBag Layout Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        Test myMenu = new Test();
        myMenu.myMenuBar();
        myMenu.myToolBar();
        
        frame.setJMenuBar(menuBar);
        frame.add(dataPanel(), BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.NORTH);
        
        frame.pack();
        frame.setVisible(true);
    }

    public static JPanel dataPanel() {

        panelData = new JPanel();
        label = new JLabel("First Name: ");
        textFirst = new JTextField(10);
        panelData.setLayout(new GridBagLayout());
        GridBagConstraints bagData = new GridBagConstraints();
        int i = 0;
        bagData.gridx = 1;
        bagData.gridy = i;
        bagData.gridwidth = 2;
        bagData.fill = GridBagConstraints.HORIZONTAL;
        panelData.add(textFirst, bagData);

        bagData.gridx = 0;
        bagData.gridy = i;
        bagData.gridwidth = 1;
        bagData.fill = GridBagConstraints.NONE;
        panelData.add(label, bagData);
        i++;
        return panelData;
    }

    public void myToolBar() {

        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        frame.add(toolBar);

        barSave = new JButton("Save", iconMenu);
        toolBar.add(barSave);

        barEdit = new JButton("Edit", iconMenu);
        toolBar.add(barEdit);

        barClear = new JButton("Clear", iconMenu);
        toolBar.add(barClear);

        barDelete = new JButton("Delete", iconMenu);
        toolBar.add(barDelete);
    }

    public void myMenuBar() {
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        //Menus
        menuFile = new JMenu("File");
        menuBar.add(menuFile);

        itemNew = new JMenuItem("New", iconMenu);
        menuFile.add(itemNew);

        itemOpen = new JMenuItem("Open", iconMenu);
        menuFile.add(itemOpen);

        itemSave = new JMenuItem("Save", iconMenu);
        menuFile.add(itemSave);

        menuFile.addSeparator();

        itemPrint = new JMenuItem("Print", iconMenu);
        menuFile.add(itemPrint);
        menuFile.addSeparator();

        menuSetting = new JMenu("Settings");
        menuFile.add(menuSetting);
        group = new ButtonGroup();

        subFont1 = new JRadioButtonMenuItem("10 Pixel");
        menuSetting.add(subFont1);
        group.add(subFont1);

        subFont2 = new JRadioButtonMenuItem("12 Pixel");
        menuSetting.add(subFont2);
        group.add(subFont2);

        subFont3 = new JRadioButtonMenuItem("14 Pixel");
        menuSetting.add(subFont3);
        group.add(subFont3);

        subFont4 = new JRadioButtonMenuItem("16 Pixel");
        menuSetting.add(subFont4);
        group.add(subFont4);

        subFont5 = new JRadioButtonMenuItem("24 Pixel");
        menuSetting.add(subFont5);
        group.add(subFont5);
        menuSetting.addSeparator();

        checkPrefer = new JCheckBoxMenuItem("Preference");
        menuSetting.add(checkPrefer);
        group.add(checkPrefer);

        itemClose = new JMenuItem("Close", iconMenu);
        menuFile.add(itemClose);

        menuOption = new JMenu("Option");
        menuBar.add(menuOption);

        itemView = new JMenuItem("View Users", iconMenu);
        menuOption.add(itemView);

        itemReports = new JMenuItem("Reports", iconMenu);
        menuOption.add(itemReports);
    }
}