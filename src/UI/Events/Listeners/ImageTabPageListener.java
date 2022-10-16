package UI.Events.Listeners;

import UI.Events.ImageTabPathChangedEvent;
import UI.Events.ImageTabNameChangedEvent;

public interface ImageTabPageListener 
{
	public void imagePathChanged(ImageTabPathChangedEvent e);
	
	public void imageTabNameChanged(ImageTabNameChangedEvent e);
}
