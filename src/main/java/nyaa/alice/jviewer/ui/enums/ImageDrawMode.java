package nyaa.alice.jviewer.ui.enums;

public interface ImageDrawMode
{
    /**
     * Allows for free dragging and zooming of the image.
     */
    public static final byte RESIZEABLE = 0;

    /**
     * Always scale the image to fit the maximum possible size.
     */
    public static final byte FIT_IMAGE = 1;

    /**
     * Scale the image when it doesn't fit on the control, otherwise show the
     * default image size
     */
    public static final byte DOWNSCALE_IMAGE = 2;

    /**
     * Only show the image as default size.
     */
    public static final byte ACTUAL_SIZE = 3;

    /**
     * stretch the image to fit the whole area of the control
     */
    public static final byte STRETCH = 4;
}
