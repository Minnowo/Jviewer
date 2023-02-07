package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;

public interface IErrorDiffusion
{
    public void diffuse(Color[] data, Color original, Color transformed, int x, int y, int width, int height);
    
    public boolean prescan();
}
