package nyaa.alice.jviewer.ui.events;

public class ImageTabNameChangedEvent implements ImageTabEvent
{
    private String newname;
    private int index;

    public ImageTabNameChangedEvent(int index, String newname)
    {
        this.newname = newname;
        this.index = index;
    }

    public String getNewname()
    {
        return newname;
    }

    public int getIndex()
    {
        return index;
    }

    public int getCurrentTabIndex()
    {
        // TODO Auto-generated method stub
        return index;
    }
}
