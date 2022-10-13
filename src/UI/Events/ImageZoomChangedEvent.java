package UI.Events;

/**
 * An event fired when the image display image zoom changes
 */
public class ImageZoomChangedEvent implements Event
{
    private int ZoomLevelPercent;
    private double ZoomLevel;
    
    public ImageZoomChangedEvent(int zoomPercent, double zoomLevel)
    {
    	this.ZoomLevel = zoomLevel;
    	this.ZoomLevelPercent = zoomPercent;
    }
    
    public int getZoomLevelPercent()
    {
    	return this.ZoomLevelPercent;
    }
    
    public double getZoomLevel()
    {
    	return this.ZoomLevel;
    }
} 
