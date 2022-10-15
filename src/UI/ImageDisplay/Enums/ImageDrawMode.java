package UI.ImageDisplay.Enums;

public class ImageDrawMode 
{
	/**
	 * Always scale the image to fit the maximum possible size.
	 */
    public static final byte FIT_IMAGE = 0;


    /**
     * Only show the image as default size.
     */
    public static final byte ACTUAL_SIZE = 1;


    /**
     * Scale the image when it doesn't fit on the control, otherwise show the default image size
     */
    public static final byte DOWNSCALE_IMAGE = 2;


    /**
     * Allows for free dragging and zooming of the image.
     */
    public static final byte RESIZEABLE = 3;
}
