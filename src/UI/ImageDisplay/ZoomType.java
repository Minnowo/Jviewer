package UI.ImageDisplay;



/*
 * C like enum that represents the zoom type 
 */
public class ZoomType 
{
	/*
	 * Zooms into the bottom right of the image.
	 */
    public static final byte BOTTOM_RIGHT_IMAGE = 1;
    
    /*
     * Zooms into the center of the image.
     */
    public static final byte CENTER_IMAGE = 2;
    
    /*
     * CenterImage but at the mouse location.
     */
    public static final byte CENTER_IMAGE_AT_MOUSE = 3;
    
    /*
     * Zooms into the pixel the mouse is on.
     */
    public static final byte INTO_MOUSE = 4; 

}
