package Graphics.Imaging;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.im4java.core.IM4JavaException;

import Configuration.ImageMagick;
import Graphics.ImageUtil;
import Graphics.Rotation;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Exceptions.ImageUnsupportedException;
import Graphics.Imaging.Exceptions.RequiresMagickException;
import Util.StringUtil;

public class IMAGE extends ImageBase 
{
	private BufferedImage image;
	private BufferedImage image2;
	
	public IMAGE()
	{
		super(ImageFormat.BMP);
	}
	
	public IMAGE(String path) 
	{
		super(ImageFormat.BMP);
		this.load(path);
	}
	
	public IMAGE(String path, byte imageFormat) 
	{
		super(ImageFormat.BMP);
		this.loadWithoutDetect(new File(path), imageFormat);
	}
	
	public IMAGE(byte imageFormat, BufferedImage b) 
	{
		super(imageFormat);
		
		this.image = b;
		super.width = b.getWidth();
		super.height = b.getHeight();
	}
	
	
	
	public static IMAGE fromBuffered(BufferedImage i)
	{
		// TODO: fix this constructor 
		return new IMAGE(ImageFormat.BMP, i);
	}

	@Override
	public BufferedImage getBuffered() {
		// TODO Auto-generated method stub
		return this.image;
	}

	/**
	 * this method should be used to load the image if you detect the image format previously to avoid extra reading
	 * since the standard load function reads the image byte headers to detect the image, this trusts the given format
	 * @param path the image paath
	 * @param imageFormat the image format to set this image, and pass onto image magick
	 * @return
	 */
	public synchronized boolean loadWithoutDetect(File path, byte imageFormat)
	{
		if(!path.exists())
			return false;
	
		try 
		{
			super.isprocessing++;
		
			final String absp = path.toString();
			
			logger.log(Level.INFO, String.format("loading %s", absp));
			
			super.imageFormat = imageFormat;
	
			if(ImageMagick.useImageMagick) 
			{
				try 
				{
					this.image =  ImageUtil.loadImageWithMagick(absp, super.imageFormat);
					super.width = this.image.getWidth();
					super.height = this.image.getHeight();
					super.error = false;
					return true;
				} 
				catch (IOException | InterruptedException | IM4JavaException e) 
				{
					super.error = true;
					logger.log(Level.WARNING, String.format("Failed to read image %s using ImageMagick:\nMessage: %s", path, e.getMessage()), e);
				}
			}
		
			if(!ImageFormat.hasNativeSupport(imageFormat))
			{
				logger.log(Level.WARNING, String.format("unsupported image format %s, no native support", ImageFormat.getMimeType(imageFormat)));
				super.error = true;
				super.width = 0;
				super.height = 0;
				return false;
			}
			
			try
			{
				BufferedImage i = ImageIO.read(path);
				
				if(i == null)
				{
					throw new IOException("unable to read image, ImageIO.read returned null");
				}
				
			    this.image = ImageUtil.createOptimalImageFrom(i);
			    super.width = this.image.getWidth();
				super.height = this.image.getHeight();
				super.error = false;
				return true;
			} 
			catch (IOException e) 
			{
				logger.log(Level.WARNING, String.format("Failed to read image %s using ImageIO.read:\nMessage: %s", path, e.getMessage()), e);
				super.error = true;
				super.width = 0;
				super.height = 0;
			}
		}
		finally 
		{
			super.isprocessing--;
			checkDelayedFlush();
		}
		
		return false;
	}
	
	@Override
	public boolean load(File path) 
	{
		return loadWithoutDetect(path, ImageDetector.getImageFormat(path.getAbsolutePath()));
	}

	@Override
	public boolean load(String path) 
	{
		File path_ = new File(path);
		
		return this.load(path_);
	}

	@Override
	public boolean save(String path) throws ImageUnsupportedException 
	{
    	return this.save(new File(path));
	}

	@Override
	public synchronized boolean save(File path) throws ImageUnsupportedException 
	{
		try 
		{
			super.isprocessing++;
			
			if(ImageMagick.useImageMagick)
	    	{
	    		try 
	    		{
					ImageUtil.saveImageWithMagick(this.image, path);
					return true;
				} 
	    		catch (IOException | InterruptedException | IM4JavaException e) 
	    		{
	    			logger.log(Level.WARNING, String.format("Failed to save image %s using ImageMagick:\nMessage: %s", path, e.getMessage()), e);
				}
	    	}
	    	
			final byte imgFormat = ImageFormat.getFromFileExtension(StringUtil.getFileExtension(path, false));
	    	
	    	if(imgFormat == ImageFormat.UNKNOWN)
	    		throw new ImageUnsupportedException(String.format("The image format from '%s' was not recognized", path));
	    	
	    	if(imgFormat == ImageFormat.WEBP)
	    		throw new RequiresMagickException("Saving with the WebP image format requires the use of ImageMagick");
	    	
	    	try 
	    	{
	    		ImageIO.write(this.image, ImageFormat.getFileExtension(imgFormat), path);
	    		return true;
	    	}
	    	catch (IOException e) 
	    	{
	            logger.log(Level.WARNING, String.format("Failed to save image %s with ImageIO.write:\nMessage: %s", path, e.getMessage()), e);
	    	}
		}
		finally 
		{
			super.isprocessing--;
			checkDelayedFlush();
		}
    	return false;
	}


	@Override
	public void flush() 
	{
		if(this.image == null)
			return;
		
		super.delayFlush = true;
		
		if(isProcessing())
			return;
		
		synchronized (this) 
		{	
			super.delayFlush = false;
			
			if(this.image != null)
				this.image.flush();
			
			this.image = null;
			
			if(super.getInvokeGC())
				System.gc();
		}
	}

	@Override
	public synchronized void rotate(Byte r) 
	{
		if(this.image == null)
			return;
		
		try 
		{
			super.isprocessing++;
			
			switch (r) 
			{
				case Rotation.MIRROR_HORIZONTAL:
					ImageUtil.mirrorHorizontal2(image);
					return;
					
				case Rotation.MIRROR_VERTICAL:
					ImageUtil.mirrorVertical2(image);
					return;
			}
			
			this.image = ImageUtil.rotate(this.image, r);
			super.width = this.image.getWidth();
			super.height = this.image.getHeight();
		}
		finally 
		{
			super.isprocessing--;
			super.checkDelayedFlush();
		}
	}

	@Override
	public synchronized void rotateByDegrees(double degree) 
	{
		if(this.image == null)
			return;
		
		
		try 
		{
			super.isprocessing++;
			
			this.image = ImageUtil.rotateImageByDegrees(this.image, degree);
			super.width = this.image.getWidth();
			super.height = this.image.getHeight();
		}
		finally
		{
			super.isprocessing--;
			super.checkDelayedFlush();
		}
	}

	@Override
	public synchronized void convertGreyscale() 
	{
		if(this.image == null)
			return;
		
		try 
		{
			super.isprocessing++;
			
			ImageUtil.convertGreyscale2(this.image);	
		}
		finally 
		{
			super.isprocessing--;
			super.checkDelayedFlush();
		}
	}

	@Override
	public synchronized void convertInverse() 
	{
		if(this.image == null)
			return;
		
		try 
		{
			super.isprocessing++;
		
			ImageUtil.convertInverse3(this.image);
		}
		finally 
		{
			super.isprocessing--;
			super.checkDelayedFlush();
		}
	}

	@Override
	public boolean err() {
		// TODO Auto-generated method stub
		return super.error;
	}

}
