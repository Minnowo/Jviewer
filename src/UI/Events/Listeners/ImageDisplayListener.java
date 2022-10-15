package UI.Events.Listeners;

import UI.Events.ImageSizeChangedEvent;
import UI.Events.ImageZoomChangedEvent;

public interface ImageDisplayListener 
{
	/**
	 * Called when image zoom size changes
	 */
	public void ImageZoomChanged(ImageZoomChangedEvent e);
	
	/**
	 * Called when image width and height changes
	 */
	public void ImageSizeChanged(ImageSizeChangedEvent e);
}
