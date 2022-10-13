package UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Configuration.GUISettings;
import UI.Events.ImageZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.GraphicsFrame;

public class MainForm extends JFrame implements ImageDisplayListener
{
	private boolean _preventOverflow = false;
	
	Icon iconMenu = UIManager.getIcon("html.pendingImage");
	
	JPanel mainContentPanel, splitPaneLeftPanel;
	JMenuBar menuBar;
    JToolBar toolBar;
    JButton barSave, barEdit, barClear, barDelete;
    JSplitPane mainSplitPane ;
    GraphicsFrame mainDisplay;
    JSpinner zoomPercentSpinner;
    
    ActionListener alToggleLeftPane = new ActionListener() 
    {
        private int loc = 0;
        
        public void actionPerformed(ActionEvent ae) 
        {
           if(mainSplitPane.getLeftComponent().isVisible()) 
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
      
    
	protected void initToolbar() 
	{
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        getContentPane().add(toolBar);

        barSave = new JButton("Save");
        toolBar.add(barSave);

        barEdit = new JButton("Edit");
        toolBar.add(barEdit);

        barClear = new JButton("Clear");
        toolBar.add(barClear);

        barDelete = new JButton("Delete");
        toolBar.add(barDelete);
        
        SpinnerModel model = new SpinnerNumberModel(100d, GraphicsFrame.MIN_ZOOM_PERCENT, GraphicsFrame.MAX_ZOOM_PERCENT, GUISettings.MAIN_ZOOM_SPINNER_CHANGE_VALUE);     
        zoomPercentSpinner = new JSpinner(model);
        zoomPercentSpinner.addChangeListener(chzoomPercentSpinnerChanged);
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
		
		JMenu mnNewMenu_2 = new JMenu("Window");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Toggle Left Pane");
		mntmNewMenuItem_2.addActionListener(alToggleLeftPane);
		mnNewMenu_2.add(mntmNewMenuItem_2);
	}
	
	/**
	 * Create the frame.
	 */
	public MainForm() 
	{
		this.setTitle("Hello World");
		this.setBounds(100, 100, 847, 659);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initToolbar();
		initMenuBar();
		
		
		mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new GridLayout(1,1));

		mainSplitPane = new JSplitPane();
		
		
		
		splitPaneLeftPanel = new JPanel();
		splitPaneLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnNewButton = new JButton("New button");
		
		splitPaneLeftPanel.add(btnNewButton);
		
		mainDisplay= new GraphicsFrame();
		mainDisplay.addImageDisplayListener(this);
		
		
		mainSplitPane.setLeftComponent(splitPaneLeftPanel);
		mainSplitPane.setRightComponent(mainDisplay);
		mainSplitPane.setDividerLocation(250);
		
		
		mainContentPanel.add(mainSplitPane);
		getContentPane().add(mainContentPanel, BorderLayout.CENTER);
        getContentPane().add(toolBar, BorderLayout.NORTH);
     
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
