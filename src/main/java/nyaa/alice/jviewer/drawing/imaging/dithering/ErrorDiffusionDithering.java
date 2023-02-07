package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;

public class ErrorDiffusionDithering implements IErrorDiffusion
{

    private final byte _divisor;

    private final byte[][] _matrix;

    private final byte _matrixHeight;

    private final byte _matrixWidth;

    private final byte _startingOffset;

    private final boolean _useShifting;

    protected ErrorDiffusionDithering(byte[][] matrix, byte divisor, boolean useShifting)
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
        _matrixWidth = (byte) (matrix[0].length);
        _matrixHeight = (byte) (matrix.length);
        _divisor = divisor;
        _useShifting = useShifting;

        byte startingoffset = 0;
        for (int i = 0; i < _matrixWidth; i++)
        {
            if (matrix[0][i] != 0)
            {
                startingoffset = (byte) (i - 1);
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

                    r =  (((offsetPixel.getRed() + newR)));
                    g =  ((offsetPixel.getGreen() + newG));
                    b =  ((offsetPixel.getBlue() + newB));

                    data[offsetIndex] = new Color(r & 0xff, g & 0xff, b & 0xff, offsetPixel.getAlpha());
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
