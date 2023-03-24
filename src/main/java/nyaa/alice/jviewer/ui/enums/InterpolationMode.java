package nyaa.alice.jviewer.ui.enums;

import java.awt.RenderingHints;

public interface InterpolationMode
{
    public static final byte NEAREST_NEIGHBOR = 0;

    public static final byte BILINEAR = 1;

    public static final byte BICUBIC = 2;

    public static Object getMode(int interpolationMode)
    {
        switch (interpolationMode)
        {
        default:
        case NEAREST_NEIGHBOR:
            return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

        case BILINEAR:
            return RenderingHints.VALUE_INTERPOLATION_BICUBIC;

        case BICUBIC:
            return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        }
    }
}
