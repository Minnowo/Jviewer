package Graphics.Imaging.Gif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import Graphics.ImageUtil;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;

public class GIF2 extends GifBase 
{
	private int currentFrameIndex;
	private long lastRenderTime = System.nanoTime();
	private ImageReader decoder;
	private ImageInputStream stream;
	private int frameCount = 0;
	
	// this is hopefully temporary 
	private final int FRAME_DELAY = 100;
	
	public GIF2()
	{
		this.currentFrameIndex = -1;
	}
	
	public GIF2(String path)
	{
		this.currentFrameIndex = -1;
		
		this.load(path);
	}
	
	public int getFrameIndex()
	{
		return this.currentFrameIndex;
	}
	
	public void setFrameIndex(int index)
	{
		if(index < 0 || index >= this.getFrameCount())
			return;
		
		this.currentFrameIndex = index;
	}
	
	public void setLastRenderTime()
	{
		this.lastRenderTime = System.nanoTime();
	}
	
	public void updateCurrentFrame()
	{
		if(this.decoder == null)
			return;
		
		long elapsed = (long) ((System.nanoTime() - this.lastRenderTime) / 1e6); 
		
		// hard coding this for now, this whole class is a test
		if(elapsed > FRAME_DELAY )//this.decoder.getDelay(currentFrameIndex))
		{
			this.currentFrameIndex = (currentFrameIndex + 1) % this.frameCount;
			this.setLastRenderTime();
		}
	}
	
	public int getFrameCount()
	{
		return frameCount;
	}
	
	public int getCurrentFrameDelay()
	{
		return this.getFrameDelay(currentFrameIndex);
	}
	
	int u= 0;

	@Override
	public BufferedImage getBuffered() 
	{
		if(this.decoder == null)
			return null;

		try 
		{
			return this.decoder.read(currentFrameIndex);
		} 
		catch (IOException e) 
		{
			logger.log(Level.WARNING, "error reading gif frame %d / %d".formatted(this.currentFrameIndex, this.frameCount), e);
			return null;
		}
	}

	@Override
	public boolean load(File path) 
	{
		try 
		{
		    this.decoder = ImageIO.getImageReadersByFormatName("gif").next();
		    
		    this.stream = ImageIO.createImageInputStream(path);
		    
		    this.decoder.setInput(stream);
		    
		    this.frameCount = this.decoder.getNumImages(true);
		    
		    this.currentFrameIndex = 0;

		    BufferedImage b = this.getBuffered();
		    
		    if(b != null)
		    {
		    	this.width = b.getWidth();
		    	this.height = b.getHeight();
		    }
		    
		    return frameCount != -1;
		} 
		catch (IOException ex) 
		{
		    // An I/O problem has occurred
		}
		return false;
	}


//	@Override
//	public int getWidth()
//	{
//		BufferedImage b = this.getBuffered();
//		
//		if(b == null)
//			return 0;
//		
//		return b.getWidth();
//	}
//	
//	@Override
//	public int getHeight()
//	{
//		BufferedImage b = this.getBuffered();
//		
//		if(b == null)
//			return 0;
//		
//		return b.getWidth();
//	}
	
	@Override
	public boolean load(String path) 
	{
		return this.load(new File(path));
	}

	@Override
	public boolean save(String path) throws ImageUnsupportedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean save(File path) throws ImageUnsupportedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(Byte r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateByDegrees(double degree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void convertGreyscale() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void convertInverse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BufferedImage getFrame(int index) 
	{
		if(this.decoder == null || index < 0 || index >= this.frameCount)
			return null;
		
		try 
		{
			return this.decoder.read(index);
		} 
		catch (IOException e) 
		{
			return null;
		}
	}

	@Override
	public int getFrameDelay(int index) {
		// TODO Auto-generated method stub
		return 100;
	}
}
