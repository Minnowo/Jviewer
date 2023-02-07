package nyaa.alice.jviewer.drawing.imaging.dithering;

public class Dithers
{
    public static class FloydSteinbergDithering extends ErrorDiffusionDithering
    {
        public FloydSteinbergDithering()
        {
            super(new byte[][] { { 0, 0, 7 }, { 3, 5, 1 } }, (byte) 4, true);
        }
    }

    public static class AtkinsonDithering extends ErrorDiffusionDithering
    {
        public AtkinsonDithering()
        {
            super(new byte[][] { { 0, 0, 1, 1 }, { 1, 1, 1, 0 }, { 0, 1, 0, 0 } }, (byte)3, true);
        }
    }
}
