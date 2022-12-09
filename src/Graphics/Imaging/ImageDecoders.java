package Graphics.Imaging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import Graphics.Imaging.Enums.ImageFormat;
import Util.Logging.LoggerWrapper;

public class ImageDecoders 
{
	public static boolean zipSearch(File zipFile, String find, File output)
    {
        final int BUFFER_SIZE = 1024*1024*1024;
        
        try(ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile)))
        {
            ZipEntry e = zipIn.getNextEntry();

            for(; e != null; e = zipIn.getNextEntry())
            {
                if(e.getName().equalsIgnoreCase(find))
                    break;
            }

            if(e == null)
                return false;

            try(FileOutputStream fos = new FileOutputStream(output);
                BufferedOutputStream bos = new BufferedOutputStream(fos))
            {
                final byte[] bytesIn = new byte[BUFFER_SIZE];
                int read = 0;
                while ((read = zipIn.read(bytesIn)) != -1) 
                {
                    bos.write(bytesIn, 0, read);
                }
                
                return true;
            }
        }
        catch(IOException e)
        {
        	LoggerWrapper.warning("exception reading zip file", e);
        }
        return false;
    }
	
	public static IMAGE loadKritaFile(String path)
	{
		File filePath;
		
		try 
		{
			Path tempDirWithPrefix = Files.createTempDirectory("jview");
			filePath = File.createTempFile("tmp", ".png", tempDirWithPrefix.toFile());
			LoggerWrapper.info(String.format("creating temp file: %s", filePath));
		} 
		catch (IOException e) 
		{
			LoggerWrapper.warning(String.format("could not create temp file to load: %s", path), e);
			return null;
		}
		
		// krita files (.kra) are like zip files, and if you open them there is usually 2 pngs
		// mergedimage.png is the full merged visible image
		// preview.png is the preview image shown in file explorer
		// find and extract them to a temp file 
		if(!zipSearch(new File(path), "mergedimage.png", filePath) &&
		   !zipSearch(new File(path), "preview.png", filePath))
		{
			return null;
		}
		
		IMAGE preview = new IMAGE();
		
		if(preview.loadWithoutDetect(filePath, ImageFormat.UNOFFICIAL_SUPPORT.KRA))
		{
			return preview;
		}
		
		return null;
	}
}
