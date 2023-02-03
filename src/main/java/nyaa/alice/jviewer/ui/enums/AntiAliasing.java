package nyaa.alice.jviewer.ui.enums;

import java.awt.RenderingHints;

public interface AntiAliasing
{
    /**
     * Antialiasing hint value -- rendering is done with a defaultantialiasing mode
     * chosen by the implementation.
     */
    public static final byte DEFAULT = 0;

    /**
     * Antialiasing hint value -- rendering is done without antialiasing
     */
    public static final byte DISABLED = 1;

    /**
     * Antialiasing hint value -- rendering is done with antialiasing.
     */
    public static final byte ENABLED = 2;

    public static Object getMode(int antiAliasingMode)
    {
        switch (antiAliasingMode)
        {
        default:
        case DEFAULT:
            return RenderingHints.VALUE_ANTIALIAS_DEFAULT;

        case DISABLED:
            return RenderingHints.VALUE_ANTIALIAS_OFF;

        case ENABLED:
            return RenderingHints.VALUE_ANTIALIAS_ON;
        }
    }
}
