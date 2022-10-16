package UI.Events;

public class ImageTabPathChangedEvent implements ImageTabEvent  
{
	private String before;
	private String after;
	private int index;
	
	public ImageTabPathChangedEvent(int tabPageIndex, String old, String neww)
	{
		this.index = tabPageIndex;
		this.before = old ;
		this.after = neww;
	}

	public String getAfter() {
		return after;
	}

	public String getBefore() {
		return before;
	}


	public int getCurrentTabIndex() 
	{
		return index;
	}
}
