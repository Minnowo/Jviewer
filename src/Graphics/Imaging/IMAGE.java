package Graphics.Imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.im4java.core.IM4JavaException;

import Configuration.ImageMagick;
import Graphics.ImageUtil;
import Graphics.Rotation;
import Graphics.Imaging.Enums.ImageFormat;

public class IMAGE extends ImageBase 
{
	private BufferedImage image;
	
	public IMAGE()
	{
		super(ImageFormat.BMP, "image/bmp");
	}
	
	public IMAGE(String path) 
	{
		super(ImageFormat.BMP, "image/bmp");
		this.load(path);
	}
	
	public IMAGE(byte imageFormat, String mime) 
	{
		super(imageFormat, mime);
	}
	
	private IMAGE(byte imageFormat, String mime, BufferedImage b) 
	{
		super(imageFormat, mime);
		
		this.image = b;
		super.width = b.getWidth();
		super.height = b.getHeight();
	}
	
	
	
	public static IMAGE fromBuffered(BufferedImage i)
	{
		// TODO: fix this constructor 
		return new IMAGE(ImageFormat.BMP, "image/bmp", i);
	}

	@Override
	public BufferedImage getBuffered() {
		// TODO Auto-generated method stub
		return this.image;
	}

	@Override
	public void load(File path) 
	{
		if(!path.exists())
			return;
		
		logger.log(Level.INFO, "loading %s".formatted(path.getAbsolutePath()));
		
		// TODO: stop using file extension and read image headers 
		final String ext = Util.StringUtil.getFileExtension(path);
		final byte imageFormat = ImageFormat.getFromFileExtension(ext);
		
		super.imageFormat = imageFormat;

		if(ImageMagick.useImageMagick) 
		{
			try 
			{
				this.image = ImageUtil.loadImageWithMagick(path.getAbsolutePath());
				super.width = this.image.getWidth();
				super.height = this.image.getHeight();
				return;
			} 
			catch (IOException | InterruptedException | IM4JavaException e) 
			{
				logger.log(Level.WARNING, "Failed to read image %s using ImageMagick:\nMessage: %s".formatted(path, e.getMessage()), e);
			}
		}
	
		try 
		{
		    this.image = ImageIO.read(path);
		    super.width = this.image.getWidth();
			super.height = this.image.getHeight();
		} 
		catch (IOException e) 
		{
			logger.log(Level.WARNING, "Failed to read image %s using ImageIO.read:\nMessage: %s".formatted(path, e.getMessage()), e);
			super.width = 0;
			super.height = 0;
		}
	}

	@Override
	public void load(String path) 
	{
		File path_ = new File(path);
		
		this.load(path_);
	}

	@Override
	public void save(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(File path) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void flush() 
	{
		if(this.image == null)
			return;
		
		this.image.flush();
	}

	@Override
	public void rotate(Byte r) 
	{
		if(this.image == null)
			return;
		
		this.image = ImageUtil.rotate(this.image, r);
	}

	@Override
	public void rotateByDegrees(double degree) 
	{
		if(this.image == null)
			return;
		
		this.image = ImageUtil.rotateImageByDegrees(this.image, degree);
	}

	@Override
	public void convertGreyscale() 
	{
		if(this.image == null)
			return;
		
		ImageUtil.convertGreyscale(this.image);
	}

	@Override
	public void convertInverse() 
	{
		if(this.image == null)
			return;
		
		ImageUtil.convertInverse(this.image);
	}

}
