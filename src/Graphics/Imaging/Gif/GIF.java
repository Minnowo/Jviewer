package Graphics.Imaging.Gif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import Graphics.ImageUtil;
import Graphics.Imaging.ImageBase;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Gif.GifDecoder.GifFrame;

/**
 * a gif wrapper around the GifDecoder extending the ImageBase class<br>
 * can be used to draw the image with animation via updateCurrentFame()<br<
 * 
 * NOTE: this class uses a TON of memory, this is because it decodes the gif into an array of frames
 * rather than using a single Image object, this is more flexible but uses a lot more memory 
 * @author minno
 *
 */
public class GIF extends GifBase 
{
	private int currentFrameIndex;
	private GifDecoder decoder;
	private long lastRenderTime = System.nanoTime();
	
	public GIF()
	{
		this.currentFrameIndex = -1;
	}
	
	public GIF(String path)
	{
		this.currentFrameIndex = -1;
		
		this.load(path);
	}
	
	@Override
	public int getFrameIndex()
	{
		return this.currentFrameIndex;
	}
	
	@Override
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
	
	@Override
	public void updateCurrentFrame()
	{
		if(this.decoder == null)
			return;
		
		long elapsed = (long) ((System.nanoTime() - this.lastRenderTime) / 1e6); 
		
		if(elapsed > this.decoder.getDelay(currentFrameIndex))
		{
			this.currentFrameIndex = (currentFrameIndex + 1) % this.decoder.getFrameCount();
			this.setLastRenderTime();
		}
	}
	
	
	public int getFrameCount()
	{
		if(this.decoder == null)
			return 0;
		
		return this.decoder.getFrameCount();
	}
	
	@Override
	public int getCurrentFrameDelay()
	{
		return this.getFrameDelay(currentFrameIndex);
	}
	
	@Override
	public BufferedImage getBuffered()
	{
		return this.getCurrentFrame();
	}
	
	public BufferedImage getCurrentFrame()
	{
		return this.getFrame(currentFrameIndex);
	}
	
	public BufferedImage getFrame(int index)
	{
		if(this.decoder == null)
			return null;
		
		return this.decoder.getFrame(index);
	}
	
	public int getFrameDelay(int index)
	{
		if(this.decoder == null)
			return 0;
		
		return this.decoder.getDelay(index);
	}
	
	
	
	@Override
	public int getWidth()
	{
		if(this.decoder == null)
			return 0;

		return this.decoder.getFrame(currentFrameIndex).getWidth();
	}
	
	@Override
	public int getHeight()
	{
		if(this.decoder == null)
			return 0;

		return this.decoder.getFrame(currentFrameIndex).getHeight();
	}
	

	@Override
	public boolean load(File path) 
	{
		return this.load(path.getAbsolutePath());
	}

	@Override
	public boolean load(String path) 
	{
		if(this.decoder == null)
			this.decoder = new GifDecoder();
		
		this.decoder.read(path);
		
		if(this.decoder.err())
		{
			this.decoder = null;
			this.currentFrameIndex = -1;
			super.error = true;
			return false;
		}
		
		this.currentFrameIndex = 0;
		super.error = false;
		return true;
	}

	@Override
	public boolean save(String path) 
	{
		GifEncoder e = new GifEncoder();
		
		e.start(path);
		e.setRepeat(0);
		
		for(int i = 0; i < this.decoder.getFrameCount(); i++)
		{
			GifFrame gf = this.decoder.getFrameSource(i);
			
			e.addFrame(gf.image);
			
			e.setDelay(gf.delay);	
			
			// TODO: fix transparency on encoding, idk why it's so broken
			
//			if(this.decoder.hasTransparency())
//			{
//				e.setTransparent(new Color(gf.transColor));
//				
//				BufferedImage bf = new BufferedImage(gf.image.getWidth(), gf.image.getHeight(), BufferedImage.TYPE_INT_RGB);
//				Graphics g = bf.getGraphics();
//				g.setColor(new Color(gf.transColor));
//				g.fillRect(0, 0, gf.image.getWidth(), gf.image.getHeight());
//				g.drawImage(gf.image, 0,0, null);
//				
//				e.addFrame(bf);
//			}
//			else
//			{
//				e.addFrame(gf.image);
//				e.setTransparent(null);
//				System.out.println("has not trans");
//			}
			
			
		}

		e.finish();
	
		return true;
	}

	@Override
	public boolean save(File path) 
	{
		return this.save(path.getAbsolutePath());	
	}

	@Override
	public void flush() 
	{
		if(this.decoder == null)
			return;
		
		for(int i = 0; i < this.decoder.getFrameCount(); i++)
		{
			this.decoder.getFrame(i).flush();
		}
		
		this.decoder = null;
		this.currentFrameIndex = -1;
	}

	@Override
	public void rotate(Byte r) 
	{
		if(this.decoder == null)
			return;
		
		for(int i = 0; i < this.decoder.getFrameCount(); i++)
		{
			GifFrame buff = this.decoder.getFrameSource(i);
			
			buff.image = ImageUtil.rotate(buff.image, r);
		}
	}

	@Override
	public void rotateByDegrees(double degree) 
	{
		if(this.decoder == null)
			return;
		
		for(int i = 0; i < this.decoder.getFrameCount(); i++)
		{
			GifFrame buff = this.decoder.getFrameSource(i);
			
			buff.image = ImageUtil.rotateImageByDegrees(buff.image, degree);
		}
	}

	@Override
	public void convertGreyscale() 
	{
		if(this.decoder == null)
			return;
		
		for(int i = 0; i < this.decoder.getFrameCount(); i++)
		{
			GifFrame buff = this.decoder.getFrameSource(i);
			
			buff.transColor = ImageUtil.getGreyPixel(buff.transColor);
			
			ImageUtil.convertGreyscale(buff.image);
		}
	}

	@Override
	public void convertInverse() 
	{
		if(this.decoder == null)
			return;
		
		for(int i = 0; i < this.decoder.getFrameCount(); i++)
		{
			GifFrame buff = this.decoder.getFrameSource(i);
			
			buff.transColor = ImageUtil.getInversePixel(buff.transColor);
			
			ImageUtil.convertInverse(buff.image);
		}
	}

	@Override
	public boolean err() {
		// TODO Auto-generated method stub
		return super.error;
	}
}
