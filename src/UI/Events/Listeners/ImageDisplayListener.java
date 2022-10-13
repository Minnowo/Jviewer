package UI.Events.Listeners;

import UI.Events.ImageZoomChangedEvent;

public interface ImageDisplayListener 
{
	/**
	 * Called when image zoom size changes
	 */
	public void ImageZoomChanged(ImageZoomChangedEvent e);
}
