package nyaa.alice.jviewer.system;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

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
                Logger.warn("Could not load resource {}", RESOURCE_PATH);
                return null;
            }

            BufferedImage buff = ImageIO.read(in);

            if (buff == null)
            {
                Logger.warn("Could not load image from resource {}", RESOURCE_PATH);
                return null;
            }

            return ImageUtil.createOptimalImageFrom(buff);
        }
        catch (IOException e)
        {
            Logger.warn("Error while loading resource {}", RESOURCE_PATH);
        }

        return null;
    }

}
