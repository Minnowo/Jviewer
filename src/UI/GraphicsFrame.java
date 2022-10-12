package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphicsFrame extends JPanel implements MouseListener, MouseMotionListener 
{

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
    
    //introduce constructor
    public GraphicsFrame() 
    {
    	super.addMouseListener(this);
    	super.addMouseMotionListener(this);
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

    @Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g);
    	
    	if(this.image != null && this.drawImage)
    	{
            g.drawImage(image, this.drX, this.drY, 400, 400, null);
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
	public void mouseClicked(MouseEvent e) 	{	}
	
	@Override
	public void mouseEntered(MouseEvent e) 	{	}
	
	@Override
	public void mouseExited(MouseEvent e) 	{	}
	
    @Override
    public void mouseMoved(MouseEvent e)    {   }

}