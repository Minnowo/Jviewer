package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.tinylog.Logger;

import nyaa.alice.jviewer.drawing.ColorUtil;

public class Ditherer
{
    public static class WorkerData
    {
        public IErrorDiffusion dither;
        public IPixelTransform transform;
        public BufferedImage imageToDither;
        public boolean includeAlpha = false;
        public boolean cancel = false;
    }

    public static Color[] getImageColors(BufferedImage image)
    {
        Color[] buff = new Color[image.getWidth() * image.getHeight()];

        int[] rgbs = new int[image.getWidth() * image.getHeight()];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgbs, 0, image.getWidth());

        for (int i = 0; i < rgbs.length; ++i)
        {
            buff[i] = ColorUtil.fromARGB(rgbs[i]);
        }

        return buff;
    }

    public static int[] getImageColorsInt(BufferedImage image)
    {
        int[] rgbs = new int[image.getWidth() * image.getHeight()];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgbs, 0, image.getWidth());

        return rgbs;
    }

    public static void setPixels(Color[] colors, BufferedImage buff)
    {
        int[] rgbs = new int[colors.length];

        for (int i = 0; i < rgbs.length; ++i)
        {
            rgbs[i] = ColorUtil.toARGB(colors[i]);
        }

        buff.setRGB(0, 0, buff.getWidth(), buff.getHeight(), rgbs, 0, buff.getWidth());
    }

    public static BufferedImage getImageFromArray(Color[] colors, int width, int height, int bufferedImageType)
    {
        BufferedImage buff = new BufferedImage(width, height, bufferedImageType);

        setPixels(colors, buff);

        return buff;
    }

    public static BufferedImage getImageFromArray(int[] colors, int width, int height, int bufferedImageType)
    {
        BufferedImage buff = new BufferedImage(width, height, bufferedImageType);

        buff.setRGB(0, 0, buff.getWidth(), buff.getHeight(), colors, 0, buff.getWidth());

        return buff;
    }

    public static void transformImage(WorkerData workerData)
    {
        IPixelTransform transform = workerData.transform;
        IErrorDiffusion dither = workerData.dither;
        BufferedImage buff = workerData.imageToDither;

        int width = buff.getWidth();
        int height = buff.getHeight();

        {
            Color[] pixelData = getImageColors(buff);

            if (dither != null && dither.prescan())
            {
                // perform the dithering on the source data before
                // it is transformed
                processPixels(pixelData, width, height, null, dither, workerData);
                dither = null;
            }

            if (workerData.cancel)
            {
                pixelData = null;
                return;
            }

            // scan each pixel, apply a transform the pixel
            // and then dither it
            processPixels(pixelData, width, height, transform, dither, workerData);

            if (workerData.cancel)
            {
                pixelData = null;
                return;
            }

            setPixels(pixelData, buff);
        }
    }

    public static BufferedImage getTransformImage(WorkerData workerData)
    {
        IPixelTransform transform = workerData.transform;
        IErrorDiffusion dither = workerData.dither;
        BufferedImage buff = workerData.imageToDither;

        int width = buff.getWidth();
        int height = buff.getHeight();

        {
            Color[] pixelData = getImageColors(buff);

            if (dither != null && dither.prescan())
            {
                // perform the dithering on the source data before
                // it is transformed
                processPixels(pixelData, width, height, null, dither, workerData);
                dither = null;
            }

            if (workerData.cancel)
            {
                pixelData = null;
                return null;
            }

            // scan each pixel, apply a transform the pixel
            // and then dither it
            processPixels(pixelData, width, height, transform, dither, workerData);

            if (workerData.cancel)
            {
                pixelData = null;
                return null;
            }

            int transformType = BufferedImage.TYPE_INT_RGB;

            return getImageFromArray(pixelData, width, height, transformType);
        }
    }

    public static BufferedImage getTransformImageInt(WorkerData workerData)
    {
        IPixelTransform transform = workerData.transform;
        IErrorDiffusion dither = workerData.dither;
        BufferedImage buff = workerData.imageToDither;

        int width = buff.getWidth();
        int height = buff.getHeight();

        {
            int[] pixelData = getImageColorsInt(buff);

            if (dither != null && dither.prescan())
            {
                // perform the dithering on the source data before
                // it is transformed
                processPixels(pixelData, width, height, null, dither, workerData);
                dither = null;
            }

            if (workerData.cancel)
            {
                pixelData = null;
                return null;
            }

            // scan each pixel, apply a transform the pixel
            // and then dither it
            processPixels(pixelData, width, height, transform, dither, workerData);

            if (workerData.cancel)
            {
                pixelData = null;
                return null;
            }

            int transformType= BufferedImage.TYPE_INT_RGB;

            return getImageFromArray(pixelData, width, height, transformType);
        }
    }

    public static void processPixels(Color[] pixelData, int width, int height, IPixelTransform pixelTransform,
            IErrorDiffusion dither, WorkerData bw)
    {
        Color current;
        Color transformed;
        int index = 0;

        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
            {
                if (bw != null && bw.cancel)
                {
                    return;
                }

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
                if (dither != null)
                {
                    dither.diffuse(pixelData, current, transformed, col, row, width, height);
                }
            }
    }

    public static void processPixels(int[] pixelData, int width, int height, IPixelTransform pixelTransform,
            IErrorDiffusion dither, WorkerData bw)
    {
        int current;
        int transformed;
        int index = 0;

        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
            {
                if (bw != null && bw.cancel)
                {
                    Logger.debug("Dither canceled");
                    return;
                }

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
                if (dither != null)
                {
                    dither.diffuse(pixelData, current, transformed, col, row, width, height);
                }
            }
    }
}
