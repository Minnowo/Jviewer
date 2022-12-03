package UI.ImageDisplay;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JTabbedPane;

import Graphics.Imaging.ImageBase;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import UI.Events.ImageTabNameChangedEvent;
import UI.Events.ImageTabPathChangedEvent;
import UI.Events.Listeners.ImageTabPageListener;
import Util.AVL_FileTree;

public class ImageTabPage extends ImageDisplay 
{
	public static final String EMPTY_TAB_PAGE_NAME = "---";
	
	private Set<ImageTabPageListener> listeners = new HashSet<ImageTabPageListener>();
	
	private AVL_FileTree directory;
	
	private File currentFilePath;
	
	final private JTabbedPane parent ;
	
	private int currentTabIndex = -1;
	
	
	public ImageTabPage(JTabbedPane parent)
	{
		this.parent = parent;
		this.directory = new AVL_FileTree();
	}
	
	public void SetTabName(String name)
	{
		onTabNameChangedEvent(name);
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
	
	public File getCurrentPathOrTempPath()
	{
		if(this.currentFilePath != null && this.currentFilePath.exists())
			return this.currentFilePath;
		
		if(super.getImage() == null)
			return null;
		
		try 
		{
			Path tempDirWithPrefix = Files.createTempDirectory("jview");
			File filePath = File.createTempFile("tmp", ".jpg", tempDirWithPrefix.toFile());
			
			logger.log(Level.INFO, "creating temp file: %s".formatted(filePath));
			
			if(super.getImage().save(filePath))
			{
				this.currentFilePath = filePath;
				return filePath;
			}
			
			return null;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ImageUnsupportedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void nextImage()
	{
		File f = this.directory.inOrderSuccessor(currentFilePath);

		if(f == null || !f.exists())
			return;
		
		tryLoadImage(f.getAbsolutePath(), true);
	}
	
	@Override
	public void tryLoadImage(String path, boolean flushLastImage)
	{
		File f = new File(path);
		
		if(!f.exists())
			return;
		
		String current = f.getAbsolutePath();
		
		currentFilePath = f;
		
		if(!path.equals(directory.getDirectory()))
		{
			directory.loadDirectory(path);
			System.out.println("loading directory");
		}
			
		
		super.tryLoadImage(f.getAbsolutePath(), flushLastImage);
		
		onImagePathChanged(current, f.getAbsolutePath());
		onTabNameChangedEvent(f.getName());
	}
	
    
	@Override 
	public void setImage(ImageBase image, boolean flushLastImage)
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
