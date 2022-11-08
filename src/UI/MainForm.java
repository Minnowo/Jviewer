package UI;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.im4java.process.ProcessStarter;

import Configuration.GUISettings;
import Configuration.KeyboardSettings;
import Graphics.Imaging.IMAGE;
import Graphics.Imaging.ImageBase;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Gif.GIF;
import UI.ComboBox.Items.ComboBoxItemInt;
import UI.Events.ImageDisplayImageSizeChangedEvent;
import UI.Events.ImageDisplayZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.ImageTabPage;
import UI.ImageDisplay.Enums.AntiAliasing;
import UI.ImageDisplay.Enums.ImageDrawMode;
import UI.ImageDisplay.Enums.InterpolationMode;
import UI.ImageDisplay.Enums.RenderQuality;
import Util.ClipboardHelper;
import Util.Logging.LogUtil;

public class MainForm extends JFrame implements ImageDisplayListener, ChangeListener 
{
	protected static final Logger logger = LogUtil.getLogger(MainForm.class.getName());
	
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
    
    
    SpinnerNumberModel gifFrameNumberModel = new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1));
    
    DefaultListModel listModel1 = new DefaultListModel();
    
    class KeyAction extends AbstractAction 
    {
    	public final String actionName;
    	public final KeyStroke keystroke;
    	private final Runnable action;
    	
    	public KeyAction(String actionName, KeyStroke key, final Runnable action) 
    	{
			this.actionName = actionName;
			this.action = action;
			this.keystroke = key;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			 if ( SwingUtilities.isEventDispatchThread()) 
			 {
				 this.action.run();
			 } 
			 else 
			 {
			     SwingUtilities.invokeLater(this.action);
			 }
		}
	}
    
    final KeyAction PASTE_IMAGE = new KeyAction("PasteImage", KeyboardSettings.PASTE_IMAGE_KEY, this::pasteImage);
    final KeyAction COPY_IMAGE = new KeyAction("CopyImage", KeyboardSettings.COPY_IMAGE_KEY, this::copyImage);

    
    
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
			getCurrentDisplay().setDrawBorder(e.getStateChange() != ItemEvent.SELECTED);
		}
	};
	
	ItemListener ilToggleAnimation = new ItemListener() 
	{
		public void itemStateChanged(ItemEvent e) 
		{
			if(_preventOverflow)
				return;
			
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				getCurrentDisplay().setAnimationPaused(true);
				
				if(getCurrentDisplay().getImage() != null && getCurrentDisplay().getImage().GetImageFormat() == ImageFormat.GIF)
				{
					_preventOverflow = true;
					
					gifFrameSpinner.setValue(((GIF)getCurrentDisplay().getImage()).getFrameIndex());
					
					_preventOverflow = false;
				}
			}
			else 
			{
				getCurrentDisplay().setAnimationPaused(false);
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
      
      ChangeListener gifFrameSpinnerChanged = new ChangeListener() 
      {
      	public void stateChanged(ChangeEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		
      		getCurrentDisplay().setAnimationFrame((int)gifFrameSpinner.getValue());
      		
      		chckbxmntmNewCheckItem_3.setState(true);

      		_preventOverflow = false;
    	}
      };
    
      ItemListener interpolationModeChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setInterpolationMode(i.getValue());
      		_preventOverflow = false;
      	}
      };
      
      ItemListener renderQualityChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setRenderQuality(i.getValue());
      		_preventOverflow = false;
      	}
      };
      ItemListener antiAliasingChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setRenderQuality(i.getValue());
      		_preventOverflow = false;
      	}
      };
      ItemListener drawModeChangedListener = new ItemListener() 
      {
      	public void itemStateChanged(ItemEvent e) 
      	{
      		if(_preventOverflow)
      			return;
      		
      		_preventOverflow = true;
      		ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
      		getCurrentDisplay().setDrawMode(i.getValue());
      		
      		_preventOverflow = false;
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
        
        // NOTE: the order in which these are added are the order of increment
     	// 0th index in the combobox is the InterpolationMode.DEFAULT because it has a value of 0
        // this is used when switching tabs to easily set the combobox to the correct value via the index
        // see this.stateChanged
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Default", InterpolationMode.DEFAULT));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Nearest Neighbor", InterpolationMode.NEAREST_NEIGHBOR));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Bilinear", InterpolationMode.BILINEAR));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Bicubic", InterpolationMode.BICUBIC));
        
        
        comboBoxRenderQuality = new JComboBox<ComboBoxItemInt>();
        comboBoxRenderQuality.addItemListener(renderQualityChangedListener);
        
        
        // NOTE: the order in which these are added are the order of increment
     	// 0th index in the combobox is the RenderQuality.DEFAULT because it has a value of 0
     // this is used when switching tabs to easily set the combobox to the correct value via the index
        // see this.stateChanged
		comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Default", RenderQuality.DEFAULT));
		comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Fast", RenderQuality.FAST));
		comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Quality", RenderQuality.QUALITY));
		
		
		comboBoxAntiAliasingMode = new JComboBox<ComboBoxItemInt>();
		comboBoxAntiAliasingMode.addItemListener(antiAliasingChangedListener);
		
		// NOTE: the order in which these are added are the order of increment
		// 0th index in the combobox is the AntiAliasing.DEFAULT because it has a value of 0
		// this is used when switching tabs to easily set the combobox to the correct value via the index
        // see this.stateChanged
		comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Default", AntiAliasing.DEFAULT));
		comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Disabled", AntiAliasing.DISABLED));
		comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Enabled", AntiAliasing.ENABLED));
		
		
		comboBoxDrawMode = new JComboBox<ComboBoxItemInt>();
		comboBoxDrawMode.addItemListener(drawModeChangedListener);
		
		// NOTE: the order in which these are added are the order of increment
		// 0th index in the combobox is ImageDrawMode.RESIZEABLE because it has a value of 0
		// this is used when switching tabs to easily set the combobox to the correct value via the index
        // see this.stateChanged
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Free Move", ImageDrawMode.RESIZEABLE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Awlays Fit Image", ImageDrawMode.FIT_IMAGE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Downscale Only", ImageDrawMode.DOWNSCALE_IMAGE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Actual Size", ImageDrawMode.ACTUAL_SIZE));
		comboBoxDrawMode.addItem(new ComboBoxItemInt("Stretch", ImageDrawMode.STRETCH));
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
        zoomPercentSpinner.setToolTipText("Zoom Percentage");
        zoomPercentSpinner.addChangeListener(chzoomPercentSpinnerChanged);
        
        btnNewButton = new JButton("Close Page");
        btnNewButton.addActionListener(alCloseCurrentPage);
        toolBar.add(btnNewButton);
        

        gifFrameSpinner = new JSpinner();
        gifFrameSpinner.addChangeListener(gifFrameSpinnerChanged);
        gifFrameSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        gifFrameSpinner.setToolTipText("Gif Frame");
        gifFrameSpinner.setVisible(false);
        
        toolBar.add(comboboxInterpolationMode);
        
        
        toolBar.add(comboBoxDrawMode);
        toolBar.add(zoomPercentSpinner);
        toolBar.add(gifFrameSpinner);
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
		
		chckbxmntmNewCheckItem_3 = new JCheckBoxMenuItem("Pause Animation");
		chckbxmntmNewCheckItem_3.addItemListener(ilToggleAnimation);
		mnNewMenu_3.add(chckbxmntmNewCheckItem_3);
		
		
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
	


	public MainForm() 
	{
		this.setTitle("Hello World");
		this.setSize(847, 659);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		tabbedPane = new TabPage();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab(ImageTabPage.EMPTY_TAB_PAGE_NAME, new ImageTabPage(tabbedPane));
		tabbedPane.addChangeListener(this);
		
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
        setupKeybindings();
	}

	
	private void setupKeybindings()
	{
		if(tabbedPane == null)
			return;
		
		final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
		
		tabbedPane.getInputMap(IFW).put(PASTE_IMAGE.keystroke, PASTE_IMAGE.actionName);
	    tabbedPane.getActionMap().put(PASTE_IMAGE.actionName, PASTE_IMAGE);
	    
	    tabbedPane.getInputMap(IFW).put(COPY_IMAGE.keystroke, COPY_IMAGE.actionName);
	    tabbedPane.getActionMap().put(COPY_IMAGE.actionName, COPY_IMAGE);
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
	
	public void openInNewTab(ImageBase image)
	{
		ImageTabPage img = new ImageTabPage(tabbedPane);
        tabbedPane.addTab("%d x %d".formatted(image.getWidth(), image.getHeight()), img);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        
        img.addImageDisplayListener(this);
        	
        img.setImage(image, true);

        setStatusLabelText();
	}
	
	public void openInNewTab(File f)
	{
		ImageTabPage img = new ImageTabPage(tabbedPane);
        tabbedPane.addTab("", img);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        
        img.addImageDisplayListener(this);
        	
        img.tryLoadImage(f.getPath(), true);

        setStatusLabelText();
	}
	
	public void openInPlace(File f)
	{

        getCurrentDisplay().tryLoadImage(f.getPath(), true);

        setStatusLabelText();
	}
	
	public void askOpenFileInNewTab()
	{
		File f = DialogHelper.askChooseFile();
        
        if(!f.exists())
        {
        	return;
        }
        
        openInNewTab(f);
	}
	
	public void askOpenFileInPlace()
	{
		 File f = DialogHelper.askChooseFile();
         
         if(!f.exists())
         {
         	return;
         }

         openInPlace(f);
	}
	
	public void askSaveImage()
	{
		if(getCurrentDisplay().getImage() == null)
			return;
		
		File f = DialogHelper.askSaveFile();
		
		if(f.getPath() == "")
			return;
		
//		try 
//		{
//			ImageUtil.saveImage(getCurrentDisplay().getImage(), f);
//		}
//		catch (ImageUnsupportedException e1) 
//		{
//			logger.log(Level.WARNING, "Could not save image %s imageMagick is required or the format is not supportd:\nMessage: %s".formatted(f.getAbsolutePath(), e1.getMessage()), e1);
//		}
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
	
	
	int openedImaegs = 0;
	private JCheckBoxMenuItem chckbxmntmNewCheckItem_3;
	private JSpinner gifFrameSpinner;
	public void handleStartArgument(String arg)
	{
		if(arg.contains("="))
		{
			int index = arg.indexOf('=');
			
			String key = arg.substring(0, index).toLowerCase();
			String value = arg.substring(index + 1);
			
			
			switch (key) 
			{
				case "im4java_toolpath":
					
					ProcessStarter.setGlobalSearchPath(value);
					
					return;
			}
			
			return;
		}
		
		
		File f = new File(arg);
		
		// limit to 5 for now to prevent computer dying here from accidentally opening way more 
		// TODO: make this better isntead of just a limit of 5
		if(f.exists() && !(openedImaegs > 5))
		{
			openedImaegs++;
			
			openInNewTab(f);
		}
	}
	
	public void handleStartArguments(String[] args)
	{
		for(String a : args)
		{
			handleStartArgument(a);
		}
		
		openedImaegs = 0;
	}
	
	public void bringToFront()
	{
		if(getState() != Frame.NORMAL) 
		{ 
			setState(Frame.NORMAL); 
		}
		
		toFront();
		repaint();
	}
	
	
	public void pasteImage()
	{
		BufferedImage img = ClipboardHelper.getClipboardImage();
		
		if(img == null)
			return;
		
		openInNewTab(IMAGE.fromBuffered(img));
	}
	
	public void copyImage()
	{
		if(getCurrentDisplay() == null || getCurrentDisplay().getImage() == null)
			return;
		
		ClipboardHelper.copyImageToClipboard(getCurrentDisplay().getImage().getBuffered());
	}
	
	public void updateGifAnimationStuff()
	{
		if(_preventOverflow)
   			return;
   		
		 _preventOverflow = true;
		 
		 ImageTabPage tp = getCurrentDisplay();
		 ImageBase i = tp.getImage();
		 
		
		 if(i != null && i.GetImageFormat() == ImageFormat.GIF)
		 {
			 GIF g = (GIF) i;
			 
			 gifFrameNumberModel.setMaximum(Integer.valueOf(g.getFrameCount() - 1));
			 gifFrameNumberModel.setValue(g.getFrameIndex());
			 gifFrameSpinner.setModel(gifFrameNumberModel);
			 
			 gifFrameSpinner.setVisible(true);
			 
			 chckbxmntmNewCheckItem_3.setSelected(tp.getAnimationPaused());
		 }
		 else 
		 {
			gifFrameSpinner.setVisible(false);
		 }
		 
		 _preventOverflow = false;
	}
	
	@Override
	public void ImageZoomChanged(ImageDisplayZoomChangedEvent e) 
	{
		if(_preventOverflow)
			return;
		
		 _preventOverflow = true;
		 
		 this.zoomPercentSpinner.setValue(e.getZoomLevelPercent());
		 
		 _preventOverflow = false;
	}

	@Override
	public void ImageSizeChanged(ImageDisplayImageSizeChangedEvent e) 
	{
		setStatusLabelText();
		updateGifAnimationStuff();
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{
		if(_preventOverflow)
   			return;
   		
   		 _preventOverflow = true;
   		 
   		 ImageTabPage tp = getCurrentDisplay();
   		 
   		 zoomPercentSpinner.setValue(tp.getZoomPercent());
   		 
   		 comboBoxAntiAliasingMode.setSelectedIndex(tp.getAntiAliasing());
   		 comboBoxDrawMode.setSelectedIndex(tp.getDrawMode());
   		 comboboxInterpolationMode.setSelectedIndex(tp.getInterpolationMode());
   		 comboBoxRenderQuality.setSelectedIndex(tp.getRenderQuality());
      	
   		 _preventOverflow = false;
   		 
   		 updateGifAnimationStuff();
	}

	@Override
	public void ImageChanged() 
	{
		updateGifAnimationStuff();	
	}
}
