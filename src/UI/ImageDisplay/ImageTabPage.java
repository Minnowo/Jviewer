package UI.ImageDisplay;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTabbedPane;

import UI.Events.ImageTabPathChangedEvent;
import UI.Events.ImageTabNameChangedEvent;
import UI.Events.Listeners.ImageTabPageListener;

public class ImageTabPage extends ImageDisplay 
{
	public static final String EMPTY_TAB_PAGE_NAME = "---";
	
	
	private Set<ImageTabPageListener> listeners = new HashSet<ImageTabPageListener>();
	
	private File currentFilePath;
	
	final private JTabbedPane parent ;
	
	private int currentTabIndex = -1;
	
	
	public ImageTabPage(JTabbedPane parent)
	{
		this.parent = parent;
	}
	
	
	
	public int getCurrentTabIndex()
	{
		return this.currentTabIndex;
	}
	
	public void setTabIndex(int index)
	{
		this.currentTabIndex = index;
	}
	
	public File getCurrentPath()
	{
		return this.currentFilePath;
	}
	
	@Override
	public void tryLoadImage(String path, boolean flushLastImage)
	{
		File f = new File(path);
		
		if(!f.exists())
			return;
		
		String current = f.getAbsolutePath();
		
		currentFilePath = f;
		
		super.tryLoadImage(f.getAbsolutePath(), flushLastImage);
		
		onImagePathChanged(current, f.getAbsolutePath());
		onTabNameChangedEvent(f.getName());
	}
	
    
	@Override 
	public void setImage(BufferedImage image, boolean flushLastImage)
	{
		super.setImage(image, flushLastImage);
		
		if(this.getImage() == null)
		{
			onTabNameChangedEvent(EMPTY_TAB_PAGE_NAME);
		}
	}
	
    
    /**
     * register the given object to the event listener to receive events from this class 
     * @param lis a class which implements {@link ImageTabPageListener}
     */
    public void addImageTabPageListener(ImageTabPageListener lis) 
    {
    	this.listeners.add(lis);
	}
    
    
    
    protected void onImagePathChanged(String before, String after) 
    {
    	ImageTabPathChangedEvent event = new ImageTabPathChangedEvent(this.currentTabIndex, before, after);
    	
    	for(ImageTabPageListener ls : this.listeners)
    	{
    		ls.imagePathChanged(event);
    	}
	}
    
    
    protected void onTabNameChangedEvent(String newname)
    {
    	ImageTabNameChangedEvent event = new ImageTabNameChangedEvent(this.currentTabIndex, newname);
    	
    	for(ImageTabPageListener ls : this.listeners)
    	{
    		ls.imageTabNameChanged(event);
    	}
    }
    
    
	
	
	
}
