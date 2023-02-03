package nyaa.alice.jviewer.drawing.imaging;

import java.awt.image.BufferedImage;
import java.io.File;

import nyaa.alice.jviewer.drawing.imaging.exceptions.ImageUnsupportedException;

public abstract class ImageBase
{
    protected byte imageFormat;
    protected int width;
    protected int height;
    protected boolean error = false;
    protected int isprocessing = 0;
    protected boolean delayFlush = false;
    protected boolean invokeGC = true;

    public ImageBase(byte imageFormat)
    {
        this.imageFormat = imageFormat;
    }

    public byte GetImageFormat()
    {
        return this.imageFormat;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public boolean isProcessing()
    {
        return this.isprocessing > 0;
    }

    protected void checkDelayedFlush()
    {
        if (this.delayFlush && this.isprocessing <= 0)
            this.flush();
    }

    public void changeGCInvoke(boolean callGConFlush)
    {
        this.invokeGC = callGConFlush;
    }

    public boolean getInvokeGC()
    {
        return this.invokeGC;
    }

    public abstract BufferedImage getBuffered();

    public abstract boolean err();

    public abstract boolean load(File path);

    public abstract boolean load(String path);

    public abstract boolean save(String path) throws ImageUnsupportedException;

    public abstract boolean save(File path) throws ImageUnsupportedException;

    public abstract void flush();

    public abstract void rotate(Byte r);

    public abstract void rotateByDegrees(double degree);

    public abstract void convertGreyscale();

    public abstract void convertInverse();
}
