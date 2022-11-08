package UI.Events.Listeners;

import UI.Events.ImageDisplayImageSizeChangedEvent;
import UI.Events.ImageDisplayZoomChangedEvent;

public interface ImageDisplayListener 
{
	/**
	 * Called when image zoom size changes
	 */
	public void ImageZoomChanged(ImageDisplayZoomChangedEvent e);
	
	/**
	 * Called when image width and height changes
	 */
	public void ImageSizeChanged(ImageDisplayImageSizeChangedEvent e);
	
	
	/**
	 * Called when image changes
	 */
	public void ImageChanged();
}
