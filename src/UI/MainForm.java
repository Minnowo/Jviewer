package UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Configuration.GUISettings;
import Graphics.ImageUtil;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import UI.ComboBox.Items.ComboBoxItemInt;
import UI.Events.ImageSizeChangedEvent;
import UI.Events.ImageZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.ImageDisplay;
import UI.ImageDisplay.ImageTabPage;
import UI.ImageDisplay.Enums.AntiAliasing;
import UI.ImageDisplay.Enums.ImageDrawMode;
import UI.ImageDisplay.Enums.InterpolationMode;
import UI.ImageDisplay.Enums.RenderQuality;
import javax.swing.JTabbedPane;

public class MainForm extends JFrame implements ImageDisplayListener
{
	protected static final Logger logger = Logger.getLogger(MainForm.class.getName());
	
	private boolean _preventOverflow = false;
	
	Icon iconMenu = UIManager.getIcon("html.pendingImage");
	
	JPanel mainContentPanel, splitPaneLeftPanel, statusPanel;
	JMenuBar menuBar;
    JToolBar toolBar;
    JButton barSave, barEdit, barClear;
    JSplitPane mainSplitPane ;
//    ImageDisplay mainDisplay;
    JSpinner zoomPercentSpinner;
    JLabel statusLabel;
    JComboBox<ComboBoxItemInt> comboboxInterpolationMode;
    private JMenu mnNewMenu_3;
    private JComboBox<ComboBoxItemInt> comboBoxRenderQuality;
    private JComboBox<ComboBoxItemInt> comboBoxAntiAliasingMode;
    private JCheckBoxMenuItem chckbxmntmNewCheckItem;
    private JCheckBoxMenuItem chckbxmntmNewCheckItem_1;
    private JMenuItem mntmNewMenuItem_2;
    private JMenuItem mntmNewMenuItem_3;
    private JMenuItem mntmNewMenuItem_4;
    
    DefaultListModel listModel1 = new DefaultListModel();
    
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
	ItemListener ilToggleImageBorder = new ItemListener() 
	{
		public void itemStateChanged(ItemEvent e) 
		{
			if(e.getStateChange() != ItemEvent.SELECTED) 
            {
				getCurrentDisplay().setDrawBorder(false);
            } 
            else 
            {
            	getCurrentDisplay().setDrawBorder(true);
            }
		}
	};
	
	ActionListener askSaveImage = new ActionListener() { public void actionPerformed(ActionEvent e) { askSaveImage(); } };
     
    ActionListener alAskOpenFileInPlace = new ActionListener() { public void actionPerformed(ActionEvent e) { askOpenFileInPlace(); } };
    
    ActionListener alAskOpenFileInNewTab = new ActionListener() { public void actionPerformed(ActionEvent e) { askOpenFileInNewTab(); } };
      
    ActionListener alCloseCurrentPage = new ActionListener() { public void actionPerformed(ActionEvent e) { closeCurrentImage(); } };
    
    ActionListener alClearCurrentPage = new ActionListener() { public void actionPerformed(ActionEvent e) { clearCurrentImage(); } };
    
    ChangeListener chzoomPercentSpinnerChanged = new ChangeListener() 
      {
      	public void stateChanged(ChangeEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		
      		getCurrentDisplay().setZoomPercentAndZoomCenter((int)(double)zoomPercentSpinner.getValue());

      		_preventOverflow = false;
    	}
      };
      
      
    
      ItemListener interpolationModeChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setInterpolationMode(i.getValue());
      	}
      };
      
      ItemListener renderQualityChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{

      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setRenderQuality(i.getValue());
      	}
      };
      ItemListener antiAliasingChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{

      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setRenderQuality(i.getValue());
      	}
      };
      ItemListener drawModeChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setDrawMode(i.getValue());
      	}
      };
      private JComboBox<ComboBoxItemInt> comboBoxDrawMode;
      private JMenuItem mntmNewMenuItem_5;
      private JMenuItem mntmNewMenuItem_6;
      private JMenuItem mntmNewMenuItem_7;
      private JMenuItem mntmNewMenuItem_8;
      private JMenuItem mntmNewMenuItem_9;
      private JMenuItem mntmNewMenuItem_10;
      private JProgressBar progressBar;

      
      
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
		
		comboBoxDrawMode = new JComboBox<ComboBoxItemInt>();
		comboBoxDrawMode.addItemListener(drawModeChangedListener);
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Free Move", ImageDrawMode.RESIZEABLE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Awlays Fit Image", ImageDrawMode.FIT_IMAGE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Actual Size", ImageDrawMode.ACTUAL_SIZE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Downscale Only", ImageDrawMode.DOWNSCALE_IMAGE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Stretch", ImageDrawMode.STRETCH));
//		comboBoxDrawMode.addItem(new ComboBoxItemInt("DEGUG", 32));
		
    }
      
	protected void initToolbar() 
	{
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        getContentPane().add(toolBar);

        barSave = new JButton("Open");
        barSave.addActionListener(alAskOpenFileInPlace);
        toolBar.add(barSave);

        barEdit = new JButton("Save");
        barEdit.addActionListener(askSaveImage);
        toolBar.add(barEdit);

        barClear = new JButton("Clear");
        barClear.addActionListener(alClearCurrentPage);
        toolBar.add(barClear);
        
        SpinnerModel model = new SpinnerNumberModel(100d, getCurrentDisplay().MIN_ZOOM_PERCENT, getCurrentDisplay().MAX_ZOOM_PERCENT, GUISettings.MAIN_ZOOM_SPINNER_CHANGE_VALUE);     
        zoomPercentSpinner = new JSpinner(model);
        zoomPercentSpinner.addChangeListener(chzoomPercentSpinnerChanged);
        
        btnNewButton = new JButton("Close Page");
        btnNewButton.addActionListener(alCloseCurrentPage);
        toolBar.add(btnNewButton);
        
        
        toolBar.add(comboboxInterpolationMode);
        
        
        toolBar.add(comboBoxDrawMode);
        toolBar.add(zoomPercentSpinner);
    }
	
	protected void initMenuBar()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Open In Place");
		mntmNewMenuItem.addActionListener(alAskOpenFileInPlace);
		mnNewMenu.add(mntmNewMenuItem);
		
		mntmNewMenuItem_13 = new JMenuItem("Open In New Tab");
		mntmNewMenuItem_13.addActionListener(alAskOpenFileInNewTab);
		mnNewMenu.add(mntmNewMenuItem_13);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Save");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenu mnNewMenu_1 = new JMenu("Edit");
		menuBar.add(mnNewMenu_1);
		
		mntmNewMenuItem_6 = new JMenuItem("Rotate 90 Left");
		mntmNewMenuItem_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotateImage(270);
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_6);
		
		mntmNewMenuItem_7 = new JMenuItem("Rotate 90 Right");
		mntmNewMenuItem_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotateImage(90);
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_7);
		
		mntmNewMenuItem_8 = new JMenuItem("Flip Horizontal");
		mntmNewMenuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flipHorizontal();
			}
		});
		
		mntmNewMenuItem_10 = new JMenuItem("Rotate By...");
		mntmNewMenuItem_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				askRotateImage();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_10);
		mnNewMenu_1.add(mntmNewMenuItem_8);
		
		mntmNewMenuItem_9 = new JMenuItem("Flip Vertical");
		mntmNewMenuItem_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flipVertical();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_9);
		
		mntmNewMenuItem_11 = new JMenuItem("Convert Greyscale");
		mntmNewMenuItem_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertGreyscale();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_11);
		
		mntmNewMenuItem_12 = new JMenuItem("Invert Image");
		mntmNewMenuItem_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertInverse();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_12);
		
		mnNewMenu_3 = new JMenu("View");
		menuBar.add(mnNewMenu_3);
		
		mntmNewMenuItem_3 = new JMenuItem("Actual Size");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewActualImageSize();
			}
		});
		mnNewMenu_3.add(mntmNewMenuItem_3);
		
		mntmNewMenuItem_4 = new JMenuItem("Fit To Screen");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fitImageToView();
			}
		});
		mnNewMenu_3.add(mntmNewMenuItem_4);
		
		mntmNewMenuItem_5 = new JMenuItem("Center Image");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				centerImage();
			}
		});
		mnNewMenu_3.add(mntmNewMenuItem_5);
		
		chckbxmntmNewCheckItem_2 = new JCheckBoxMenuItem("Image Outline");
		chckbxmntmNewCheckItem_2.addItemListener(ilToggleImageBorder);
		mnNewMenu_3.add(chckbxmntmNewCheckItem_2);
		
		
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
		
		mntmNewMenuItem_2 = new JMenuItem("Settings");
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
		
		tabbedPane = new TabPage();
		tabbedPane.addTab(ImageTabPage.EMPTY_TAB_PAGE_NAME, new ImageTabPage(tabbedPane));
		
		initComboBox();
		initToolbar();
		initMenuBar();
		
		
		mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new GridLayout(1,1));

		mainSplitPane = new JSplitPane();
		
		
		
		splitPaneLeftPanel = new JPanel();
		
		
		getCurrentDisplay().addImageDisplayListener(this);
		
		
		mainSplitPane.setLeftComponent(splitPaneLeftPanel);
		splitPaneLeftPanel.setLayout(new BorderLayout(0, 0));
		
		
		
		list = new JList();
		list.setModel(listModel1);
		
		splitPaneLeftPanel.add(list);
		mainSplitPane.setRightComponent(tabbedPane);
		
		
		
		tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		mainSplitPane.setDividerLocation(250);
		
		
		// create the status bar panel and shove it down the bottom of the frame
		statusPanel = new JPanel();
//		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
//		statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel("");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);

		
		
		
		mainContentPanel.add(mainSplitPane);
		getContentPane().add(mainContentPanel, BorderLayout.CENTER);
		
		
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        statusPanel.add(progressBar);
        chckbxmntmNewCheckItem_1.setSelected(false);
        this.setLocationRelativeTo(null);
	}

	public void setStatusLabelText()
	{
		if(getCurrentDisplay().getImage() == null)
		{
			statusLabel.setText("");
			return;
		}
		
		statusLabel.setText("  " + getCurrentDisplay().getImageWidth() + " x " + getCurrentDisplay().getImageHeight());
	}
	
	
	public Set<String> listFilesUsingJavaIO(String dir) {
	    return Stream.of(new File(dir).listFiles())
	      .filter(file -> !file.isDirectory())
	      .map(File::getName)
	      .collect(Collectors.toSet());
	}
	
	public void askOpenFileInNewTab()
	{
		File f = DialogHelper.askChooseFile();
        
        if(!f.exists())
        {
        	return;
        }

        ImageTabPage img = new ImageTabPage(tabbedPane);
        tabbedPane.addTab("", img);
			
        img.tryLoadImage(f.getPath(), true);

        setStatusLabelText();
	}
	
	public void askOpenFileInPlace()
	{
		 File f = DialogHelper.askChooseFile();
         
         if(!f.exists())
         {
         	return;
         }

         getCurrentDisplay().tryLoadImage(f.getPath(), true);

         setStatusLabelText();
	}
	
	public void askSaveImage()
	{
		if(getCurrentDisplay().getImage() == null)
			return;
		
		File f = DialogHelper.askSaveFile();
		
		if(f.getPath() == "")
			return;
		
		try 
		{
			ImageUtil.saveImage(getCurrentDisplay().getImage(), f);
		}
		catch (ImageUnsupportedException e1) 
		{
			logger.log(Level.WARNING, "Could not save image %s imageMagick is required or the format is not supportd:\nMessage: %s".formatted(f.getAbsolutePath(), e1.getMessage()), e1);
		}
	}
	
	public void askRotateImage()
	{
		if(getCurrentDisplay().getImage() == null)
			return;
		
		String s = JOptionPane.showInputDialog("Enter the angle to rotate the image");
			
		if(s == null)
			return;
		
		try 
		{
			rotateImage(Double.parseDouble(s));
		}
		catch (NumberFormatException ex) {}
	}
	
	public void viewActualImageSize()
	{
		getCurrentDisplay().showActualImageSize();
	}
	
	public void centerImage()
	{
		getCurrentDisplay().CenterCurrentImageWithoutResize();
	}
	
	public void fitImageToView()
	{
		getCurrentDisplay().CenterCurrentImage();
	}
	
	public void rotateImage(double degree)
	{
		switch ((int)degree) 
		{
		
		case 270:
			getCurrentDisplay().rotate90Left();
			return;
			
		case 90: 
			getCurrentDisplay().rotate90Right();
			return;
		}
		getCurrentDisplay().rotateImage(degree);
	}
	
	public void flipVertical()
	{
		getCurrentDisplay().flipVertical();
	}
	
	public void flipHorizontal()
	{
		getCurrentDisplay().flipHorizontal();
	}
	
	public void convertGreyscale()
	{
		getCurrentDisplay().setGreyscale();
	}
	
	public void convertInverse()
	{
		getCurrentDisplay().setInverse();
	}
	
	Runnable runProgressBar = new Runnable() {
	       public void run() {
	    	   progressBar.setVisible(true);
	    	   progressBar.setIndeterminate(true);
	       }
	     };
	private JCheckBoxMenuItem chckbxmntmNewCheckItem_2;
	private JMenuItem mntmNewMenuItem_11;
	private JMenuItem mntmNewMenuItem_12;
	private JList list;
	private TabPage tabbedPane;
	private JMenuItem mntmNewMenuItem_13;
	private JButton btnNewButton;
	     
	public void setProgress(boolean rue)
	{
		SwingUtilities.invokeLater(runProgressBar);   
	}
	
	public void resetProgressbar()
	{
		progressBar.setVisible(false);
		progressBar.setIndeterminate(false);
	}
	
	public void clearCurrentImage()
	{
		getCurrentDisplay().setImage(null, true);
		setStatusLabelText();
	}
	
	public void closeCurrentImage()
	{
		if(tabbedPane.getTabCount() == 1)
			return;
		
		ImageTabPage tp = getCurrentDisplay();
		
		tp.setImage(null, true);
		
		tabbedPane.remove(tp.getCurrentTabIndex());
		
		setStatusLabelText();
	}
	
	public ImageTabPage getCurrentDisplay()
	{
		return (ImageTabPage)this.tabbedPane.getSelectedComponent();
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

	@Override
	public void ImageSizeChanged(ImageSizeChangedEvent e) 
	{
		setStatusLabelText();
	}
}
