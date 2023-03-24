package nyaa.alice.jviewer.system;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;
import org.tinylog.configuration.PropertiesConfigurationLoader;

import nyaa.alice.jviewer.drawing.imaging.ImageUtil;

public class ResourceLoader
{
    public static final ResourceLoader INSTANCE = new ResourceLoader();

    /**
     * This doesn't call any tinylog functions, because if that happens before a
     * config is loaded You cannot change the tiny log config
     * 
     * @param resourcePath
     * @return
     */
    public static String findResourcePathSilent(String resourcePath)
    {
        String path = resourcePath;

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of("/", "resources", resourcePath).toString();
        }

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of(".", "resources", resourcePath).toString();
        }

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of("resources", resourcePath).toString();
        }

        File f = new File(resourcePath);
        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of("/", "resources", f.getName()).toString();
        }

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of(".", "resources", f.getName()).toString();
        }

        return path;
    }

    public static String findResourcePath(String resourcePath)
    {
        String path = resourcePath;

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of("/", "resources", resourcePath).toString();
            Logger.warn("Resource {} is null, trying {}", resourcePath, path);
        }

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of(".", "resources", resourcePath).toString();
            Logger.warn("Resource {} is null, trying {}", resourcePath, path);
        }

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of("resources", resourcePath).toString();
            Logger.warn("Resource {} is null, trying {}", resourcePath, path);
        }

        File f = new File(resourcePath);
        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of("/", "resources", f.getName()).toString();
            Logger.warn("Resource {} is null, trying {}", resourcePath, path);
        }

        if (INSTANCE.getClass().getResource(path) == null)
        {
            path = Path.of(".", "resources", f.getName()).toString();
            Logger.warn("Resource {} is null, trying {}", resourcePath, path);
        }

        return path;
    }

    public static Image loadIconResource(final String RESOURCE_PATH)
    {
        String path = findResourcePath(RESOURCE_PATH);

        try (InputStream in = INSTANCE.getClass().getResourceAsStream(path))
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

    public static Properties readProperty(String resource)
    {
        String path = findResourcePath(resource);

        try (InputStream in = INSTANCE.getClass().getResourceAsStream(path))
        {
            if (in == null)
            {
                Logger.warn("Could not load resource {}", resource);
                return null;
            }

            Properties properties = new Properties();
            properties.load(in);

            return properties;
        }
        catch (IOException e)
        {
            Logger.warn("Error while loading resource {}", resource);
        }

        return null;
    }

    /**
     * CRITICAL that no logging with tinylog happens BEFORE THIS, otherwise you
     * cannot change the config
     */
    public static void loadTinyLogConfig()
    {
        Configuration.set("writer1", "file");
        Configuration.set("writer1.file", Path.of(ResourcePaths.LOGS_DIR_PATH.toString(), "logs.txt").toString());
        Configuration.set("writer1.format", "[{date: yyyy-MM-dd HH:mm:ss.SSS}] [{level}] {message}");

        Configuration.set("writer2", "console");
        Configuration.set("writer2.format", "[{date: yyyy-MM-dd HH:mm:ss.SSS}] [{level}] {message}");
        Logger.warn("Could not load tinylog.properties from resource, setting default values");

//        
//        String path = findResourcePathSilent(ResourcePaths.TINY_LOG_CONFIG_PATH);
//        
//        Properties p = new Properties();
//        
//        try (InputStream in = INSTANCE.getClass().getResourceAsStream(path))
//        {
//            if(in != null)
//            {
//                p.load(in);
//            }
//            else 
//            {
//                p = null;
//            }
//        }
//        catch (IOException e)
//        {        
//            p = null;
//        }
//
//        
//        if (p == null)
//        {
//            try
//            {
//                Configuration.set("writer1", "file");
//                Configuration.set("writer1.file", Path.of(ResourcePaths.LOGS_DIR_PATH.toString(), "logs.txt").toString());
//                Configuration.set("writer1.format", "[{date: yyyy-MM-dd HH:mm:ss.SSS}] [{level}] {message}");
//
//                Configuration.set("writer2", "console");
//                Configuration.set("writer2.format", "[{date: yyyy-MM-dd HH:mm:ss.SSS}] [{level}] {message}");
//                Logger.warn("Could not load tinylog.properties from resource, setting default values");
//            }
//            catch (UnsupportedOperationException e)
//            {
//                Logger.warn("Tried to load default log configuration but there a config was already loaded");
//            }
//
//            return;
//        }
//        
//
//        System.out.println( INSTANCE.getClass().getResource(path).getPath());
//        System.out.println( INSTANCE.getClass().getResource(path).getFile());
//        System.out.println( INSTANCE.getClass().getResource(path).toString());
//
//        // this just does not work in a jar file
//        System.setProperty("tinylog.configuration", path);
//
//        
//        try
//        {
//            System.out.println("Loading properties manually");
//            
//            // this fucks stuff up because it doesn't format anything, and idk how to make it
//            p.forEach((x, y) -> 
//            {
//                if (x instanceof String && y instanceof String)
//                {
//                    Configuration.set((String) x, (String) y);
//                }
//            });
//        }
//        catch (UnsupportedOperationException e)
//        {
//            Logger.warn("Tried to load default log configuration but there a config was already loaded");
//        }
    }
}
