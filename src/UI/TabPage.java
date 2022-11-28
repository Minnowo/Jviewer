package UI;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import UI.Events.ImageTabNameChangedEvent;
import UI.Events.ImageTabPathChangedEvent;
import UI.Events.Listeners.ImageTabPageListener;
import UI.ImageDisplay.ImageTabPage;
import Util.DragAndDropHelper.FileDragHandler;

public class TabPage extends JTabbedPane implements ImageTabPageListener, ChangeListener, MouseListener
{
	private boolean dragButtonDown = false;
	private boolean dragDropStarted = false;
	
	public int mouseDragButton = MouseEvent.BUTTON1;
	
	public TabPage()
	{
		super();
		
		super.addChangeListener(this);
		super.addMouseListener(this);
		this.setTransferHandler(new FileDragHandler());
	}
	
	
	@Override
	public void addTab(String title, Component component)
	{
		this.addTab(title, (ImageTabPage)component);
	}
	
	public void addTab(String title, ImageTabPage component)
	{
		super.addTab(title, component);
		
		component.setTabIndex(super.getTabCount() - 1);
		
		component.addImageTabPageListener(this);
	}
	
	@Override
	public void remove(int index)
	{
		super.remove(index);
		
		int totalTabs = super.getTabCount();
		
		for(int i = 0; i < totalTabs; i++)
		{
			Component c = super.getComponentAt(i);
		   
			if(!(c instanceof ImageTabPage) || c == null)
				continue;
			
			((ImageTabPage)c).setTabIndex(i);
		}
	}
	
	@Override
	public void imagePathChanged(ImageTabPathChangedEvent e) 
	{
		if(e.getCurrentTabIndex() < 0 || e.getCurrentTabIndex() >= super.getTabCount())
			return;
		
		super.setToolTipTextAt(e.getCurrentTabIndex(), e.getAfter());
	}

	@Override
	public void imageTabNameChanged(ImageTabNameChangedEvent e) 
	{
		if(e.getCurrentTabIndex() < 0 || e.getCurrentTabIndex() >= super.getTabCount())
			return;
		
		super.setTitleAt(e.getCurrentTabIndex(), e.getNewname());	
	}

	
	public void stateChanged(ChangeEvent e)
	{
        int selectedIndex = super.getSelectedIndex();
        
        Component c = super.getComponentAt(selectedIndex);
		   
		if(c == null || !(c instanceof ImageTabPage))
			return;
		
		((ImageTabPage)c).setLoadOnce();
	}


	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }


	@Override
	public void mousePressed(MouseEvent e) 
	{
		if(e.getButton() == mouseDragButton)
		{
			this.dragButtonDown = true;
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) 
	{
		this.dragButtonDown = false;
		this.dragDropStarted = false;
	}


	// this function handles dragging and dropping files into other applications,
	// if you click on the tab page component and drag the mouse out of it, file drag drop begins
	@Override
	public void mouseExited(MouseEvent e) 
	{
		if(!dragButtonDown || dragDropStarted)
			return;
		
		final JComponent      c       = (JComponent)e.getSource();
		final TransferHandler handler = c.getTransferHandler();
		
		if(handler == null || !(handler instanceof FileDragHandler))
			return;
		
		
		final FileDragHandler fdh           = (FileDragHandler)handler;
		final int             selectedIndex = super.getSelectedIndex();
        final Component       tabPage__     = super.getComponentAt(selectedIndex);
		   
		if(tabPage__ == null || !(tabPage__ instanceof ImageTabPage))
			return;
		
		File f = ((ImageTabPage)tabPage__).getCurrentPathOrTempPath();
		
		if(f == null)
			return;
		
		ArrayList<File> files = fdh.getFiles();
		files.clear();
		files.add(f);
		
		dragDropStarted = true;
		handler.exportAsDrag(c, e, TransferHandler.COPY);
	}
}
