package nyaa.alice.jviewer.system;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import nyaa.alice.jviewer.data.logging.WrappedLogger;

public class OS
{
    public static interface OSType
    {
        public static final byte INVALID = -1;
        public static final byte Windows = 0;
        public static final byte MacOS = 1;
        public static final byte Linux = 2;
        public static final byte Other = 3;

        public static String getOSName(byte ostype)
        {
            switch (getOperatingSystemType())
            {
            case OSType.Windows:
                return "Windows";
            case OSType.Linux:
                return "Linux";
            case OSType.MacOS:
                return "MacOS";
            case OSType.Other:
                return "Other";

            default:
            case OSType.INVALID:
                return "Invalid";
            }
        }
    }

    // cached result of OS detection
    protected static byte detectedOS = OSType.INVALID;

    /**
     * detect the operating system from the os.name System property and cache the
     * result
     * 
     * @returns - the operating system detected
     */
    public static byte getOperatingSystemType()
    {
        if (detectedOS == OSType.INVALID)
        {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0))
            {
                detectedOS = OSType.MacOS;
            }
            else if (OS.indexOf("win") >= 0)
            {
                detectedOS = OSType.Windows;
            }
            else if (OS.indexOf("nux") >= 0)
            {
                detectedOS = OSType.Linux;
            }
            else
            {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }

    public static void openFileExplorerToPath(File path)
    {
        if (path == null || !path.exists())
            return;

        try
        {
            switch (getOperatingSystemType())
            {
            case OSType.Windows:

                try
                {
                    if (path.isFile())
                    {
                        Runtime.getRuntime().exec("explorer.exe /select," + path.getAbsolutePath());
                        return;
                    }
                }
                catch (IOException e)
                {
                }

            default:

                if (path.isFile())
                {
                    Desktop.getDesktop().open(path.getParentFile());
                }
                else
                {
                    Desktop.getDesktop().open(path);
                }

                return;
            }
        }
        catch (IOException e)
        {
            WrappedLogger.warning(String.format("Failed to open %s in a file explorer", path), e);
        }
    }
}
