package nyaa.alice.jviewer.data;

public class ByteHelper
{
    public static int clampByte(int value)
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
