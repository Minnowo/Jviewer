package UI.ImageDisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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

import Graphics.ImageUtil;
import UI.Events.ImageZoomChangedEvent;
import UI.Events.Listeners.ImageDisplayListener;
import UI.ImageDisplay.Enums.AntiAliasing;
import UI.ImageDisplay.Enums.InterpolationMode;
import UI.ImageDisplay.Enums.RenderQuality;
import UI.ImageDisplay.Enums.ZoomType;

public class GraphicsFrame extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	private Set<ImageDisplayListener> listeners = new HashSet<ImageDisplayListener>();

	public static final double MAX_ZOOM = 200d;
    
    public static final double MIN_ZOOM = 0.01d;
    
    public static final int MAX_ZOOM_PERCENT = (int)(MAX_ZOOM * 100d);

    public static final int MIN_ZOOM_PERCENT = (int)(MIN_ZOOM * 100d);
    
    public static final int[] ZOOM_PERCENT = { 4, 7, 10, 15, 20, 25, 30, 50, 70, 100, 
    		150, 200, 300, 400, 500, 600, 700, 800, 1200, 1600, 2000, 2400, 2800, 
    		3200, 3600, 4000, 4400, 4800, 5200, 5600, 6000, 6400, 6800, 7200, 
    		7600, 8000, 8400, 8800, 9200, 9600, 10000, 10400, 10800, 11200, 11600, 12000,
    		12400, 12800, 13200, 13600, 14000, 14400, 14800, 15200, 15600, 16000, 16400,
    		16800, 17200, 17600, 18000, 18400, 18800, 19200, 19600, 20000 };
    
    private int zoomIndex = 9;
    
    public static Color DefaultCellColor1 = new Color(32, 32, 32);
    public static Color DefaultCellColor2 = new Color(64, 64, 64);

    public int mouseDragButton = MouseEvent.BUTTON1;
    
    public int zoomType = ZoomType.INTO_MOUSE;
    
    
    private int interpolationMode = InterpolationMode.NEAREST_NEIGHBOR;
    private Object interpolationModeObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    
    private int antiAliasing = AntiAliasing.DISABLED;
    private Object antiAliasingObject = RenderingHints.VALUE_ANTIALIAS_OFF;
    
    private int renderQuality = RenderQuality.QUALITY;
    private Object renderQualityObject = RenderingHints.VALUE_RENDER_QUALITY;
    
    private Color cellColor1 = DefaultCellColor1;
    
    private Color cellColor2 = DefaultCellColor2;
    
    private int cellSize ;
    
    private float cellScale;

    private boolean allowDrag = true;
    
    private boolean isAnimating = false;
    
    private boolean animationPaused = false;
    
    private boolean centerImage = false;

	private boolean display = true;
	
    private Image image;

    private int imageWidth;
    private int imageHeight;
    
    
    
    private Point lastClickPoint = new Point(0, 0);
    
    // image draw position x
    private int drX = 0;
    
    // image draw position y 
    private int drY = 0;
    
    private boolean isDragButtonDown = false;

    private double _zoom = 1;
    
    private TexturePaint tileBrush;
    
  //introduce constructor
    public GraphicsFrame() 
    {
    	super.addMouseListener(this);
    	super.addMouseMotionListener(this);
    	super.addMouseWheelListener(this);
    	
    	this.cellSize = 32;
    	this.cellScale = 1;
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
    	Image i = ImageUtil.loadImage(path);
    	
    	if(i == null)
    	{
    		return;
    	}
    	
    	this.setImage(i, flushLastImage);
    }
  
    public void setImage(Image image, boolean flushLastImage)
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
        	this.imageWidth = image.getWidth(null);
        	this.imageHeight = image.getHeight(null);
    	}
    	
    	this.repaint();
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
    	if(value > GraphicsFrame.MAX_ZOOM_PERCENT || 
    	   value < GraphicsFrame.MIN_ZOOM_PERCENT)
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
    	if (value > GraphicsFrame.MAX_ZOOM ||
    		value < GraphicsFrame.MIN_ZOOM)
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
        	if(this.zoomIndex + 1 == ZOOM_PERCENT.length)
        		return; 
        	
        	this.setZoomPercent(ZOOM_PERCENT[++this.zoomIndex]);
        }
        else
        {
        	if(this.zoomIndex - 1 == -1)
        		return; 
        	
        	this.setZoomPercent(ZOOM_PERCENT[--this.zoomIndex]);
        }
    }
    
    
    protected Rect GetImageViewPort()
    {
        int xPos = drX;
        int yPos = drY;

        int width  = this.image.getWidth(null);
        int height = this.image.getHeight(null);


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
    }
    
    
    protected void drawBackground(Graphics g, Graphics2D g2)
    {
        g2.setPaint(this.tileBrush);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    
    
    protected void drawImage(Graphics g, Graphics2D g2)
    {
    	Rect r = this.GetImageViewPort();
    
        g2.setRenderingHint(RenderingHints.KEY_RENDERING    , this.renderQualityObject);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING , this.antiAliasingObject);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.interpolationModeObject);
 
    	g.drawImage(image, 
    			this.drX, this.drY, r.width, r.height, 
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
		if(!this.isDragButtonDown || !this.allowDrag)
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