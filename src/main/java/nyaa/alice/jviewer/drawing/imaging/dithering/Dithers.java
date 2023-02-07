package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;
import java.util.Random;

public class Dithers
{
    public static class ErrorDiffusionDithering implements IErrorDiffusion
    {
        private final int _divisor;

        private final byte[][] _matrix;

        private final int _matrixHeight;

        private final int _matrixWidth;

        private final int _startingOffset;

        private final boolean _useShifting;

        protected ErrorDiffusionDithering(byte[][] matrix, int divisor, boolean useShifting)
        {
            if (matrix == null)
            {
                throw new NullPointerException("matrix argument cannot be null");
            }

            if (matrix.length == 0)
            {
                throw new IllegalArgumentException("Matrix is empty.");
            }

            _matrix = matrix;
            _matrixWidth = matrix[0].length;
            _matrixHeight = matrix.length;
            _divisor = DitherHelper.clamp(divisor);
            _useShifting = useShifting;

            int startingoffset = 0;
            for (int i = 0; i < _matrixWidth; i++)
            {
                if (matrix[0][i] != 0)
                {
                    startingoffset = (i - 1);
                    break;
                }
            }

            _startingOffset = startingoffset;
        }

        @Override
        public void diffuse(Color[] data, Color original, Color transformed, int x, int y, int width, int height)
        {
            int redError = original.getRed() - transformed.getRed();
            int blueError = original.getGreen() - transformed.getGreen();
            int greenError = original.getBlue() - transformed.getBlue();

            for (int row = 0; row < _matrixHeight; row++)
            {
                int offsetY = y + row;

                for (int col = 0; col < _matrixWidth; col++)
                {
                    int coefficient = _matrix[row][col];

                    int offsetX = x + (col - _startingOffset);

                    if (coefficient != 0 && offsetX > 0 && offsetX < width && offsetY > 0 && offsetY < height)
                    {
                        Color offsetPixel;
                        int offsetIndex;
                        int newR;
                        int newG;
                        int newB;
                        int r;
                        int g;
                        int b;

                        offsetIndex = offsetY * width + offsetX;
                        offsetPixel = data[offsetIndex];

                        // if the UseShifting property is set, then bit shift the values by the
                        // specified
                        // divisor as this is faster than integer division. Otherwise, use integer
                        // division

                        if (_useShifting)
                        {
                            newR = (redError * coefficient) >> _divisor;
                            newG = (greenError * coefficient) >> _divisor;
                            newB = (blueError * coefficient) >> _divisor;
                        }
                        else
                        {
                            newR = redError * coefficient / _divisor;
                            newG = greenError * coefficient / _divisor;
                            newB = blueError * coefficient / _divisor;
                        }

                        r = DitherHelper.clamp(offsetPixel.getRed() + newR);
                        g = DitherHelper.clamp(offsetPixel.getGreen() + newG);
                        b = DitherHelper.clamp(offsetPixel.getBlue() + newB);

                        data[offsetIndex] = new Color(r, g, b, offsetPixel.getAlpha());
                    }
                }
            }

        }

        @Override
        public boolean prescan()
        {
            // TODO Auto-generated method stub
            return false;
        }
    }

    public static class OrderedDithering implements IErrorDiffusion
    {

        private final byte[][] _matrix;

        private final byte _matrixHeight;

        private final byte _matrixWidth;

        protected OrderedDithering(byte[][] matrix)
        {
            int max;
            int scale;

            if (matrix == null)
            {
                throw new NullPointerException("Matrix cannot be null");
            }

            if (matrix.length == 0)
            {
                throw new IllegalArgumentException("Matrix is empty.");
            }

            _matrixWidth = (byte) (matrix[0].length);
            _matrixHeight = (byte) (matrix.length);

            max = _matrixWidth * _matrixHeight;
            scale = 255 / max;

            _matrix = new byte[_matrixHeight][_matrixWidth];

            for (int x = 0; x < _matrixWidth; x++)
            {
                for (int y = 0; y < _matrixHeight; y++)
                {
                    _matrix[x][y] = (byte) DitherHelper.clamp(matrix[x][y] * scale);
                }
            }
        }

        public void diffuse(Color[] data, Color original, Color transformed, int x, int y, int width, int height)
        {
            int row = y % _matrixHeight;
            int col = x % _matrixWidth;
            byte threshold = _matrix[col][row];

            if (threshold > 0)
            {
                int r = DitherHelper.clamp(transformed.getRed() + threshold);
                int g = DitherHelper.clamp(transformed.getGreen() + threshold);
                int b = DitherHelper.clamp(transformed.getBlue() + threshold);

                data[y * width + x] = new Color(r, g, b, original.getAlpha());
            }
        }

        public boolean prescan()
        {
            return true;
        }
    }

    public static class RandomDithering implements IErrorDiffusion
    {
        private final Color _black;

        private static final Color _blackDefault = new Color(0, 0, 0, 255);

        private final Random _random;

        private final Color _white;

        private static final Color _whiteDefault = new Color(255, 255, 255, 255);

        public RandomDithering()
        {
            this(_whiteDefault, _blackDefault);
        }

        public RandomDithering(Color white, Color black)
        {
            this(System.currentTimeMillis(), white, black);
        }

        public RandomDithering(long seed, Color white, Color black)
        {
            _random = new Random(seed);
            _white = white;
            _black = black;
        }

        public RandomDithering(int seed)
        {
            this(seed, _whiteDefault, _blackDefault);
        }

        @Override
        public boolean prescan()
        {
            return false;
        }

        public void diffuse(Color[] data, Color original, Color transformed, int x, int y, int width, int height)
        {
            int gray = (int) (0.299 * original.getRed() + 0.587 * original.getGreen() + 0.114 * original.getBlue());

            if (gray > _random.nextInt(0, 255))
            {
                data[y * width + x] = _white;
            }
            else
            {
                data[y * width + x] = _black;
            }
        }

    }

    public static class Bayer2 extends OrderedDithering
    {
        public Bayer2()
        {
            super(new byte[][] { { 0, 2 }, { 3, 1 } });
        }
    }

    public static class Bayer3 extends OrderedDithering
    {
        public Bayer3()
        {
            super(new byte[][] { { 0, 7, 3 }, { 6, 5, 2 }, { 4, 1, 8 } });
        }
    }

    public static class Bayer4 extends OrderedDithering
    {
        public Bayer4()
        {
            super(new byte[][] { { 0, 8, 2, 10 }, { 12, 4, 14, 6 }, { 3, 11, 1, 9 }, { 15, 7, 13, 5 } });
        }
    }

    public static class Bayer8 extends OrderedDithering
    {
        public Bayer8()
        {
            super(new byte[][] { { 0, 48, 12, 60, 3, 51, 15, 63 }, { 32, 16, 44, 28, 35, 19, 47, 31 },
                    { 8, 56, 4, 52, 11, 59, 7, 55 }, { 40, 24, 36, 20, 43, 27, 39, 23 },
                    { 2, 50, 14, 62, 1, 49, 13, 61 }, { 34, 18, 46, 30, 33, 17, 45, 29 },
                    { 10, 58, 6, 54, 9, 57, 5, 53 }, { 42, 26, 38, 22, 41, 25, 37, 21 } });
        }
    }

    public static class FloydSteinbergDithering extends ErrorDiffusionDithering
    {
        public FloydSteinbergDithering()
        {
            super(new byte[][] { { 0, 0, 7 }, { 3, 5, 1 } }, 4, true);
        }
    }

    public static class AtkinsonDithering extends ErrorDiffusionDithering
    {
        public AtkinsonDithering()
        {
            super(new byte[][] { { 0, 0, 1, 1 }, { 1, 1, 1, 0 }, { 0, 1, 0, 0 } }, 3, true);
        }
    }

    public static class BurksDithering extends ErrorDiffusionDithering
    {
        public BurksDithering()
        {
            super(new byte[][] { { 0, 0, 0, 8, 4 }, { 2, 4, 8, 4, 2 } }, 5, true);
        }
    }

    public static class JarvisJudiceNinkeDithering extends ErrorDiffusionDithering
    {
        public JarvisJudiceNinkeDithering()
        {
            super(new byte[][] { { 0, 0, 0, 7, 5 }, { 3, 5, 7, 5, 3 }, { 1, 3, 5, 3, 1 } }, 48, false);
        }
    }

    public static class Sierra2Dithering extends ErrorDiffusionDithering
    {
        public Sierra2Dithering()
        {
            super(new byte[][] { { 0, 0, 0, 4, 3 }, { 1, 2, 3, 2, 1 } }, 4, true);
        }
    }

    public static class Sierra3Dithering extends ErrorDiffusionDithering
    {
        public Sierra3Dithering()
        {
            super(new byte[][] { { 0, 0, 0, 5, 3 }, { 2, 4, 5, 4, 2 }, { 0, 2, 3, 2, 0 } }, 5, true);
        }
    }

    public static class SierraLiteDithering extends ErrorDiffusionDithering
    {
        public SierraLiteDithering()
        {
            super(new byte[][] { { 0, 0, 2 }, { 1, 1, 0 } }, 2, true);
        }
    }

    public static class StuckiDithering extends ErrorDiffusionDithering
    {
        public StuckiDithering()
        {
            super(new byte[][] { { 0, 0, 0, 8, 4 }, { 2, 4, 8, 4, 2 }, { 1, 2, 4, 2, 1 } }, 42, false);
        }
    }
}
