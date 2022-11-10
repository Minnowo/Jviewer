package Graphics.Imaging.Gif;

import java.awt.image.BufferedImage;

import Graphics.Imaging.ImageBase;
import Graphics.Imaging.Enums.ImageFormat;

public abstract class GifBase extends ImageBase 
{

	public GifBase()
	{
		super(ImageFormat.GIF);
	}
	
	public GifBase(String path)
	{
		super(ImageFormat.GIF);
		this.load(path);
	}

	public abstract int getFrameIndex();
	
	public abstract void setFrameIndex(int index);
	
	public abstract void updateCurrentFrame();
	
	public abstract int getFrameCount();
	
	public abstract int getCurrentFrameDelay();
	
	public abstract BufferedImage getFrame(int index);
	
	public abstract int getFrameDelay(int index);
	
}
