package UI;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

public class DialogHelper 
{
	public static File askChooseFile()
	{
		FileDialog fd = new FileDialog((Frame)null, "Select File to Open");
	    fd.setMode(FileDialog.LOAD);
	    fd.setVisible(true);
	    
	    if(fd.getFile() == null)
	    {
	    	return new File("");
	    }

	    return  new File(fd.getDirectory() + fd.getFile());
	}
}
