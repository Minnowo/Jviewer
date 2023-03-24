package nyaa.alice.jviewer.data;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.tinylog.Logger;

import nyaa.alice.jviewer.drawing.imaging.ImageUtil;

public class ClipboardUtil
{
    public static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static BufferedImage getClipboardImage()
    {
        Transferable content = clipboard.getContents(null);

        if (content == null)
        {
            Logger.warn("Could not read clipboard, content is null");
            return null;
        }

        if (!content.isDataFlavorSupported(DataFlavor.imageFlavor))
        {
            Logger.warn("Could not read image from clipboard: imageFlavor not supported");
            return null;
        }

        try
        {
            BufferedImage img = (BufferedImage) content.getTransferData(DataFlavor.imageFlavor);
            return ImageUtil.createOptimalImageFrom2(img);
        }
        catch (UnsupportedFlavorException | IOException e)
        {
            Logger.warn(e, "Could not read image from clipboard");
            return null;
        }
    }

    public static void copyImageToClipboard(BufferedImage img)
    {
        if (img == null)
            return;
        // this prints a big stack trace if the image has transparency
        // idk how to remove it or catch the error or what but it doesn't seem to do
        // anything?
        clipboard.setContents(new TransferableImage(img), null);
    }

    public static class TransferableImage implements Transferable
    {
        private Image image;

        public TransferableImage(Image image)
        {
            this.image = image;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
        {
            if (flavor.equals(DataFlavor.imageFlavor))
            {
                return this.image;
            }

            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return flavor.equals(DataFlavor.imageFlavor);
        }
    }
}
