package Graphics;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
import org.im4java.process.ProcessStarter;

public class ImageUtil 
{
	public static BufferedImage loadImage(String path)
	{
		File path_ = new File(path);
		
		if(!path_.exists())
			return null;
		
		
		
		try 
		{
			return loadImageMagick(path);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		try 
		{
		    return ImageIO.read(path_);
		} 
		catch (IOException e) 
		{
			return null;
		}
	}
	
	
	public static void setupMagick()
	{
		String myPath="D:\\Programming\\java\\Jviewer\\Lib\\magick";
		ProcessStarter.setGlobalSearchPath(myPath);
	}
	
	public static void resizeImages(String... pImageNames) throws IOException, InterruptedException, IM4JavaException {
		  // create command
		  ConvertCmd cmd = new ConvertCmd();

		  // create the operation, add images and operators/options
		  IMOperation op = new IMOperation();
		  op.addImage();
		  op.resize(800,600);
		  op.addImage();

		  for (String srcImage:pImageNames) 
		  {
		    int lastDot = srcImage.lastIndexOf('.');
		    
		    String dstImage = srcImage.substring(1,lastDot-1)+"_small.jpg";
		    
		    cmd.run(op,srcImage,dstImage);
		  }
		}
	public static void test()
	{
		// create command
		ConvertCmd cmd = new ConvertCmd();

		// create the operation, add images and operators/options
		IMOperation op = new IMOperation();
		op.addImage("D:\\02.jpg");
		op.resize(800,600);
		op.addImage("D:\\myimage_small.jpg");

		// execute the operation
		try {
			cmd.run(op);
		} catch (IOException | InterruptedException | IM4JavaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static BufferedImage loadImageMagick(String path ) throws IOException, InterruptedException, IM4JavaException
	{
		
		// bmp loads the fastest, but draws to the screen slowly
		// png loads the slowest, but keeps transparency and draws fast, also most memory
		// jpg loads faster than png, but removes transparency,
		final int chosenFormatIndex = 1;
		final String[] stdoutDecodeFormat = { "bmp", "png", "jpg" };
		final String stdoutDFormat = stdoutDecodeFormat[chosenFormatIndex];
		
		IMOperation op = new IMOperation();

		// input image path
		op.addImage(path);
		
		// set image output type into stdout, (bmp seems fastest but slow to render)
		op.addImage(stdoutDFormat + ":-"); 


		// set up command
		ConvertCmd convert = new ConvertCmd();
		Stream2BufferedImage s2b = new Stream2BufferedImage();
		convert.setOutputConsumer(s2b);

		// run command and extract BufferedImage from OutputConsumer
		convert.run(op);

		BufferedImage b = s2b.getImage();
		
		// https://stackoverflow.com/a/659533
		// optimize the image for drawing to the screen, only need this for bmp, but could do it always to ensure fast rendering
		if(stdoutDFormat.compareTo("bmp") == 0)
		{
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    GraphicsDevice device = env.getDefaultScreenDevice();
		    GraphicsConfiguration config = device.getDefaultConfiguration();
		    BufferedImage buffy = config.createCompatibleImage(b.getWidth(), b.getHeight(), Transparency.TRANSLUCENT);
		    
		    Graphics g = buffy.getGraphics();
		    g.drawImage(b, 0, 0, b.getWidth(), b.getHeight(), null);
		    g.dispose();
		    
		    return buffy;
		}
		

		return b;
	}
}
