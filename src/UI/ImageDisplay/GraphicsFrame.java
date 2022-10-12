package UI.ImageDisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class GraphicsFrame extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	public static final double MAX_ZOOM = 200d;
    
    public static final double MIN_ZOOM = 0.01d;
    
    public static final int MAX_ZOOM_PERCENT = (int)(MAX_ZOOM / 100d);

    public static final int MIN_ZOOM_PERCENT = (int)(MIN_ZOOM / 100d);
    
    
    
    public static Color DefaultCellColor1 = new Color(32, 32, 32);
    public static Color DefaultCellColor2 = new Color(64, 64, 64);

    private Color cellColor1 = DefaultCellColor1;
    
    private Color cellColor2 = DefaultCellColor2;
    
    private int cellSize ;
    
    private float cellScale;

    private boolean allowDrag = true;
    private boolean display = true;
    
    private boolean isAnimating = false;
    
    private boolean animationPaused = false;
    
    private boolean centerImage = false;

	private boolean drawImage = true;
	
    private Image image;

    private int mouseDragButton = MouseEvent.BUTTON1;
    
    private Point lastClickPoint = new Point(0, 0);
    
    // image draw position x
    private int drX = 0;
    
    // image draw position y 
    private int drY = 0;
    
    private boolean isDragButtonDown = false;

    private double _zoom = 1;
    
    
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
	}
    
    //introduce constructor
    public GraphicsFrame() 
    {
    	super.addMouseListener(this);
    	super.addMouseMotionListener(this);
    	super.addMouseWheelListener(this);
    }
    
    public void loadImage(Image i)
    {
    	if(this.image != null) 
    	{
    		this.image.flush();
    		this.image = null;
    	}
    	
    	this.image = i;
    	
    	this.repaint();
    }
    
    public void setDrawImage(boolean drawImage) 
    {
		this.drawImage = drawImage;
		
		this.repaint();
	}
    
    public boolean getDrawImage()
    {
    	return this.drawImage;
    }

    
    protected void MouseZoom(boolean isZoomIn)
    {
//        int newZoom;
//        int currentZoom = this.getZoomPercent();

        if (isZoomIn)
        {
        	this._zoom *= 1.1111;
//            newZoom = this.ZoomLevels.NextZoom(currentZoom);
        }
        else
        {
//            newZoom = this.ZoomLevels.PreviousZoom(currentZoom);
        	this._zoom /= 1.1111;
        }

//        if (newZoom == currentZoom)
//            return;
//
//        this.setZoomPercent(newZoom);
    }
    
    
    protected Rectangle GetImageViewPort()
    {
        if (this.image == null)
            return new Rectangle();

        int xPos = drX;
        int yPos = drY;

        int width  = this.image.getWidth(null);
        int height = this.image.getHeight(null);


        width  = (int)(width  * this._zoom);
        height = (int)(height * this._zoom);

        // prevent the image from getting lost off the left and top side of the control
//        if (drX < -width)
//        {
//            xPos = -width;
//            drX = xPos;
//        }
//
//        if (drY < -height)
//        {
//            yPos = -height;
//            drY = yPos;
//        }

        return new Rectangle(xPos, yPos, xPos + width, yPos + height);
    }
    
    @Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g);
    	
    	if(this.image != null && this.drawImage)
    	{
//            g.drawImage(image, this.drX, this.drY, image.getWidth(null), image.getHeight(null), null);
            Rectangle r = this.GetImageViewPort();
            g.drawImage(image, this.drX, this.drY, (int)r.getWidth(), (int)r.getHeight(), 0, 0, this.image.getWidth(null), this.image.getHeight(null), null);
            
//            g.drawImage(image, this.drX, this.drY,r.getWidth(), r.getHeight(), 0, 0, this.image.getWidth(null), this.image.getHeight(null), null);
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
		if(!this.isDragButtonDown)
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
//
//        if (this.DrawMode != ImageDrawMode.Resizeable)
//            return;
        
        
        
//        System.out.println(e.getScrollAmount());
//        System.out.println(e.getScrollType());
//        System.out.println(e.getWheelRotation());
//        
        if(e.getWheelRotation() > 0)
        {
        	MouseZoom(true);
        }
        else 
        {
        	MouseZoom(false);
        }
        this.repaint();
        
//        // The MouseWheel event can contain multiple "spins" of the wheel so we need to adjust accordingly
//        int spins = Math.Abs(e.Delta / SystemInformation.MouseWheelScrollDelta);
//
//        // TODO: Really should update the source method to handle multiple increments rather than calling it multiple times
//        for (int i = 0; i < spins; i++)
//        {
//            double beforeZoom = this._Zoom;
//
//            this.MouseZoom(e.Delta > 0);
//
//            switch (ZoomMode)
//            {
//                case ZoomMode.TopLeftImage:
//                    break;
//
//                case ZoomMode.BottomRightImage:
//                    ZoomBottomRightImage(beforeZoom, this._Zoom);
//                    break;
//
//                case ZoomMode.IntoMouse:
//                    ZoomIntoMousePosition(beforeZoom, this._Zoom, e.Location);
//                    break;
//
//                case ZoomMode.CenterImage:
//                    ZoomCenterImage(beforeZoom, this._Zoom);
//                    break;
//
//                case ZoomMode.CenterMouse:
//                    ZoomCenterMouse(this._Zoom, e.Location);
//                    break;
//            }
//        }
//
//        Invalidate();
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