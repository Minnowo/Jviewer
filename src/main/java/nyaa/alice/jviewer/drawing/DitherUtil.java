package nyaa.alice.jviewer.drawing;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DitherUtil
{

    public static BufferedImage dithering(BufferedImage image)
    {
        Color3i[] palette = new Color3i[] { new Color3i(0, 0, 0), new Color3i(255, 255, 255) };

        int width = image.getWidth();
        int height = image.getHeight();

        Color3i[][] buffer = new Color3i[height][width];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                buffer[y][x] = new Color3i(image.getRGB(x, y));
            }
        }

        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                Color3i old = buffer[y][x];
                Color3i nem = findClosestPaletteColor(old, palette);
                image.setRGB(x, y, nem.toColor().getRGB());

                Color3i error = old.sub(nem);

                if (x + 1 < width)
                    buffer[y][x + 1] = buffer[y][x + 1].add(error.mul(7. / 16));
                if (x - 1 >= 0 && y + 1 < height)
                    buffer[y + 1][x - 1] = buffer[y + 1][x - 1].add(error.mul(3. / 16));
                if (y + 1 < height)
                    buffer[y + 1][x] = buffer[y + 1][x].add(error.mul(5. / 16));
                if (x + 1 < width && y + 1 < height)
                    buffer[y + 1][x + 1] = buffer[y + 1][x + 1].add(error.mul(1. / 16));
            }
        }

        return image;
    }

    private static Color3i findClosestPaletteColor(Color3i match, Color3i[] palette)
    {
        Color3i closest = palette[0];

        for (Color3i color : palette)
        {
            if (color.diff(match) < closest.diff(match))
            {
                closest = color;
            }
        }

        return closest;
    }
}

class Color3i
{

    private int r, g, b;

    public Color3i(int c)
    {
        Color color = new Color(c);
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
    }

    public Color3i(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color3i add(Color3i o)
    {
        return new Color3i(r + o.r, g + o.g, b + o.b);
    }

    public Color3i sub(Color3i o)
    {
        return new Color3i(r - o.r, g - o.g, b - o.b);
    }

    public Color3i mul(double d)
    {
        return new Color3i((int) (d * r), (int) (d * g), (int) (d * b));
    }

    public int diff(Color3i o)
    {
        int Rdiff = o.r - r;
        int Gdiff = o.g - g;
        int Bdiff = o.b - b;
        int distanceSquared = Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
        return distanceSquared;
    }

    public int toRGB()
    {
        return toColor().getRGB();
    }

    public Color toColor()
    {
        return new Color(clamp(r), clamp(g), clamp(b));
    }

    public int clamp(int c)
    {
        return Math.max(0, Math.min(255, c));
    }
}