package nyaa.alice.jviewer.drawing.imaging.dithering;

public class DitherHelper
{
    public static int clamp(int value)
    {
        if (value < 0)
        {
            return 0;
        }

        if (value > 255)
        {
            return 255;
        }

        return value;
    }
}
