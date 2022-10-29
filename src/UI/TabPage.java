package UI;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import UI.Events.ImageTabNameChangedEvent;
import UI.Events.ImageTabPathChangedEvent;
import UI.Events.Listeners.ImageTabPageListener;
import UI.ImageDisplay.ImageTabPage;

public class TabPage extends JTabbedPane implements ImageTabPageListener, ChangeListener
{
	public TabPage()
	{
		super();
		
		super.addChangeListener(this);
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
		   
		if(!(c instanceof ImageTabPage) || c == null)
			return;
		
		((ImageTabPage)c).setLoadOnce();
	}
}
