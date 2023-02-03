package nyaa.alice.jviewer.data;

import java.io.File;

public class PathUtil
{

    public static File getTempFilePath(String startingWidth, String endingWith, File directory)
    {
        File f;

        do
        {
            f = new File(String.format("%s%s%s.%d.%s", directory.getAbsolutePath(), File.separatorChar, startingWidth,
                    System.currentTimeMillis(), endingWith));
        }
        while (f.exists());

        return f;
    }

}
