package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Ditherer
{
    public static class WorkerData
    {
        public Color[] pixelData;
        public int width;
        public int height;
        public IErrorDiffusion dither;
        public IPixelTransform transform;
        public BufferedImage image;
    }

    public static Color[] getImageColors(BufferedImage image)
    {
        Color[] buff = new Color[image.getWidth() * image.getHeight()];

        int[] rgbs = new int[image.getWidth() * image.getHeight()];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgbs, 0, image.getWidth());

        for (int i = 0; i < rgbs.length; ++i)
        {
            int rgb = rgbs[i];
            int a = ((rgb >> 24) & 0xFF);
            int r = ((rgb >> 16) & 0xFF);
            int g = ((rgb >> 8) & 0xFF);
            int b = ((rgb & 0xFF));
            buff[i] = new Color(r, g, b, a);
        }

        return buff;
    }

    public static void setPixels(Color[] colors, BufferedImage buff)
    {
        int[] rgbs = new int[colors.length];

        for (int i = 0; i < rgbs.length; ++i)
        {
            Color c = colors[i];
            int a = c.getAlpha();
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            rgbs[i] = (a << 24) + (r << 16) + (g << 8) + b;
        }

        buff.setRGB(0, 0, buff.getWidth(), buff.getHeight(), rgbs, 0, buff.getWidth());
    }
    
    public static BufferedImage getImageFromArray(Color[] colors, int width, int height)
    {
        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] rgbs = new int[colors.length];

        for (int i = 0; i < rgbs.length; ++i)
        {
            Color c = colors[i];
            int a = c.getAlpha();
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            rgbs[i] = (a << 24) + (r << 16) + (g << 8) + b;
        }

        buff.setRGB(0, 0, width, height, rgbs, 0, width);

        return buff;
    }

    public static void GetTransformedImage(WorkerData workerData)
    {
        IPixelTransform transform = workerData.transform;
        IErrorDiffusion dither = workerData.dither;
        BufferedImage buff = workerData.image;

        int width = buff.getWidth();
        int height = buff.getHeight();

        Color[] pixelData = getImageColors(buff);

        if (dither != null && dither.prescan())
        {
            // perform the dithering on the source data before
            // it is transformed
            ProcessPixels(pixelData, width, height, null, dither, workerData);
            dither = null;
        }

        // scan each pixel, apply a transform the pixel
        // and then dither it
        ProcessPixels(pixelData, width, height, transform, dither, workerData);

        setPixels(pixelData, buff);
        // create the final bitmap
//        return getImageFromArray(pixelData, width, height);
    }

    public static void ProcessPixels(Color[] pixelData, int width, int height, IPixelTransform pixelTransform,
            IErrorDiffusion dither, WorkerData bw)
    {
        Color current;
        Color transformed;
        int index = 0;

        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
            {
                // if (bw != null && bw.Worker.CancellationPending == true)
                // {
                // bw.Args.Cancel = true;
                // return;
                // }

                current = pixelData[index];

                if (pixelTransform != null)
                {
                    transformed = pixelTransform.Transform(current);
                    pixelData[index] = transformed;
                }
                else
                {
                    transformed = current;
                }
                index++;

                // apply a dither algorithm to this pixel
                // assuming it wasn't done before
                dither.diffuse(pixelData, current, transformed, col, row, width, height);
            }
    }
}
