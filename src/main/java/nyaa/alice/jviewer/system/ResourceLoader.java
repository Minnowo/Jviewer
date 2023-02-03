package nyaa.alice.jviewer.system;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import nyaa.alice.jviewer.data.logging.WrappedLogger;
import nyaa.alice.jviewer.drawing.imaging.ImageUtil;

public class ResourceLoader
{
    public static final ResourceLoader INSTANCE = new ResourceLoader();

    public static Image loadIconResource(final String RESOURCE_PATH)
    {
        try (InputStream in = INSTANCE.getClass().getResourceAsStream(RESOURCE_PATH))
        {
            if (in == null)
            {
                WrappedLogger.warning("could not load resource: " + RESOURCE_PATH);
                return null;
            }

            BufferedImage buff = ImageIO.read(in);

            if (buff == null)
            {
                WrappedLogger.warning("could not load image from resource: " + RESOURCE_PATH);
                return null;
            }

            return ImageUtil.createOptimalImageFrom(buff);
        }
        catch (IOException e)
        {
            WrappedLogger.warning(String.format("Error loading resource %s", RESOURCE_PATH), e);
        }

        return null;
    }

}
