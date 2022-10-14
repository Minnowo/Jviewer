package Graphics.Imaging;

import java.io.File;

import Graphics.Imaging.Enums.ImageFormat;

public class BMP extends IMAGE 
{

	public BMP() {
		super(ImageFormat.BMP, "image/bmp");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void load(File path) 
	{
		super.image = super.loadImage(path);
	}

	@Override
	public void load(String path) 
	{
		super.image = super.loadImage(path);
	}

	@Override
	public void save(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(File path) {
		// TODO Auto-generated method stub
		
	}

}
