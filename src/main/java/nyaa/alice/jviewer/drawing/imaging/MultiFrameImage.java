package nyaa.alice.jviewer.drawing.imaging;

import java.awt.image.BufferedImage;

import nyaa.alice.jviewer.drawing.imaging.enums.ImageFormat;

public abstract class MultiFrameImage extends ImageBase
{
    public MultiFrameImage()
    {
        super(ImageFormat.GIF);
    }

    public MultiFrameImage(String path)
    {
        super(ImageFormat.GIF);
        this.load(path);
    }

    public abstract int getFrameIndex();

    public abstract void setFrameIndex(int index);

    public abstract void updateCurrentFrame();

    public abstract int getFrameCount();

    public abstract int getCurrentFrameDelay();

    public abstract BufferedImage getFrame(int index);

    public abstract int getFrameDelay(int index);
}
