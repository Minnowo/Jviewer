package nyaa.alice.jviewer.ui.enums;

import java.awt.RenderingHints;

public interface RenderQuality
{
    public static final byte DEFAULT = 0;

    public static final byte FAST = 1;

    public static final byte QUALITY = 2;

    public static Object getMode(int renderQualityMode)
    {
        switch (renderQualityMode)
        {
        default:
        case DEFAULT:
            return RenderingHints.VALUE_RENDER_DEFAULT;

        case FAST:
            return RenderingHints.VALUE_RENDER_SPEED;

        case QUALITY:
            return RenderingHints.VALUE_RENDER_QUALITY;
        }
    }
}
