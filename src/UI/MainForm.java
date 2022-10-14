package UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.im4java.core.IM4JavaException;

import Configuration.GUISettings;
import Graphics.Imaging.IMAGE;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import UI.ComboBox.Items.ComboBoxItemInt;
import UI.Events.ImageZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.ImageDisplay;
import UI.ImageDisplay.Enums.AntiAliasing;
import UI.ImageDisplay.Enums.InterpolationMode;
import UI.ImageDisplay.Enums.RenderQuality;

public class MainForm extends JFrame implements ImageDisplayListener
{
	protected static final Logger logger = Logger.getLogger(MainForm.class.getName());
	
	private boolean _preventOverflow = false;
	
	Icon iconMenu = UIManager.getIcon("html.pendingImage");
	
	JPanel mainContentPanel, splitPaneLeftPanel;
	JMenuBar menuBar;
    JToolBar toolBar;
    JButton barSave, barEdit, barClear, barDelete;
    JSplitPane mainSplitPane ;
    ImageDisplay mainDisplay;
    JSpinner zoomPercentSpinner;
    
    ItemListener ilToggleAlwaysOnTop = new ItemListener() {
		public void itemStateChanged(ItemEvent e) 
		{
			setAlwaysOnTop(e.getStateChange() == ItemEvent.SELECTED);
		}
	};
	ItemListener ilToggleLeftPane = new ItemListener() {
		private int loc = 0;
        
		public void itemStateChanged(ItemEvent e) 
		{
			if(e.getStateChange() != ItemEvent.SELECTED) 
	           {
	              loc = mainSplitPane.getDividerLocation();
	              mainSplitPane.setDividerSize(0);
	              mainSplitPane.getLeftComponent().setVisible(false);
	           } 
	           else 
	           {
	        	   mainSplitPane.getLeftComponent().setVisible(true);
	        	   mainSplitPane.setDividerLocation(loc);
	        	   mainSplitPane.setDividerSize(GUISettings.MAIN_SPLIT_PANE_DIVISOR_SIZE);
	           }
		}
	};
	
	ActionListener askSaveImage = new ActionListener() 
	{
    	public void actionPerformed(ActionEvent e) 
    	{
    		File f = DialogHelper.askSaveFile();
    		
    		if(f.getPath() == "")
    			return;
    		
    		try 
    		{
				IMAGE.saveImage(mainDisplay.getImage(), f);
    		}
    		catch (ImageUnsupportedException e1) 
    		{
    			logger.log(Level.WARNING, "Could not save image %s imageMagick is required or the format is not supportd:\nMessage: %s".formatted(f.getAbsolutePath(), e1.getMessage()), e1);
			}
    	}
    };
     
     ActionListener alAskOpenFile = new ActionListener() 
     {
         public void actionPerformed(ActionEvent ae) 
         {
            File f = DialogHelper.askChooseFile();
            
            if(!f.exists())
            {
            	return;
            }
            
            mainDisplay.tryLoadImage(f.getPath(), true);
         }
      };
      
      ChangeListener chzoomPercentSpinnerChanged = new ChangeListener() 
      {
      	public void stateChanged(ChangeEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		
      		mainDisplay.setZoomPercent((int)(double)zoomPercentSpinner.getValue());
      		
      		_preventOverflow = false;
    	}
      };
      private JComboBox<ComboBoxItemInt> comboboxInterpolationMode;
      
    
      ItemListener interpolationModeChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(mainDisplay == null)
      			return;
      		
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		mainDisplay.setInterpolationMode(i.getValue());
      	}
      };
      
      ItemListener renderQualityChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(mainDisplay == null)
      			return;
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		mainDisplay.setRenderQuality(i.getValue());
      	}
      };
      ItemListener antiAliasingChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(mainDisplay == null)
      			return;
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		mainDisplay.setRenderQuality(i.getValue());
      	}
      };
      private JMenu mnNewMenu_3;
      private JComboBox<ComboBoxItemInt> comboBoxRenderQuality;
      private JComboBox<ComboBoxItemInt> comboBoxAntiAliasingMode;
      private JCheckBoxMenuItem chckbxmntmNewCheckItem;
      private JCheckBoxMenuItem chckbxmntmNewCheckItem_1;
      private JMenuItem mntmNewMenuItem_2;
      
      
    protected void initComboBox()
    {
    	comboboxInterpolationMode = new JComboBox<ComboBoxItemInt>();
        comboboxInterpolationMode.addItemListener(this.interpolationModeChangedListener);
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Nearest Neighbor", InterpolationMode.NEAREST_NEIGHBOR));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Bicubic", InterpolationMode.BICUBIC));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Bilinear", InterpolationMode.BILINEAR));
        
        comboBoxRenderQuality = new JComboBox<ComboBoxItemInt>();
        comboBoxRenderQuality.addItemListener(renderQualityChangedListener);
		comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Default", RenderQuality.DEFAULT));
		comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Quality", RenderQuality.QUALITY));
		comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Fast", RenderQuality.FAST));
		
		comboBoxAntiAliasingMode = new JComboBox<ComboBoxItemInt>();
		comboBoxAntiAliasingMode.addItemListener(antiAliasingChangedListener);
		comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Default", AntiAliasing.DEFAULT));
		comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Enabled", AntiAliasing.ENABLED));
		comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Disabled", AntiAliasing.DISABLED));
    }
      
	protected void initToolbar() 
	{
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        getContentPane().add(toolBar);

        barSave = new JButton("Open");
        barSave.addActionListener(alAskOpenFile);
        toolBar.add(barSave);

        barEdit = new JButton("Save");
        barEdit.addActionListener(askSaveImage);
        toolBar.add(barEdit);

        barClear = new JButton("Close");
        barClear.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mainDisplay.setImage(null, true);
        	}
        });
        toolBar.add(barClear);

        barDelete = new JButton("Delete");
        toolBar.add(barDelete);
        
        SpinnerModel model = new SpinnerNumberModel(100d, ImageDisplay.MIN_ZOOM_PERCENT, ImageDisplay.MAX_ZOOM_PERCENT, GUISettings.MAIN_ZOOM_SPINNER_CHANGE_VALUE);     
        zoomPercentSpinner = new JSpinner(model);
        zoomPercentSpinner.addChangeListener(chzoomPercentSpinnerChanged);
        
        
        toolBar.add(comboboxInterpolationMode);
        toolBar.add(zoomPercentSpinner);
    }
	
	protected void initMenuBar()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Open");
		mntmNewMenuItem.addActionListener(alAskOpenFile);
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Save");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenu mnNewMenu_1 = new JMenu("Edit");
		menuBar.add(mnNewMenu_1);
		
		mnNewMenu_3 = new JMenu("View");
		menuBar.add(mnNewMenu_3);
		
		
//		mnNewMenu_3.add(comboBoxRenderQuality);
//		mnNewMenu_3.add(comboBoxAntiAliasingMode);
		
		JMenu mnNewMenu_2 = new JMenu("Window");
		menuBar.add(mnNewMenu_2);
		
	
		chckbxmntmNewCheckItem_1 = new JCheckBoxMenuItem("Toggle Left Pane");
		chckbxmntmNewCheckItem_1.setSelected(true);
		chckbxmntmNewCheckItem_1.addItemListener(ilToggleLeftPane);
		mnNewMenu_2.add(chckbxmntmNewCheckItem_1);

		
		chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Always On Top");
		chckbxmntmNewCheckItem.addItemListener(ilToggleAlwaysOnTop);
		mnNewMenu_2.add(chckbxmntmNewCheckItem);
		
		mntmNewMenuItem_2 = new JMenuItem("New menu item");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SettingsDialog().showDialog();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_2);
	}
	
	/**
	 * Create the frame.
	 */
	public MainForm() 
	{
		this.setTitle("Hello World");
		this.setSize(847, 659);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initComboBox();
		initToolbar();
		initMenuBar();
		
		
		mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new GridLayout(1,1));

		mainSplitPane = new JSplitPane();
		
		
		
		splitPaneLeftPanel = new JPanel();
		splitPaneLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnNewButton = new JButton("New button");
		
		splitPaneLeftPanel.add(btnNewButton);
		
		mainDisplay= new ImageDisplay();
		mainDisplay.addImageDisplayListener(this);
		
		
		mainSplitPane.setLeftComponent(splitPaneLeftPanel);
		mainSplitPane.setRightComponent(mainDisplay);
		mainSplitPane.setDividerLocation(250);
		
		
		mainContentPanel.add(mainSplitPane);
		getContentPane().add(mainContentPanel, BorderLayout.CENTER);
        getContentPane().add(toolBar, BorderLayout.NORTH);
        chckbxmntmNewCheckItem_1.setSelected(false);
        this.setLocationRelativeTo(null);
	}

	@Override
	public void ImageZoomChanged(ImageZoomChangedEvent e) 
	{
		if(_preventOverflow)
			return;
		
		 _preventOverflow = true;
		 
		 this.zoomPercentSpinner.setValue(e.getZoomLevelPercent());
		 
		 _preventOverflow = false;
	}
}
