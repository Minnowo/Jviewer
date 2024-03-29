package nyaa.alice.jviewer.ui.events;

public class ImageDisplayImageSizeChangedEvent implements Event
{
    private int newHeight;
    private int newWidth;
    private int oldHeight;
    private int oldWidth;

    public ImageDisplayImageSizeChangedEvent(int newWidth, int newHeight, int oldWidth, int oldHeight)
    {
        this.newHeight = newHeight;
        this.newWidth = newWidth;
        this.oldHeight = oldHeight;
        this.oldWidth = oldWidth;
    }

    public int getNewHeight()
    {
        return newHeight;
    }

    public int getNewWidth()
    {
        return newWidth;
    }

    public int getOldHeight()
    {
        return oldHeight;
    }

    public int getOldWidth()
    {
        return oldWidth;
    }

}
