package UI.ImageDisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.Border;

import Graphics.Imaging.IMAGE;
import UI.Events.ImageZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.Enums.AntiAliasing;
import UI.ImageDisplay.Enums.DrawMode;
import UI.ImageDisplay.Enums.ImageDrawMode;
import UI.ImageDisplay.Enums.InterpolationMode;
import UI.ImageDisplay.Enums.RenderQuality;
import UI.ImageDisplay.Enums.ZoomType;

public class ImageDisplay extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	/**
	 * listeners to evens created by this control
	 */
	private Set<ImageDisplayListener> listeners = new HashSet<ImageDisplayListener>();

	/**
	 * the default max zoom
	 */
	public static final double MAX_ZOOM_DEFAULT = 200d;
    
	/**
	 * the default min zoom
	 */
    public static final double MIN_ZOOM_DEFAULT = 0.01d;
    
    /**
     * the default max zoom percent
     */
    public static final int MAX_ZOOM_PERCENT_DEFAULT = (int)(MAX_ZOOM_DEFAULT * 100d);

    /**
     * the default min zoom percent
     */
    public static final int MIN_ZOOM_PERCENT_DEFAULT = (int)(MIN_ZOOM_DEFAULT * 100d);
    
    /**
     * the default array of zoom levels
     */
    public static final int[] ZOOM_PERCENT_DEFAULT = { 4, 7, 10, 15, 20, 25, 30, 50, 70, 100, 
    		150, 200, 300, 400, 500, 600, 700, 800, 1200, 1600, 2000, 2400, 2800, 
    		3200, 3600, 4000, 4400, 4800, 5200, 5600, 6000, 6400, 6800, 7200, 
    		7600, 8000, 8400, 8800, 9200, 9600, 10000, 10400, 10800, 11200, 11600, 12000,
    		12400, 12800, 13200, 13600, 14000, 14400, 14800, 15200, 15600, 16000, 16400,
    		16800, 17200, 17600, 18000, 18400, 18800, 19200, 19600, 20000 };
    
    
    /**
     * the minimum possible zoom
     */
	public final double MAX_ZOOM;
    
	/**
	 * the max possible zoom
	 */
    public final double MIN_ZOOM;
    
    /**
     * the minimum possible zoom percent
     */
    public final int MIN_ZOOM_PERCENT;
    
    /**
     * the maximum possible zoom percent
     */
    public final int MAX_ZOOM_PERCENT;
    
    /**
     * constant array that holds the zoom percent values for the control
     */
    public final int[] ZOOM_PERCENT;

    /**
     * the index of the current zoom level, 
     */
    private int zoomIndex ;
    
    /**
     * default color for cell 1
     */
    public static Color DefaultCellColor1 = new Color(32, 32, 32);
    
    /**
     * default color for cell 2
     */
    public static Color DefaultCellColor2 = new Color(64, 64, 64);

    /**
     * the MouseEvent.BUTTONx button that determines dragging  
     */
    public int mouseDragButton = MouseEvent.BUTTON1;
    
    /**
     * how the control should handle mouse zooming
     */
    public int zoomType = ZoomType.INTO_MOUSE;
    
    /**
     * the interpolation mode to use.
     * determines the RenderHints.VALUE_INTERPOLATION_xyz value 
     */
    private int interpolationMode = InterpolationMode.NEAREST_NEIGHBOR;
    
    /**
     * RenderingHints.VALUE_INTERPOLATION_xyz Object reference, this is changed with interpolationMode
     */
    private Object interpolationModeObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    
    /**
     * the antialiasing mode to use.
     * determines the RenderHints.VALUE_ANTIALIAS_xyz value 
     */
    private int antiAliasing = AntiAliasing.DISABLED;
    
    /**
     * RenderingHints.VALUE_ANTIALIAS_xyz Object reference, this is changed with antiAliasing
     */
    private Object antiAliasingObject = RenderingHints.VALUE_ANTIALIAS_OFF;
    
    /**
     * the render quality to use.
     * determines the RenderHints.VALUE_RENDER_xyz value 
     */
    private int renderQuality = RenderQuality.QUALITY;
    
    /**
     * RenderingHints.VALUE_RENDER_xyz Object reference, this is changed with renderQuality
     */
    private Object renderQualityObject = RenderingHints.VALUE_RENDER_QUALITY;
    
    /**
     * the cell color 1 for the checkerboard pattern
     */
    private Color cellColor1 = DefaultCellColor1;
    
    /**
     * the cell color 2 for the checkerboard pattern
     */
    private Color cellColor2 = DefaultCellColor2;
    
    /**
     * the cell size for the checkerboard pattern BEFORE the cellScale is applied 
     */
    private int cellSize ;
    
    /**
     * the cell scale for the checkerboard pattern
     */
    private float cellScale;

    /*
     * allow the user to drag the image 
     */
    private boolean allowDrag = true;
    
    /**
     * can the image be animated 
     */
    private boolean isAnimating = false;
    
    /**
     * is the animated image paused 
     */
    private boolean animationPaused = false;
    
    /**
     * should the image be centered when first drawn, or when the drawMode requires it 
     */
    private boolean centerImage = true;

    /**
     * should the background and image be drawn
     */
	private boolean display = true;
	
	/**
	 * the current image
	 */
    private BufferedImage image;

    /**
     * the current image width
     */
    private int imageWidth;
    
    /**
     * the current image height
     */
    private int imageHeight;
    
    /**
     * how the image should be drawn and interacted with by the user 
     */
    private byte drawMode = DrawMode.RESIZEABLE;
    
    /**
     * last click point when dragging the image
     */
    private Point lastClickPoint = new Point(0, 0);
    
    /**
     * image draw position x
     */
    private int drX = 0;
    
    /**
     *  image draw position y 
     */
    private int drY = 0;
    
    /**
     * is the user dragging on the control 
     */
    private boolean isDragButtonDown = false;

    /**
     * the image zoom level as a double 
     */
    private double _zoom = 1;
    
    /**
     * brush used to render the checkerboard pattern
     */
    private TexturePaint tileBrush;
    

    public ImageDisplay() 
    {
    	super.addMouseListener(this);
    	super.addMouseMotionListener(this);
    	super.addMouseWheelListener(this);
    	
    	this.cellSize = 32;
    	this.cellScale = 1;
    	ZOOM_PERCENT = ZOOM_PERCENT_DEFAULT;
    	MAX_ZOOM = MAX_ZOOM_DEFAULT;
    	MIN_ZOOM = MIN_ZOOM_DEFAULT;
    	MAX_ZOOM_PERCENT = MAX_ZOOM_PERCENT_DEFAULT;
    	MIN_ZOOM_PERCENT = MIN_ZOOM_PERCENT_DEFAULT;
    	zoomIndex = 9; // 9th index is 100% 
    	
    	initTileBrush();
    }
    
    /**
     * lets you specify the zoom levels
     * @param zoomPercent the int[] of zoomLevelPercent to use, THIS SHOULD BE SORTED
     * @param zoomPercentStartIndex the index in zoomPercent to start the zoom at 
     */
    public ImageDisplay(int[] zoomPercent, int zoomPercentStartIndex) 
    {
    	super.addMouseListener(this);
    	super.addMouseMotionListener(this);
    	super.addMouseWheelListener(this);
    	
    	this.cellSize = 32;
    	this.cellScale = 1;
    	ZOOM_PERCENT = zoomPercent;
    	MAX_ZOOM_PERCENT = zoomPercent[zoomPercent.length - 1];
    	MIN_ZOOM_PERCENT = zoomPercent[0];
    	MAX_ZOOM = MAX_ZOOM_PERCENT / 100d;
    	MIN_ZOOM = MIN_ZOOM_PERCENT / 100d;
    	zoomIndex = zoomPercentStartIndex;
    	initTileBrush();
    }
    
    private class Rect
    {
    	public int x;
    	public int y;
    	public int width;
    	public int height;
    	
    	public Rect(int x, int y, int width, int height)
    	{
    		this.x = x;
    		this.y = y;
    		this.width = width;
    		this.height = height;
    	}
    	
    	public String toString() 
    	{
    		return x + " " + y + " " + width + " " + height;
    	}
    }
    

    public void tryLoadImage(String path, boolean flushLastImage)
    {
    	BufferedImage i = IMAGE.loadImage(path);
    	
    	if(i == null)
    	{
    		return;
    	}
    	
    	this.setImage(i, flushLastImage);
    }
  
    public void setImage(BufferedImage image, boolean flushLastImage)
    {
    	if(this.image != null && flushLastImage)
    	{
    		this.image.flush();
    		this.image = null;
    		System.gc();
    	}
    	
    	if(image != null)
    	{
    		this.image = image;
        	this.imageWidth = image.getWidth();
        	this.imageHeight = image.getHeight();
        	this.CenterCurrentImage();
    	}
    	else 
    	{
    		this.imageWidth = -1;
    		this.imageHeight = -1;
    	}
    	
    	this.repaint();
    }
    
    protected Rect GetControlSize(boolean includePadding)
    {
        int left = 0;
        int top  = 0;
        int width = this.getWidth();
        int height = this.getHeight();

//        if (includePadding && this.getBorder() != null)
//        {
//        	Border b = this.getBorder();
//        	
//        	
//            left += this.Padding.Left;
//            top += this.Padding.Top;
//            width -= this.Padding.Horizontal;
//            height -= this.Padding.Vertical;
//        }

        return new Rect(left, top, width, height);
    }
    
    public void CenterCurrentImage()
    {
        if (this.image == null)
            return;

        int iWidth = this.imageWidth;
        int iHeight = this.imageHeight;
        final int cWidth = this.getWidth();
        final int cheight = this.getHeight();
        
        
        switch (drawMode)
        {
            case ImageDrawMode.FIT_IMAGE:
            case ImageDrawMode.RESIZEABLE:

                this.ZoomToFit();
                iWidth  = (int)(iWidth  * this._zoom);
                iHeight = (int)(iHeight * this._zoom);

                this.drX = (cWidth >> 1) - (iWidth >> 1);
                this.drY = (cheight >> 1) - (iHeight >> 1);
                break;

            case ImageDrawMode.ACTUAL_SIZE:

                if (iWidth < cWidth)
                {
                    this.drX = (cWidth >> 1) - (iWidth >> 1);
                }

                if (iHeight < cheight)
                {
                    this.drY = (cheight >> 1) - (iHeight >> 1);
                }
                break;

            case ImageDrawMode.DOWNSCALE_IMAGE:
                if (iWidth > cWidth || iHeight > cheight)
                {
                	this.ZoomToFit();
                	
                    iWidth  = (int)(iWidth  * this._zoom);
                    iHeight = (int)(iHeight * this._zoom);
                }

                this.drX = (cWidth  >> 1) - (iWidth  >> 1);
                this.drY = (cheight >> 1) - (iHeight >> 1);
                break;
        }

        this.repaint();
    }
    
    
    public void ZoomToFit()
    {
        if (this.imageWidth <= 0 || this.imageHeight <= 0)
            return;
        
        Rect innerRectangle = this.GetControlSize(true);
        double zoom;
        double aspectRatio;

        if (this.imageWidth > this.imageHeight)
        {
            aspectRatio = (double)innerRectangle.width / this.imageWidth;
            zoom = aspectRatio * 100.0;

            if (innerRectangle.height < this.imageHeight * zoom / 100.0)
            {
                aspectRatio = (double)innerRectangle.height / this.imageHeight;
                zoom = aspectRatio * 100.0;
            }
        }
        else
        {
            aspectRatio = (double)innerRectangle.height / this.imageHeight;
            zoom = aspectRatio * 100.0;

            if (innerRectangle.width < this.imageWidth * zoom / 100.0)
            {
                aspectRatio = (double)innerRectangle.width / this.imageWidth;
                zoom = aspectRatio * 100.0;
            }
        }

        this.setZoomPercent((int)zoom);
    }
    
    
    public BufferedImage getImage()
    {
    	return this.image;
    }
    
    
    public int getImageWidth()
    {
    	return this.imageWidth;
    }
    
    public int getImageHeight()
    {
    	return this.imageHeight;
    }
    
    
    public Color getCellColor1()
    {
    	return this.cellColor1;
    }
    
    public void setCellColor1(Color c)
    {
    	this.cellColor1 = c;
    }
    
    
    
    public Color getCellColor2()
    {
    	return this.cellColor2;
    }
    
    public void setCellColor2(Color c)
    {
    	this.cellColor2 = c;
    }
    
    
    public int getZoomPercent()
    {
    	return (int)(this._zoom * 100);
    }
    
    public void setZoomPercent(int value)
    {
    	if(value > MAX_ZOOM_PERCENT || 
    	   value < MIN_ZOOM_PERCENT)
    		return;
    	
    	this._zoom = value / 100d;
    	this.repaint();
    	this.onImageZoomChanged();
    }
    
    
    
    public double getZoom() 
    {
		return this._zoom;
	}
    
    public void setZoom(double value) 
    {
    	if (value > MAX_ZOOM ||
    		value < MIN_ZOOM)
            return;

        this._zoom = value;
        this.repaint();
        this.onImageZoomChanged();
	}
    
    
    
    public void setDisplay(boolean drawImage) 
    {
		this.display = drawImage;
		
		this.repaint();
	}
    
    public boolean getDisplay()
    {
    	return this.display;
    }

    
    public void setAntiAliasing(int antialiasing)
    {
    	this.antiAliasing = antialiasing;
    	this.antiAliasingObject = AntiAliasing.getMode(antialiasing); 
    	this.repaint();
    }
    
    public int getAntiAliasing()
    {
    	return this.antiAliasing;
    }
    
	public void setRenderQuality(int renderQuality) 
	{
		this.renderQuality = renderQuality;
		this.renderQualityObject = RenderQuality.getMode(renderQuality);
		this.repaint();
	}
	
	public int getRenderQuality()
	{
		return this.renderQuality;
	}

    
    public void setInterpolationMode(int interpolationMode)
    {
    	this.interpolationMode = interpolationMode;
    	this.interpolationModeObject = InterpolationMode.getMode(interpolationMode);
    	this.repaint();
    }
    
    public int getInterpolationMode()
    {
    	return this.interpolationMode;
    }
    
    
    private void ZoomIntoMouse(double beforeZoom, double nowZoom, Point mousePosition) 
    {
		double scaleRatio = (this.imageWidth * beforeZoom) / (this.imageWidth * nowZoom);
		
		double mouseOffsetX = mousePosition.getX() - this.drX;
		double mouseOffsetY = mousePosition.getY() - this.drY;
		
		this.drX = (int)(drX - ((mouseOffsetX / scaleRatio) - mouseOffsetX));
		this.drY = (int)(drY - ((mouseOffsetY / scaleRatio) - mouseOffsetY));
	}
    
    
    protected void ZoomCenterMouse(double afterZoom, Point mousePosition)
    {
        double afterZoomWidth = imageWidth * afterZoom;
        double afterZoomHeight = imageHeight * afterZoom;

        this.drX = (int)(mousePosition.getX() - (afterZoomWidth / 2d));
        this.drY = (int)(mousePosition.getY() - (afterZoomHeight / 2d));
    }

    
    protected void ZoomBottomRightImage(double beforeZoom, double afterZoom)
    {
        double beforeZoomWidth = imageWidth * beforeZoom;
        double beforeZoomHeight = imageHeight * beforeZoom;

        double afterZoomWidth = imageWidth * afterZoom;
        double afterZoomHeight = imageHeight * afterZoom;

        this.drX -= (int)(afterZoomWidth - beforeZoomWidth);
        this.drY -= (int)(afterZoomHeight - beforeZoomHeight);
    }


    protected void ZoomCenterImage(double beforeZoom, double afterZoom)
    {
        double beforeZoomWidth = imageWidth * beforeZoom;
        double beforeZoomHeight = imageHeight * beforeZoom;

        double afterZoomWidth = imageWidth * afterZoom;
        double afterZoomHeight = imageHeight * afterZoom;

        this.drX -= (int)(afterZoomWidth - beforeZoomWidth) >> 1;
        this.drY -= (int)(afterZoomHeight - beforeZoomHeight) >> 1;
    }


    protected void MouseZoom(boolean isZoomIn)
    {
        if (isZoomIn)
        {
        	this.zoomIndex = FindNearest(this.getZoomPercent());
        	
        	if(this.zoomIndex + 1 == ZOOM_PERCENT.length)
        		return; 
        	
        	this.setZoomPercent(ZOOM_PERCENT[++this.zoomIndex]);
        }
        else
        {
        	this.zoomIndex = FindNearest(this.getZoomPercent());
        	
        	if(this.zoomIndex - 1 == -1)
        		return; 
        	
        	this.setZoomPercent(ZOOM_PERCENT[--this.zoomIndex]);
        }
    }
    
    
    protected Rect GetImageViewPort()
    {
        if (this.image == null)
            return null;

        int xPos = drX;
        int yPos = drY;

        int width  = this.imageWidth;
        int height = this.imageHeight;
        
        final int cWidth = this.getWidth();
        final int cHeight = this.getHeight();


        switch (this.drawMode)
        {
            case ImageDrawMode.RESIZEABLE:

                width  = (int)(width  * this._zoom);
                height = (int)(height * this._zoom);

                // prevent the image from getting lost off the left and top side of the control
                if (drX < -width)
                {
                    xPos = -width;
                    drX = xPos;
                }

                if (drY < -height)
                {
                    yPos = -height;
                    drY = yPos;
                }

                return new Rect(xPos, yPos, xPos + width, yPos + height);

            case ImageDrawMode.ACTUAL_SIZE:

                if (this.centerImage)
                {
                    if (width < cWidth)
                    {
                        //     (this.ClientSize.Width / 2) - (width / 2);
                        xPos = (cWidth >> 1) - (width >> 1);
                    }
                    else if (xPos < -width)
                    {
                        xPos = -width;
                        
                        // this is important
                        drX = xPos;
                    }

                    if (height < cHeight)
                    {
                        yPos = (cHeight >> 1) - (height >> 1);
                    }
                    else if(yPos < -height)
                    {
                        yPos = -height;
                        
                        // this is important
                        drY = yPos;     
                    }
                }
                else
                {
                    if (width < cWidth)
                    {
                        xPos = 0;
                        drX = 0;
                    }

                    if (height < cHeight)
                    {
                        yPos = 0;
                        drY = 0;
                    }
                }

                return new Rect(xPos, yPos, xPos + width, yPos + height);

            case ImageDrawMode.FIT_IMAGE:
                ZoomToFit();

                width  = (int)(width * this._zoom);
                height = (int)(height * this._zoom);

                if (this.centerImage)
                {
                    xPos = (cWidth  >> 1) - (width  >> 1);
                    yPos = (cHeight >> 1) - (height >> 1);
                }
                else
                {
                    xPos = 0;
                    yPos = 0;
                }

                return new Rect(xPos, yPos, xPos + width, yPos + height);

            case ImageDrawMode.DOWNSCALE_IMAGE:

                if (width > cWidth || height > cHeight)
                {
                    ZoomToFit();

                    width  = (int)(width  * this._zoom);
                    height = (int)(height * this._zoom);
                }

                if (this.centerImage)
                {
                    xPos = (cWidth  >> 1) - (width  >> 1);
                    yPos = (cHeight >> 1) - (height >> 1);
                }
                else
                {
                    xPos = 0;
                    yPos = 0;
                }

                return new Rect(xPos, yPos, xPos + width, yPos + height);
        }
        
        return null;
    }

    
    protected void drawBackground(Graphics g, Graphics2D g2)
    {
        g2.setPaint(this.tileBrush);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    
    
    protected void drawImage(Graphics g, Graphics2D g2)
    {
    	Rect r = this.GetImageViewPort();
    
    	if(r == null)
    		return;
    	
        g2.setRenderingHint(RenderingHints.KEY_RENDERING    , this.renderQualityObject);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING , this.antiAliasingObject);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.interpolationModeObject);
 
    	g.drawImage(image, 
    			r.x, r.y, r.width, r.height, 
    			0, 0, this.imageWidth, this.imageHeight, null);

    }
    
    
    protected void initTileBrush()
    {        
        int width  = (int)(cellSize * 2 * this.cellScale);
        int height = (int)(cellSize * 2 * this.cellScale);
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = result.getGraphics();
        
        g.setColor(this.cellColor1);
        g.fillRect(cellSize, 0, cellSize, cellSize);
        g.fillRect(0, cellSize, cellSize, cellSize);
        
        g.setColor(this.cellColor2);
        g.fillRect(0, 0, cellSize, cellSize);
        g.fillRect(cellSize, cellSize, cellSize, cellSize);
        
        g.dispose();
    
        Rectangle2D bounds = new Rectangle2D.Float(0, 0, result.getWidth(), result.getHeight());
        
        this.tileBrush = new TexturePaint(result, bounds);
    }
    
    
    private int FindNearest(int zoomLevel)
    {
        int nearestValue = 0;
        int nearestDifference = Math.abs(ZOOM_PERCENT[0] - zoomLevel);
        
        for (int i = 1; i < ZOOM_PERCENT.length; i++)
        {
            int value = ZOOM_PERCENT[i];
            int difference = Math.abs(value - zoomLevel);
            
            if (difference < nearestDifference)
            {
                nearestValue = i;
                nearestDifference = difference;
            }
        }
        return nearestValue;
    }
    
    public void addImageDisplayListener(ImageDisplayListener lis) 
    {
    	this.listeners.add(lis);
	}
    
    protected void onImageZoomChanged() 
    {
    	ImageZoomChangedEvent event = new ImageZoomChangedEvent(this.getZoomPercent(), this._zoom);
    	
    	for(ImageDisplayListener ls : this.listeners)
    	{
    		ls.ImageZoomChanged(event);
    	}
	}
    
    @Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g);
    	
    	if(!this.display) 
    	{
    		return;
    	}
    	
    	this.drawBackground(g, (Graphics2D) g);
    	
    	if(this.image != null)
    	{	
    		this.drawImage(g, (Graphics2D) g);
    	}
    }
    

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (this.drawMode != ImageDrawMode.ACTUAL_SIZE && !this.allowDrag)
            return;
		
		if(e.getButton() == this.mouseDragButton)
		{
			this.isDragButtonDown = true;
			this.lastClickPoint.setLocation(e.getPoint());
		}
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(e.getButton() == this.mouseDragButton)
		{
			this.isDragButtonDown = false;
		}
	}
	
	
	@Override
    public void mouseDragged(MouseEvent e) 
	{
		if(!this.isDragButtonDown || this.drawMode != ImageDrawMode.RESIZEABLE && this.drawMode != ImageDrawMode.ACTUAL_SIZE)
			return;
		
		this.drX -= this.lastClickPoint.x - e.getX();
		this.drY -= this.lastClickPoint.y - e.getY();
		
		if(this.drX > this.getWidth())
			this.drX = this.getWidth();
		
		if(this.drY > this.getHeight())
			this.drY = this.getHeight();
		
		this.lastClickPoint.setLocation(e.getPoint());
		
		this.repaint();
    }
	
	
	@Override 
	public void mouseWheelMoved(MouseWheelEvent e)
	{
        if (this.image == null)
            return;

//        if (this.DrawMode != ImageDrawMode.Resizeable)
//            return;
        
        // TODO: figure out how to get the mouse wheel delta thing
        // // C# code that does ^
        // // int spins = Math.Abs(e.Delta / SystemInformation.MouseWheelScrollDelta);
        
        double beforeZoom = this._zoom;
        
        if(e.getWheelRotation() < 0)
        {
        	this.MouseZoom(true);
        }
        else 
        {
        	this.MouseZoom(false);
        }
        
        switch (this.zoomType) 
        {
	        case ZoomType.BOTTOM_RIGHT_IMAGE: 
	        	this.ZoomBottomRightImage(beforeZoom, this._zoom);
	        	break;
	        
	        case ZoomType.CENTER_IMAGE: 
	        	this.ZoomCenterImage(beforeZoom, this._zoom);
	        	break;
	        
	        case ZoomType.CENTER_IMAGE_AT_MOUSE: 
	        	this.ZoomCenterMouse(this._zoom, e.getPoint());
	        	break;
	        
	        case ZoomType.INTO_MOUSE: 
	        	this.ZoomIntoMouse(beforeZoom, this._zoom, e.getPoint());
	        	break;
		}
        
        this.repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 	{	}
	
	@Override
	public void mouseEntered(MouseEvent e) 	{	}
	
	@Override
	public void mouseExited(MouseEvent e) 	{	}
	
    @Override
    public void mouseMoved(MouseEvent e)    {   }

}