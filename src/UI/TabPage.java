package UI;

import java.awt.Component;
import java.io.File;

import javax.swing.JTabbedPane;

import UI.Events.ImageTabPathChangedEvent;
import UI.Events.ImageTabNameChangedEvent;
import UI.Events.Listeners.ImageTabPageListener;
import UI.ImageDisplay.ImageTabPage;

public class TabPage extends JTabbedPane implements ImageTabPageListener
{
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
		
		int totalTabs = super.getComponentCount();
		
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
	}

	@Override
	public void imageTabNameChanged(ImageTabNameChangedEvent e) 
	{
		if(e.getCurrentTabIndex() < 0 || e.getCurrentTabIndex() >= super.getTabCount())
			return;
		
		super.setTitleAt(e.getCurrentTabIndex(), e.getNewname());	
	}

}
