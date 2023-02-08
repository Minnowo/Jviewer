package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;

public interface IPixelTransform
{
    Color Transform(Color pixel);
    
    int Transform(int pixel);
}
