package Util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class DragAndDropHelper 
{

    public static class FileDragHandler extends TransferHandler 
    {
        private ArrayList<File> files = new ArrayList<>();
        
        public ArrayList<File> getFiles()
        {
        	return this.files;
        }

        @Override
        public int getSourceActions ( final JComponent c )
        {
            return COPY;
        }

        @Override
        protected Transferable createTransferable ( final JComponent c )
        {
            return new FileTransferable (files);
        }
    }
    
    

	public static class FileTransferable implements Transferable 
	{
	    final private List<File> files;
	    
	    final private DataFlavor[] flavors;
	
	    public FileTransferable(File f)
	    {
	    	this.files = Collections.unmodifiableList(Arrays.asList(f));
	    	this.flavors = new DataFlavor[] { DataFlavor.javaFileListFlavor };
	    }
	    
	    public FileTransferable(Collection<File> files) 
	    {
	        this.files = Collections.unmodifiableList(new ArrayList<File>(files));
	        this.flavors = new DataFlavor[] { DataFlavor.javaFileListFlavor };
	    }
	
	    public List<File> getFiles() 
	    { 
	    	return this.files; 
	    }
	
	    @Override 
	    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
	    {
	        if (isDataFlavorSupported(flavor))
	        {
	        	return this.files;
	        }
	            
	        return null;
	    }
	
	    @Override 
	    public DataFlavor[] getTransferDataFlavors() 
	    {
	        return this.flavors;
	    }
	
	    @Override 
	    public boolean isDataFlavorSupported(DataFlavor flavor) 
	    {
	        return DataFlavor.javaFileListFlavor.equals(flavor);
	    }
	}
}
