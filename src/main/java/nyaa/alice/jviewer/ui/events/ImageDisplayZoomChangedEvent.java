package nyaa.alice.jviewer.ui.events;

/**
 * An event fired when the image display image zoom changes
 */
public class ImageDisplayZoomChangedEvent implements Event
{
    private int ZoomLevelPercent;
    private double ZoomLevel;

    public ImageDisplayZoomChangedEvent(int zoomPercent, double zoomLevel)
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
