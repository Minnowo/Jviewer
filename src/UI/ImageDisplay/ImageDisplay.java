package UI.ImageDisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.Timer;

import Configuration.GUISettings;
import Graphics.ImageUtil;
import Graphics.Rotation;
import Graphics.Imaging.IMAGE;
import Graphics.Imaging.ImageBase;
import Graphics.Imaging.Enums.ImageFormat;
import Graphics.Imaging.Gif.GifBase;
import UI.Events.ImageDisplayImageSizeChangedEvent;
import UI.Events.ImageDisplayZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.Enums.AntiAliasing;
import UI.ImageDisplay.Enums.ImageDrawMode;
import UI.ImageDisplay.Enums.InterpolationMode;
import UI.ImageDisplay.Enums.RenderQuality;
import UI.ImageDisplay.Enums.ZoomType;
import Util.ColorUtil;
import Util.Logging.LoggerWrapper;

public class ImageDisplay extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener, ComponentListener
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
     * the MouseEvent.BUTTONx button that determines selection
     */
    public int mouseSelectionButton = MouseEvent.BUTTON3;
    
    /**
     * how the control should handle mouse zooming
     */
    public byte zoomType = ZoomType.INTO_MOUSE;
    
    /**
     * the interpolation mode to use.
     * determines the RenderHints.VALUE_INTERPOLATION_xyz value 
     */
    private byte interpolationMode = InterpolationMode.NEAREST_NEIGHBOR;
    
    /**
     * RenderingHints.VALUE_INTERPOLATION_xyz Object reference, this is changed with interpolationMode
     */
    private Object interpolationModeObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    
    /**
     * the antialiasing mode to use.
     * determines the RenderHints.VALUE_ANTIALIAS_xyz value 
     */
    private byte antiAliasing = AntiAliasing.DISABLED;
    
    /**
     * RenderingHints.VALUE_ANTIALIAS_xyz Object reference, this is changed with antiAliasing
     */
    private Object antiAliasingObject = RenderingHints.VALUE_ANTIALIAS_OFF;
    
    /**
     * the render quality to use.
     * determines the RenderHints.VALUE_RENDER_xyz value 
     */
    private byte renderQuality = RenderQuality.QUALITY;
    
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
     * the image border color
     */
    private Color imageBorderColor = Color.red;
    
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
    
    /*
     * allow the user to create a selection area 
     */
    private boolean allowSelection = true;
    
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
	 * draws a rectangle around the image bounds
	 */
	private boolean drawImageBorder = false;
	
	/**
	 * timer that controls repaint for gifs 
	 */
	private Timer timer = new Timer(10, this);
	
	/**
	 * the current image
	 */
    private ImageBase image;

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
    private byte drawMode = ImageDrawMode.RESIZEABLE;
    
    /**
     * last click point when dragging the image
     */
    private Point lastClickPoint = new Point(0, 0);
    
    /**
     * handles the selection box
     */
    private Selection selectionArea = new Selection();
    
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
     * is the user creating a selection on the control 
     */
    private boolean isSelectionButtonDown = false;
    
    /**
     * the image zoom level as a double 
     */
    private double _zoom = 1;
    
    /**
     * brush used to render the checkerboard pattern
     */
    private TexturePaint tileBrush;
    
    /**
     * used to check if the component has loaded with an image at least once
     * this is used to properly center the image on initial loads where the control might be hidden
     */
    private boolean hasLoadedOnce = false;
    
    /**
     * if the selection box should render in a dashed line
     */
    private boolean drawDashedSelection = true;
    
    /**
     * the selection box color 1
     */
    private Color selectionColor = Color.white;
    
    /**
     * the selection box color 2, only applies if drawDashedSelection = true
     */
    private Color selectionColor2 = Color.black;
    

    /**
     * default constructor which sets default ZOOM_PERCENT values 
     */
    public ImageDisplay() 
    {
    	super.addMouseListener(this);
    	super.addMouseMotionListener(this);
    	super.addMouseWheelListener(this);
    	super.addComponentListener(this);
    	
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
    	super.addComponentListener(this);
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
    
    
    
    /**
     * simple rectangle class used to return the image viewport 
     *
     */
    private static class Rect
    {
    	public int x, y, width, height;
    	
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
    
    
    
    /**
     * C like enum used to identify drag handles on the selection
     * @author minno
     *
     */
    public static interface DragHandle 
	{
		public static final byte INVALID      = -1;
		public static final byte TOP_LEFT     = 0;
		public static final byte TOP_RIGHT    = 1;
		public static final byte TOP          = 3;
		public static final byte BOTTOM_LEFT  = 4;
		public static final byte BOTTOM_RIGHT = 5;
		public static final byte BOTTOM       = 6;
		public static final byte LEFT         = 7;
		public static final byte RIGHT        = 8;
		public static final byte MIDDLE       = 9;
	}
    
  
    
    /**
     * class which handles drawing, and working with the selection box
     * @author minno
     *
     */
    private class Selection 
    {
    	/**
    	 * this number determines the size of all the drag handles
    	 */
    	private int margins = 8;
    	
    	/**
    	 * the bounds of the selection
    	 */
    	public int x1, x2, y1, y2;
    	
    	/**
    	 * indicates if the selection is invalid or should be treated as such
    	 */
    	public boolean isInvalid = true;
    	
    	/**
    	 * identifies which drag handle is being used to change the selection
    	 */
    	public byte expanding = DragHandle.INVALID;
    	
    	/**
    	 * handles drawing a dash style line if needed
    	 */
    	private BasicStroke dashStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 8.0f, 8.0f }, 0);
    	
    	
    	public Selection()
    	{
    		
    	}
    	
    	
    	public Selection(int margins)
    	{
    		this.margins = margins;
    	}
    	
    	
    	public void setMargins(int margins)
    	{
    		this.margins = margins;
    	}
    	
    	
    	/**
    	 * 
    	 * @return a bool indicating if the selection is being changed via a drag handle
    	 */
    	public boolean isExpanding()
    	{
    		return this.expanding != DragHandle.INVALID;
    	}
    	
    	
    	/**
    	 * this function handles resizing via drag handles<br>
    	 * 
    	 * it returns true if the given point is in a drag handle, and sets the expanding to the given drag handle<br>
    	 * @param p the mouse position of click or drag
    	 * @return true if valid drag handle / resize, otherwise false
    	 */
    	public boolean adjustSizeOrDie(Point p)
    	{
    		final int X = (int)p.getX();
    		final int Y = (int)p.getY();
    		
    		switch (this.expanding) 
    		{
	    		default:
	        		return false;
	        		
	    		case DragHandle.MIDDLE:
	    			int difx = lastClickPoint.x - X;
	    			int dify = lastClickPoint.y - Y;
	    		
	    			this.x1 -= difx;
	    			this.x2 -= difx;
	    			this.y1 -= dify;
	    			this.y2 -= dify;
	    			
	    			lastClickPoint.setLocation(p);
	    			return true;
	        		
	    		case DragHandle.TOP_LEFT:
	    			this.x1 = X;
    				this.y1 = Y;
	    			return true;
	    			
	    		case DragHandle.TOP_RIGHT:
	    			this.x2 = X;
    				this.y1 = Y;
	    			return true;
	    			
	    		case DragHandle.TOP:
	    			this.y1 = Y;
	    			return true;
	    			
	    		case DragHandle.BOTTOM_LEFT:
	    			this.x1 = X;
	    			this.y2 = Y;
	    			return true;
	    			
	    		case DragHandle.LEFT:
	    			this.x1 = X;
    				return true;
    				
	    		case DragHandle.BOTTOM_RIGHT:
	    			this.x2 = X;
	    			this.y2 = Y;
	    			return true;
	    			
	    		case DragHandle.RIGHT:
	    			this.x2 = X;
	    			return true;
	    			
	    		case DragHandle.BOTTOM:
	    			this.y2 = Y;
	    			return true;
	    			
	    			
	    		// we're not expanding so we will check for collision here
				case DragHandle.INVALID:
	
					// ensure that x1 < x2, y1 < y2 
					this.correctPoints();
					
		    		// top left corner
		    		if(X < this.x1 && Y < this.y1)
		    		{
		    			if(this.x1 - X <= this.margins && this.y1 - Y <= this.margins)
		    			{
		    				this.x1 = X;
		    				this.y1 = Y;
		    				this.expanding = DragHandle.TOP_LEFT;
		    				return true;
		    			}
		    			
		    			return false;
		    		}
		    		
		    		// top right corner
		    		if(X > this.x2 && Y < this.y1)
		    		{
		    			if(X - this.x2 <= this.margins && this.y1 - Y <= this.margins)
		    			{
		    				this.x2 = X;
		    				this.y1 = Y;
		    				this.expanding = DragHandle.TOP_RIGHT;
		    				return true;
		    			}
		    			return false;
		    		}
		    		
		    		// top
		    		if(X > this.x1 && X < this.x2 && Y < this.y1)
		    		{
		    			if(this.y1 - Y <= this.margins)
		    			{
		    				this.y1 = Y;
		    				this.expanding = DragHandle.TOP;
		    				return true;
		    			}
		    			
		    			return false;
		    		}
		    		
		    		
		    		// bottom left corner
		    		if(X < this.x1 && Y > this.y2)
		    		{
		    			if(this.x1 - X <= this.margins && Y - this.y2 <= this.margins)
		    			{
		    				this.x1 = X;
		    				this.y2 = Y;
		    				this.expanding = DragHandle.BOTTOM_LEFT;
		    				return true;
		    			}
		    			
		    			return false;
		    		}
		    		
		    		// left side
		    		if(X < this.x1 && Y < this.y2 && Y > this.y1)
		    		{
		    			if(this.x1 - X <= this.margins)
		    			{
		    				this.x1 = X;
		    				this.expanding = DragHandle.LEFT;
		    				return true;
		    			}
		    			return false;
		    		}
		    		
		    		// bottom right corner
		    		if(X > this.x2 && Y > this.y2)
		    		{
		    			if(X - this.x2 <= this.margins && Y - this.y2 <= this.margins)
		    			{
		    				this.x2 = X;
		    				this.y2 = Y;
		    				this.expanding = DragHandle.BOTTOM_RIGHT;
		    				return true;
		    			}
		    			
		    			return false;
		    		}
		    		
		    		
		    		// right side 
		    		if(X > this.x2 && Y < this.y2 && Y > this.y1)
		    		{
		    			if(X - this.x2 <= this.margins)
		    			{
		    				this.x2 = X;
		    				this.expanding = DragHandle.RIGHT;
		    				return true;
		    			}
		    			
		    			return false;
		    		}
		    		
		    		
		    		if(Y > this.y2 && X < this.x2 && X > this.x1)
		    		{
		    			if(Y - this.y2 <= this.margins)
		    			{
		    				this.y2 = Y;
		    				this.expanding = DragHandle.BOTTOM;
		    				return true;
		    			}
		    			
		    			return false;
		    		}
		    		
		    		
		    		if(Y > this.y1 && Y < this.y2 && X > this.x1 && X < this.x2)
		    		{
		    			difx = lastClickPoint.x - X;
		    			dify = lastClickPoint.y - Y;
		    		
		    			this.x1 -= difx;
		    			this.x2 -= difx;
		    			this.y1 -= dify;
		    			this.y2 -= dify;
		    			
		    			lastClickPoint.setLocation(p);
		    			
		    			this.expanding = DragHandle.MIDDLE;
		    			return true;
		    		}
		    		
		    		return false;
			}
    	}
    	
    	
    	/**
    	 * updates x2 and y2 with the given value
    	 * @param p
    	 */
    	public void updatePoint2(Point p)
    	{
    		this.x2 = (int)p.getX();
    		this.y2 = (int)p.getY();
    	}
    	
    	
    	/**
    	 * updates x1 and y1 with the given value
    	 * @param p
    	 */
    	public void updatePoint1(Point p)
    	{
    		this.x1 = (int)p.getX();
    		this.y1 = (int)p.getY();
    	}
    	
    	
    	/**
    	 * resets the expanding value to INVALID, used to cancel resizing of selection via drag handles
    	 */
    	public void resetChanging()
    	{
    		this.expanding = DragHandle.INVALID;
    	}
    	
    	
    	/**
    	 * updates the isInvalid component of the selection
    	 */
    	public boolean checkInvalid()
    	{
    		this.isInvalid =  this.x1 == this.x2 || 
    						 this.y1 == this.y2 ;
    		
    		return this.isInvalid;
    	}
    	
    	
    	/**
    	 * corrects the points and ensures that x1 < x2 and y1 < y2
    	 */
    	public void correctPoints()
    	{
    		if(this.x2 < this.x1)
    		{
    			final int X_TMP = this.x1;
    			
    			this.x1 = this.x2;
    			this.x2 = X_TMP;
    		}
    		
    		if(this.y2 < this.y1)
    		{
    			final int Y_TMP = this.y1;
    			
    			this.y1 = this.y2;
    			this.y2 = Y_TMP;
    		}
    	}
    	
    	
    	/**
    	 * returns the width of the selection
    	 * @return
    	 */
    	public int getWidth()
    	{
    		return Math.abs(this.x1 - this.x2);
    	}
    	
    	
    	/**
    	 * returns the height of the selection
    	 * @return
    	 */
    	public int getheight()
    	{
    		return Math.abs(this.y1 - this.y2);
    	}
    	
    	
    	/**
    	 * draws the selction to the given graphic
    	 * @param g the graphic to draw on
    	 * @param g2 this CAN be null IF not drawing dashed line 
    	 */
    	public void render(Graphics g, Graphics2D g2)
    	{
    		if(this.isInvalid)
    			return;
    		
    		int width = this.x2 - this.x1;
    		int height = this.y2 - this.y1;
    		int x1 = this.x1;
    		int y1 = this.y1;
    		int x2 = this.x2;
    		int y2 = this.y2;
    		
    		if(width < 0)
    		{
    			x1 += width;
    			x2 = x1 - width;
    		}
    		
    		if(height < 0)
    		{
    			y1 += height;
    			y2 = y1 - height;
    		}
    		
    		g.setColor(selectionColor);

        	// renders the inner area rectangle in a hashtag like pattern
    		g.drawLine(x1               , y1 - this.margins, x1               , y2 + this.margins);
    		g.drawLine(x1 - this.margins, y1               , x2 + this.margins, y1);
    		g.drawLine(x2               , y1 - this.margins, x2               , y2 + this.margins);
    		g.drawLine(x1 - this.margins, y2               , x2 + this.margins, y2);

    		// renders the outer area which makes a nice box with defined corners, this visualizes all the drag handles
    		g.drawLine(x1 - this.margins, y1 - this.margins, x1 - this.margins, y2 + this.margins);
    		g.drawLine(x1 - this.margins, y1 - this.margins, x2 + this.margins, y1 - this.margins);
    		g.drawLine(x2 + this.margins, y1 - this.margins, x2 + this.margins, y2 + this.margins);
    		g.drawLine(x1 - this.margins, y2 + this.margins, x2 + this.margins, y2 + this.margins);

        	
        	if(!drawDashedSelection)
        		return;
        	
        	g2.setStroke(this.dashStroke);
        	g.setColor(selectionColor2);

        	// renders the inner area rectangle in a hashtag like pattern
    		g.drawLine(x1               , y1 - this.margins, x1               , y2 + this.margins);
    		g.drawLine(x1 - this.margins, y1               , x2 + this.margins, y1);
    		g.drawLine(x2               , y1 - this.margins, x2               , y2 + this.margins);
    		g.drawLine(x1 - this.margins, y2               , x2 + this.margins, y2);

    		// renders the outer area which makes a nice box with defined corners, this visualizes all the drag handles
    		g.drawLine(x1 - this.margins, y1 - this.margins, x1 - this.margins, y2 + this.margins);
    		g.drawLine(x1 - this.margins, y1 - this.margins, x2 + this.margins, y1 - this.margins);
    		g.drawLine(x2 + this.margins, y1 - this.margins, x2 + this.margins, y2 + this.margins);
    		g.drawLine(x1 - this.margins, y2 + this.margins, x2 + this.margins, y2 + this.margins);
        	
    	}
    	
    	
    	public String toString()
    	{
    		return this.x1 + " " + this.y1 + " " + this.x2 + " " + this.y2;
    	}
    }
    

    /**
     *  because the setImage function tries to center new images 
     *  if this control hasn't been shown yet it doesn't center correctly
     *  so this method can be used by parent components to tell the control 
     *  to recenter the image for initial display
     */
    public void setLoadOnce()
	{
		if(this.image == null || this.hasLoadedOnce)
			return;
		
		this.hasLoadedOnce = true;
		this.CenterCurrentImage();
	}
    

    /**
     * tries to load the given file as an image and display it 
     * @param path the file path
     * @param flushLastImage should an existing image be flushed on successful load
     */
    public void tryLoadImage(String path, boolean flushLastImage)
    {
    	ImageBase i = ImageUtil.loadImage(path);
    	
    	if(i == null || i.err())
    	{
    		return;
    	}
    	
    	this.setImage(i, flushLastImage);
    }
  
    
    /**
     * sets the image to the given image
     * @param image the image to show
     * @param flushLastImage should the last image be flushed
     */
    public void setImage(ImageBase image, boolean flushLastImage)
    {
    	if(this.image != null && flushLastImage)
    	{
    		this.image.flush();
			this.image = null;
    	}
    	
    	if(image != null)
    	{
    		this.image = image;
    		this.isAnimating = image instanceof GifBase;
        	this.imageWidth = image.getWidth();
        	this.imageHeight = image.getHeight();
        	this.CenterCurrentImage();
        	
        	if(this.isAnimating)
        	{
        		this.animationPaused = false;
        		this.timer.start();
        	}
    	}
    	else 
    	{
    		this.isAnimating = false;
    		this.imageWidth = -1;
    		this.imageHeight = -1;
    		this.timer.stop();
    	}
    	
    	this.repaint();
    	this.onImageChanged();
    }
    
    
    /**
     * gets the size of the control
     * @param includePadding should padding be included
     */
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
    
    
    /**
     * crops the current image into the selection area
     */
    public void cropToSelection()
    {
    	BufferedImage buff = this.getSelectionImage();
    	
    	if(buff == null)
    		return;
    	
    	this.setImage(new IMAGE(this.image.GetImageFormat(), buff), true);
    }
    
    
    public BufferedImage getSelectionImage()
    {
    	if(this.image == null || this.selectionArea.checkInvalid())
    		return null;
    	
    	// TODO: add gif support for cropping 
    	if(this.image.GetImageFormat() == ImageFormat.GIF)
    		return null;
    	
    	// dividing visible components by the zoom factor gets their 'actual' size, at least with respect to the image
    	// so find the difference between the top left selection and top left of the image (visibly)
    	// then divide by zoom to get the actual position on the image
    	final int ix = (int)((this.drX - this.selectionArea.x1) / _zoom);
    	final int iy = (int)((this.drY - this.selectionArea.y1) / _zoom);
    	
    	BufferedImage newImage = new BufferedImage( 
    			Math.abs((int)(this.selectionArea.getWidth()  / _zoom)), 
    			Math.abs((int)(this.selectionArea.getheight() / _zoom)), 
    			ImageUtil.getOptimalType(Transparency.TRANSLUCENT));
    	
    	
    	Graphics g = newImage.createGraphics();
    	g.drawImage(this.image.getBuffered(), ix, iy, this);
    	g.dispose();
    	
    	return newImage;
    }
    
    
    /**
     * removes the current selection
     */
    public void clearSelection()
    {
    	this.selectionArea.isInvalid = true;
    	this.selectionArea.x1 = 0;
    	this.selectionArea.x2 = 0;
    	this.selectionArea.y1 = 0;
    	this.selectionArea.y2 = 0;
    }
    
    
    /**
     * Rotates the current image left by 90 degrees
     */
    public void rotate90Left()
    {
    	if(this.image == null)
    		return; 
    	
    	final int w = imageWidth;
    	final int h = imageHeight;

    	this.image.rotate(Rotation.CCW_90);
    	this.imageWidth = this.image.getWidth();
    	this.imageHeight = this.image.getHeight();
    	
    	this.repaint();
    	this.onImageSizeChanged(w, h, imageWidth, imageHeight);
    }
    
    
    /**
     * Rotates the current image right by 90 degrees
     */
    public void rotate90Right()
    {
    	if(this.image == null)
    		return; 

    	final int w = imageWidth;
    	final int h = imageHeight;

    	this.image.rotate(Rotation.CW_90);
    	this.imageWidth = this.image.getWidth();
    	this.imageHeight = this.image.getHeight();
    	
    	this.repaint();
    	this.onImageSizeChanged(w, h, imageWidth, imageHeight);
    }
    
    
    /**
     * flips the current image vertically
     */
    public void flipVertical()
    {
    	if(this.image == null)
    		return; 
    	
    	this.image.rotate(Rotation.MIRROR_VERTICAL);
    	this.repaint();
    }
    
    
    /**
     * flips the current image horizontally 
     */
    public void flipHorizontal()
    {
    	if(this.image == null)
    		return; 
    	
    	this.image.rotate(Rotation.MIRROR_HORIZONTAL);
    	this.repaint();
    }
    
    
    /**
     * rotates the current image by the given degrees<br>
     * this is a destructive change, it will fill the rest with transparency if needed
     * @param degree
     */
    public void rotateImage(double degree)
    {
    	if(this.image == null)
    		return; 
    	
    	final int w = imageWidth;
    	final int h = imageHeight;

    	this.image.rotateByDegrees(degree);
    	this.imageWidth = this.image.getWidth();
    	this.imageHeight = this.image.getHeight();
    	
    	this.repaint();
    	this.onImageSizeChanged(w, h, imageWidth, imageHeight);
    }
    
    
    /**
     * allows the user to drag to move the image, assuming the proper drag mode
     * @param allow
     */
    public void setImageDraggable(boolean allow)
    {
    	this.allowDrag = allow;
    }
    
    
    /**
     * gets if the user is allowed to drag the image
     * @return
     */
    public boolean getImageDraggable()
    {
    	return this.allowDrag;
    }
    
    
    /**
     * sets if the user is allowed to make and handle a selection area
     * @param allow
     */
    public void setAllowSelection(boolean allow)
    {
    	this.allowSelection = allow;
    	
    	if(!allow)
    	{
    		this.selectionArea.isInvalid = true;
    	}
    }
    
    
    /**
     * gets if the user is alloed to make selections
     * @return
     */
    public boolean getAllowSelection()
    {
    	return this.allowSelection;
    }
    
    
    /**
     * sets the selection color to the given color
     * @param c
     */
    public void setSelectionColor(Color c)
    {
    	this.selectionColor = c;
    	this.selectionColor2 = ColorUtil.invertColor(c);
    	
    	if(!this.selectionArea.isInvalid)
    		this.repaint();
    }
    
    
    /**
     * gets the selection color
     * @return
     */
    public Color getSelectionColor()
    {
    	return this.selectionColor;
    }
    
    
    /**
     * sets the selection render a dashed style line 
     * @param isDashed
     */
    public void setSelectionDashed(boolean isDashed)
    {
    	this.drawDashedSelection = isDashed;
    	
    	if(!this.selectionArea.isInvalid)
    		this.repaint();
    }
    
    
    /**
     * gets if the selection is rendered with a dashed style 
     * @return
     */
    public boolean isSelectionDashed()
    {
    	return this.drawDashedSelection;
    }
    
    
    /**
     * makes the image greyscale
     */
    public void greyscaleImage()
    {
    	if(this.image == null)
    		return; 
    	
    	this.image.convertGreyscale();
    	this.repaint();
    }
    
    
    /**
     * inverts the current image
     */
    public void invertImage()
    {
    	if(this.image == null)
    		return; 
    	
//    	ImageUtil.convertInverse(this.image);
    	this.image.convertInverse();
    	this.repaint();
    }
    
    
    /**
     * shows the image at 100% zoom factor
     */
    public void showActualImageSize()
    {
    	if(this.drawMode != ImageDrawMode.RESIZEABLE)
    		return;
    		
    	this.setZoom(1d);
    	this.CenterCurrentImageWithoutResize();
    }
    
    
    /**
     * centers the image without resizing it visually
     */
    public void CenterCurrentImageWithoutResize()
    {
    	if (this.image == null )
            return;
    	
	   final int iWidth = (int)(this.imageWidth  * this._zoom);
       final int iHeight =(int)(this.imageHeight * this._zoom);

       this.drX = (this.getWidth() >> 1) - (iWidth >> 1);
       this.drY = (this.getHeight() >> 1) - (iHeight >> 1);
       this.repaint();
    }
    
    
    /**
     * centers the image and resizes it to fit the control
     */
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
	        case ImageDrawMode.STRETCH:
	        	this.drX = 0;
	        	this.drY = 0;
	        	break;
	        	
            case ImageDrawMode.FIT_IMAGE:
            case ImageDrawMode.RESIZEABLE:

                this._ZoomToFit();
                iWidth  = (int)(iWidth  * this._zoom);
                iHeight = (int)(iHeight * this._zoom);

                this.drX = (cWidth >> 1) - (iWidth >> 1);
                this.drY = (cheight >> 1) - (iHeight >> 1);
                break;

            case ImageDrawMode.ACTUAL_SIZE:

//                if (iWidth < cWidth)
//                {
                    this.drX = (cWidth >> 1) - (iWidth >> 1);
//                }

//                if (iHeight < cheight)
//                {
                    this.drY = (cheight >> 1) - (iHeight >> 1);
//                }
                break;

            case ImageDrawMode.DOWNSCALE_IMAGE:
                if (iWidth > cWidth || iHeight > cheight)
                {
                	this._ZoomToFit();
                	
                    iWidth  = (int)(iWidth  * this._zoom);
                    iHeight = (int)(iHeight * this._zoom);
                }

                this.drX = (cWidth  >> 1) - (iWidth  >> 1);
                this.drY = (cheight >> 1) - (iHeight >> 1);
                break;
        }

        this.onImageZoomChanged();
        this.repaint();
    }
    
    
    /**
     * zooms the image to fit and repaints the control
     */
    public void ZoomToFit()
    {
    	_ZoomToFit();
    	repaint();
    }
    
    
    /**
     * zooms to fit but does not repaint the control
     */
    private void _ZoomToFit()
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

        this._zoom = zoom / 100d;
    }
    
    
    /**
     * gets the current image
     * @return
     */
    public ImageBase getImage()
    {
    	return this.image;
    }
    
    
    /**
     * gets the image width
     * @return
     */
    public int getImageWidth()
    {
    	return this.imageWidth;
    }
    
    
    /**
     * gets the image height
     * @return
     */
    public int getImageHeight()
    {
    	return this.imageHeight;
    }
    
    
    /**
     * gets the background color 1
     * @return
     */
    public Color getCellColor1()
    {
    	return this.cellColor1;
    }
    
    
    /**
     * sets the background color 1
     * @param c
     */
    public void setCellColor1(Color c)
    {
    	this.cellColor1 = c;
    }
    
    
    /**
     * gets the background color 2 
     * @return
     */
    public Color getCellColor2()
    {
    	return this.cellColor2;
    }
    
    
    /**
     * sets the background color 2
     * @param c
     */
    public void setCellColor2(Color c)
    {
    	this.cellColor2 = c;
    }
    
    
    /**
     * gets the color of the image border
     * @return
     */
    public Color getImageBorderColor()
    {
    	return this.imageBorderColor;
    }
    
    
    /**
     * sets the image border color 
     * @param c
     */
    public void setImageBorderColor(Color c)
    {
    	this.imageBorderColor = c;
    	this.repaint();
    }
    
    
    /**
     * gets the current zoom as a percentage
     * @return
     */
    public int getZoomPercent()
    {
    	return (int)(this._zoom * 100);
    }
    
    
    /**
     * sets the current zoom, assumes a percentage < MAX_ZOOM_PERCENT and > MIN_ZOOM_PERCENT 
     * @param value the percentage of zoom
     */
    public void setZoomPercent(int value)
    {
    	if(value > MAX_ZOOM_PERCENT || 
    	   value < MIN_ZOOM_PERCENT)
    		return;
    	
    	this._zoom = value / 100d;
    	this.repaint();
    	this.onImageZoomChanged();
    }
    
    
    /**
     * sets the zoom level and centers the image
     * @param value
     */
    public void setZoomPercentAndZoomCenter(int value)
    {
    	if(value > MAX_ZOOM_PERCENT || 
    	   value < MIN_ZOOM_PERCENT)
    		return;
    	    	
    	double before = this._zoom;
    	
    	this._zoom = value / 100d;
    	
    	this.ZoomCenterImage(before, this._zoom);
    	this.repaint();
    	this.onImageZoomChanged();
    }
    
    
    /**
     * gets the current zoom level
     * @return
     */
    public double getZoom() 
    {
		return this._zoom;
	}
    
    
    /**
     * sets the current zoom level
     * @param value
     */
    public void setZoom(double value) 
    {
    	if (value > MAX_ZOOM ||
    		value < MIN_ZOOM)
            return;

    	this._zoom = value;
        this.repaint();
        this.onImageZoomChanged();
	}
    
    
    /**
     * sets if the control should draw the image border
     * @param border
     */
    public void setDrawBorder(boolean border)
    {
    	this.drawImageBorder = border;
    	this.repaint();
    }
    
    
    /**
     * gets if the image border is being shown
     * @return
     */
    public boolean getImageBorder()
    {
    	return this.drawImageBorder;
    }
    
    
    /**
     * sets if animation should be paused
     * @param isPaused
     */
    public void setAnimationPaused(boolean isPaused)
    {
    	this.animationPaused = isPaused;
    	
    	if(!isPaused)
    	{
    		this.timer.start();
    	}
    	else 
    	{
    		this.timer.stop();
    	}
    	
    	this.repaint();
    }
    
    
    /**
     * gets if animation is paused
     * @return
     */
    public boolean getAnimationPaused()
    {
    	return this.animationPaused;
    }
    
    
    /**
     * sets the current frame of an animation
     * @param index
     */
    public void setAnimationFrame(int index)
    {
    	if(!this.isAnimating)
    		return;
    	
    	GifBase g = (GifBase)this.image;
    	
    	g.setFrameIndex(index);
    	
    	if(this.isAnimating)
    		setAnimationPaused(true);
    }
    

    /**
     * sets the {@link AntiAliasing} and repaints the control
     * @param antialiasing the {@link AntiAliasing} to use 
     */
    public void setAntiAliasing(int antialiasing)
    {
    	this.antiAliasing = (byte) antialiasing;
    	this.antiAliasingObject = AntiAliasing.getMode(antialiasing); 
    	this.repaint();
    }
    
    
    /**
     * gets the {@link AntiAliasing} currently in use
     * @return {@link Byte} with the value of the current {@link AntiAliasing} 
     */
    public byte getAntiAliasing()
    {
    	return this.antiAliasing;
    }
    
    
    /**
     * sets the current {@link RenderQuality} and repaints the control
     * @param renderQuality the {@link RenderQuality} to use
     */
	public void setRenderQuality(int renderQuality) 
	{
		this.renderQuality = (byte) renderQuality;
		this.renderQualityObject = RenderQuality.getMode(renderQuality);
		this.repaint();
	}
	
	
	/**
     * gets the {@link RenderQuality} currently in use
     * @return {@link Byte} with the value of the current {@link RenderQuality} 
     */
	public byte getRenderQuality()
	{
		return this.renderQuality;
	}

    
	/**
	 * sets the current {@link InterpolationMode} and repaints the control
	 * @param interpolationMode the {@link InterpolationMode} to use
	 */
    public void setInterpolationMode(int interpolationMode)
    {
    	this.interpolationMode = (byte) interpolationMode;
    	this.interpolationModeObject = InterpolationMode.getMode(interpolationMode);
    	this.repaint();
    }
    
    
    /**
     * gets the {@link InterpolationMode} currently in use
     * @return {@link Byte} with the value of the current {@link InterpolationMode} 
     */
    public byte getInterpolationMode()
    {
    	return this.interpolationMode;
    }
    
    
    /**
     * gets the {@link ImageDrawMode} currently in use
     * @return {@link Byte} with the value of the current {@link ImageDrawMode} 
     */
    public byte getDrawMode()
    {
    	return this.drawMode;
    }
    
    
    /**
     * sets the current {@link ImageDrawMode} and centers the image
     * @param mode the {@link ImageDrawMode} to use
     */
    public void setDrawMode(int mode)
    {
    	this.drawMode = (byte)mode;
    	CenterCurrentImage();
    }
    

    /**
     * gets the position the image is going to be drawn and at what size
     * @return {@link Rect} containing the x, y, width and height of visible image
     */
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
	        case ImageDrawMode.STRETCH:
	        	return new Rect(0, 0, cWidth, cHeight);
	        	
	        
            case ImageDrawMode.RESIZEABLE:

            	// calculate scaled image size 
                width  = (int)(width  * this._zoom);
                height = (int)(height * this._zoom);

                // prevent the image from getting lost off the left and top side of the control
                // the right side is handled elsewhere so we don't need to worry about it here 
                if (xPos < -width)
                {
                    xPos = -width;
                    drX = xPos;
                }

                if (yPos < -height)
                {
                    yPos = -height;
                    drY = yPos;
                }

                return new Rect(xPos, yPos, width, height);

                
            case ImageDrawMode.ACTUAL_SIZE:

            	// since ACTUAL_SIZE mode only lets you drag the image if it's width / height > control width / height
            	// we can either center it otherwise or draw it from 0, 0
                if (this.centerImage)
                {
                	// if the image width < control width
                	// we lock the image so that it's x position centers it 
                    if (width < cWidth)
                    {
                        xPos = (cWidth >> 1) - (width >> 1);
                    }
                    
                    // otherwise the user can drag the image free along the x axis
                    // just need to keep it from getting lost 
                    else if (xPos < -width)
                    {
                        xPos = -width;
                        drX = xPos;
                    }

                    // same thing as above but for the image height and y
                    if (height < cHeight)
                    {
                        yPos = (cHeight >> 1) - (height >> 1);
                    }
                    else if(yPos < -height)
                    {
                        yPos = -height;
                        drY = yPos;     
                    }
                }
                else
                {
                	// if the width < control width lock the image x at 0
                    if (width < cWidth)
                    {
                        xPos = 0;
                        drX = 0;
                    }

                    // same as above but for height and y 
                    if (height < cHeight)
                    {
                        yPos = 0;
                        drY = 0;
                    }
                }

                return new Rect(xPos, yPos, width, height);


            case ImageDrawMode.FIT_IMAGE:
            	
            	// always forces the image to fit 
            	_ZoomToFit();

                // calculate visible image size
                width  = (int)(width * this._zoom);
                height = (int)(height * this._zoom);

                // lock the image so it's centered
                if (this.centerImage)
                {
                    xPos = (cWidth  >> 1) - (width  >> 1);
                    yPos = (cHeight >> 1) - (height >> 1);
                }
                
                // or lock the image at 0, 0
                else
                {
                    xPos = 0;
                    yPos = 0;
                }

                return new Rect(xPos, yPos, width, height);

                
            // if someone set the drawMode to something it shouldn't be,
            // correct them here and reset it 
            default:
            	
            	LoggerWrapper.log(Level.WARNING, "bad drawMode [" + this.drawMode + "], resetting to DOWNSCALE_IMAGE");
            	
            	this.drawMode = ImageDrawMode.DOWNSCALE_IMAGE;
            	
            	// i love fall through, it's very handy 
            	
            case ImageDrawMode.DOWNSCALE_IMAGE:

            	// if the image width or height is bigger than the control width / height
            	// zoom the image so it fits
                if (width > cWidth || height > cHeight)
                {
                	_ZoomToFit();

                    width  = (int)(width  * this._zoom);
                    height = (int)(height * this._zoom);
                }

                // lock the image so it's centered
                if (this.centerImage)
                {
                    xPos = (cWidth  >> 1) - (width  >> 1);
                    yPos = (cHeight >> 1) - (height >> 1);
                }
                // lock the image at 0, 0
                else
                {
                    xPos = 0;
                    yPos = 0;
                }

                return new Rect(xPos, yPos, width, height);
        }
    }
    
    
    
    /**
     * adjust the x and y position of the image so the zoom appears to be onto the mouse location
     * @param beforeZoom the old zoom value
     * @param nowZoom the new zoom value
     * @param mousePosition the mouse position 
     */
    private void ZoomIntoMouse(double beforeZoom, double nowZoom, Point mousePosition) 
    {
		double scaleRatio = (this.imageWidth * beforeZoom) / (this.imageWidth * nowZoom);
		
		double mouseOffsetX = mousePosition.getX() - this.drX;
		double mouseOffsetY = mousePosition.getY() - this.drY;

		this.drX = (int)(drX - ((mouseOffsetX / scaleRatio) - mouseOffsetX));
		this.drY = (int)(drY - ((mouseOffsetY / scaleRatio) - mouseOffsetY));

		
		// this isn't perfect but it's the best i got rn
		// it for the most part keeps the selection in the same spot, but moves it slightly
		// would be nice to get it perfect but i'm not sure what i'd need to change for that to happen
		if(this.selectionArea.checkInvalid())
			return;

		scaleRatio = Math.abs((this.selectionArea.getWidth() * beforeZoom) / (this.selectionArea.getWidth() * nowZoom));
		
		mouseOffsetX = mousePosition.getX() - this.selectionArea.x1;
		mouseOffsetY = mousePosition.getY() - this.selectionArea.y1;
		this.selectionArea.x1 = (int)(this.selectionArea.x1 - ((mouseOffsetX / scaleRatio) - mouseOffsetX));
		this.selectionArea.y1 = (int)(this.selectionArea.y1 - ((mouseOffsetY / scaleRatio) - mouseOffsetY));

		mouseOffsetX = mousePosition.getX() - this.selectionArea.x2;
		mouseOffsetY = mousePosition.getY() - this.selectionArea.y2;
		this.selectionArea.x2 = (int)(this.selectionArea.x2 - ((mouseOffsetX / scaleRatio) - mouseOffsetX));
		this.selectionArea.y2 = (int)(this.selectionArea.y2 - ((mouseOffsetY / scaleRatio) - mouseOffsetY));
	}
    
    
    
    /**
     * adjust the x and y position of the image so the mouse is centered on the image
     * @param afterZoom the new zoom value
     * @param mousePosition the mouse position 
     */
    protected void ZoomCenterMouse(double afterZoom, Point mousePosition)
    {
        double afterZoomWidth = imageWidth * afterZoom;
        double afterZoomHeight = imageHeight * afterZoom;

        this.drX = (int)(mousePosition.getX() - (afterZoomWidth / 2d));
        this.drY = (int)(mousePosition.getY() - (afterZoomHeight / 2d));
    }

    
    
    /**
     * adjust the x and y position of the image so the zoom appears to be into the bottom right corner
     * @param beforeZoom the zoom value before it changed
     * @param afterZoom the new zoom value
     */
    protected void ZoomBottomRightImage(double beforeZoom, double afterZoom)
    {
        double beforeZoomWidth = imageWidth * beforeZoom;
        double beforeZoomHeight = imageHeight * beforeZoom;

        double afterZoomWidth = imageWidth * afterZoom;
        double afterZoomHeight = imageHeight * afterZoom;

        this.drX -= (int)(afterZoomWidth - beforeZoomWidth);
        this.drY -= (int)(afterZoomHeight - beforeZoomHeight);
    }


    
    /**
     * adjust the x and y position of the image so the zoom appear to be into the center of the image
     * @param beforeZoom the zoom value before it changed
     * @param afterZoom the new zoom value
     */
    protected void ZoomCenterImage(double beforeZoom, double afterZoom)
    {
        double beforeZoomWidth = imageWidth * beforeZoom;
        double beforeZoomHeight = imageHeight * beforeZoom;

        double afterZoomWidth = imageWidth * afterZoom;
        double afterZoomHeight = imageHeight * afterZoom;

        this.drX -= (int)(afterZoomWidth - beforeZoomWidth) >> 1;
        this.drY -= (int)(afterZoomHeight - beforeZoomHeight) >> 1;
    }


    
    /**
     * handles zooming in and out with the mouse
     * @param isZoomIn true = in, false = out
     */
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
    
    

    
    
    /**
     * draws the checkerboard background pattern
     * @param g {@link Graphics} object from paintComponent
     * @param g2 {@link Graphics2D} object from paintComponent
     */
    protected void drawBackground(Graphics g, Graphics2D g2)
    {
        g2.setPaint(this.tileBrush);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    
    
    
    
    
    
    /**
     * draws the image and image border to the display 
     * @param g {@link Graphics} object from paintComponent
     * @param g2 {@link Graphics2D} object from paintComponent
     */
    protected void drawImage(Graphics g, Graphics2D g2)
    {
    	Rect r = this.GetImageViewPort();
    
    	// set the current rendering settings
    	// since current the drawImage function is the last function called in paint
    	// we don't reset this, but might need to in the future 
        g2.setRenderingHint(RenderingHints.KEY_RENDERING    , this.renderQualityObject);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING , this.antiAliasingObject);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.interpolationModeObject);

        // uses x1 x2 etc
        // so we need to add the x to the width 
    	g.drawImage(image.getBuffered(), 
    			r.x, r.y, r.x + r.width, r.y + r.height, 
    			0, 0, this.imageWidth, this.imageHeight, this);
    	
    	// draw border around image bounds 
    	// this uses actual width / height for some reason? 
    	if(this.drawImageBorder)
    	{
    		g.setColor(Color.RED);
        	g.drawRect(r.x, r.y, r.width, r.height);
    	}
    }
    
    
    
    protected void drawSelectionArea(Graphics g, Graphics2D g2)
    {
    	this.selectionArea.render(g, g2);
    }
    
    
    
    /**
     * creates the tile brush used to draw the checkerboard pattern for the background
     */
    protected void initTileBrush()
    {        
        int width  = (int)(cellSize * 2 * this.cellScale);
        int height = (int)(cellSize * 2 * this.cellScale);
        
        // optimized BufferedImage of type RGB 
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = result.getGraphics();
        
        // draw smallest pattern
        // O X
        // X O 
        
        // draw X
        g.setColor(this.cellColor1);
        g.fillRect(cellSize, 0, cellSize, cellSize);
        g.fillRect(0, cellSize, cellSize, cellSize);
        
        // draw O
        g.setColor(this.cellColor2);
        g.fillRect(0, 0, cellSize, cellSize);
        g.fillRect(cellSize, cellSize, cellSize, cellSize);
        
        g.dispose();
    
        // tile brush for optimized drawing of the entire control area with the small graphic generated above 
        Rectangle2D bounds = new Rectangle2D.Float(0, 0, result.getWidth(), result.getHeight());
        
        this.tileBrush = new TexturePaint(result, bounds);
    }
    
    
    
    
    
    
    
    /**
     * find the nearest zoom in the {@link this.ZOOM_PERCENT} array and returns it's index
     * @param zoomLevel, the zoom percentage
     * @return the index of the closest zoom percent in the ZOOM_PERCENT array
     */
    public int FindNearest(int zoomLevel)
    {
    	// TODO: use a binary search here 
    	
        int nearestValue = 0;
        int nearestDifference = Math.abs(ZOOM_PERCENT[0] - zoomLevel);
        
        for (int i = 1; i < ZOOM_PERCENT.length; i++)
        {
            int difference = Math.abs(ZOOM_PERCENT[i] - zoomLevel);
            
            if (difference < nearestDifference)
            {
                nearestValue = i;
                nearestDifference = difference;
            }
        }
        return nearestValue;
    }
    
    
    
    
    
    
    /**
     * register the given object to the event listener to receive events from this class 
     * @param lis a class which implements {@link ImageDisplayListener}
     */
    public void addImageDisplayListener(ImageDisplayListener lis) 
    {
    	this.listeners.add(lis);
	}
    
    
    
    /**
     * called when the image changes to notify listeners
     */
    protected void onImageChanged() 
    {
    	for(ImageDisplayListener ls : this.listeners)
    	{
    		ls.ImageChanged();
    	}
	}
    
    
    
    /**
     * called when the image zoom changes to notify listeners
     */
    protected void onImageZoomChanged() 
    {
    	ImageDisplayZoomChangedEvent event = new ImageDisplayZoomChangedEvent(this.getZoomPercent(), this._zoom);
    	
    	for(ImageDisplayListener ls : this.listeners)
    	{
    		ls.ImageZoomChanged(event);
    	}
	}
    
    
    
    
    
    
    /**
     * called when the image size changes to notify listeners
     * @param newWidth the new width of the image
     * @param newheight the new height of the image
     * @param oldWidth the old width of the image
     * @param oldHeight the old height of the image
     */
    protected void onImageSizeChanged(int newWidth, int newheight, int oldWidth, int oldHeight) 
    {
    	ImageDisplayImageSizeChangedEvent event = new ImageDisplayImageSizeChangedEvent(newWidth, newheight, oldWidth, oldHeight);
    	
    	for(ImageDisplayListener ls : this.listeners)
    	{
    		ls.ImageSizeChanged(event);
    	}
	}
    
    
    
    
    
    @Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g);
    	
    	this.drawBackground(g, (Graphics2D) g);
    	
    	if(this.image != null)
    	{	
    		this.drawImage(g, (Graphics2D) g);
    	}
    	
    	this.drawSelectionArea(g, (Graphics2D)g);
    }
    

    
    
    
    
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if(e.getButton() == this.mouseDragButton)
		{
			// important this comes before adjustSizeOrDie, 
			// otherwise we cannot use the lastClickPoint to drag the selection box around
			this.lastClickPoint.setLocation(e.getPoint());
			
			// adjustSizeOrDie will set this.selectionArea.changing if it returns true
			// this will then be used in the mouse dragged function to highjack the image drag and handle the selection instead
			if(this.allowSelection && !this.selectionArea.isInvalid && this.selectionArea.adjustSizeOrDie(e.getPoint()))
			{
				this.isDragButtonDown = true;
				this.isSelectionButtonDown = false;
				return;
			}
			
			// this applies to the image, not the selection
			if(!this.allowDrag)
				return;
			
			this.isDragButtonDown = true;
			this.isSelectionButtonDown = false;
			this.selectionArea.resetChanging();
		}
		
		else if(e.getButton() == this.mouseSelectionButton)
		{
			if(!this.allowSelection)
				return;
			
			this.isSelectionButtonDown = true;
			this.isDragButtonDown = false;
			
			this.selectionArea.updatePoint1(e.getPoint());
		}
	}
	
	
	
	
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		// always make sure to reset this, otherwise anytime the drag button is down, its always changing the selection
		this.selectionArea.resetChanging();
		
		if(e.getButton() == this.mouseDragButton)
		{
			this.isDragButtonDown = false;
		}
		
		else if(e.getButton() == this.mouseSelectionButton)
		{
			this.isSelectionButtonDown = false;
			
			if(!this.allowSelection)
				return;
			
			this.selectionArea.updatePoint2(e.getPoint());
			this.selectionArea.checkInvalid();
			this.repaint();
		}
	}
	
	
	
	
	
	
	@Override
    public void mouseDragged(MouseEvent e) 
	{
		if(this.isDragButtonDown)
		{
			if(!this.selectionArea.isInvalid &&
				this.selectionArea.isExpanding() &&
				this.selectionArea.adjustSizeOrDie(e.getPoint()))
			{
				this.repaint();
				return;
			}
			
			// resizeable mode lets you free drag the image
			// actual size mode lets you drag the image if it's bigger than the control wdith / height
			// nothing else lets you drag it so stop here 
			if(this.drawMode != ImageDrawMode.RESIZEABLE && this.drawMode != ImageDrawMode.ACTUAL_SIZE)
				return;
			
			final int xoff = this.lastClickPoint.x - e.getX();
			final int yoff = this.lastClickPoint.y - e.getY();
			
			this.drX -= xoff;
			this.drY -= yoff;
			
			this.selectionArea.x1 -= xoff; 
			this.selectionArea.x2 -= xoff;
			this.selectionArea.y1 -= yoff; 
			this.selectionArea.y2 -= yoff;
			
			if(this.drX > this.getWidth())
				this.drX = this.getWidth();
			
			if(this.drY > this.getHeight())
				this.drY = this.getHeight();
			
			this.lastClickPoint.setLocation(e.getPoint());
			
			this.repaint();
		}

		
		else if(this.isSelectionButtonDown)
		{
			this.selectionArea.updatePoint2(e.getPoint());
			this.selectionArea.checkInvalid();
			this.repaint();
		}
    }
	
	
	
	
	
	
	@Override 
	public void mouseWheelMoved(MouseWheelEvent e)
	{
        if (this.image == null || this.drawMode != ImageDrawMode.RESIZEABLE)
            return;


        // TODO: figure out how to get the mouse wheel delta thing
        // // C# code that does ^
        // // int spins = Math.Abs(e.Delta / SystemInformation.MouseWheelScrollDelta);
        
        double beforeZoom = this._zoom;
        
        int dx = this.drX;
        int dy = this.drY;
        
        if(e.getWheelRotation() < 0)
        {
        	this.MouseZoom(true);
        }
        else 
        {
        	this.MouseZoom(false);
        }
        
        // adjusts the image x and y position after the zoom has changed 
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
	public void actionPerformed(ActionEvent e) 
	{
		if(this.isAnimating)
    	{
			GifBase _image = (GifBase)this.image;
    		
    		_image.updateCurrentFrame();
    	
    		this.timer.setDelay(_image.getCurrentFrameDelay());
    		
    		this.repaint();
    	}
	}
	
	

	@Override
    public void setVisible(boolean makeVisible)
    {
		// pause the animation timer when the control visibility changes
		// for some reason using any of https://stackoverflow.com/a/10881669 solutions didn't work
		// so overriding this is the way to go it would seem 
    	if(makeVisible)
    	{
    		if(this.isAnimating && !this.animationPaused)
    		{
    			this.timer.start();
    		}
        	else 
        	{
        		this.timer.stop();
        	}
    	}
    	else 
    	{
    		this.timer.stop();	
    	}
    	
    	super.setVisible(makeVisible);
    }
	
	@Override
	public void mouseClicked(MouseEvent e) 	{	}
	
	@Override
	public void mouseEntered(MouseEvent e) 	{	}
	
	@Override
	public void mouseExited(MouseEvent e) 	{	}
	
    @Override
    public void mouseMoved(MouseEvent e)    {   }


	@Override
	public void componentResized(ComponentEvent e) 
	{
		if(!GUISettings.CENTER_IMAGE_ON_RESIZE || this.image == null)
			return;
		
       // this.CenterCurrentImageWithoutResize();
       // pasted the above functions code here to not have the repaint called cause yes :)
		
	   final int iWidth = (int)(this.imageWidth  * this._zoom);
       final int iHeight =(int)(this.imageHeight * this._zoom);

       this.drX = (this.getWidth() >> 1) - (iWidth >> 1);
       this.drY = (this.getHeight() >> 1) - (iHeight >> 1);
	}


	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}