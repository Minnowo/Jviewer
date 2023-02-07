package nyaa.alice.jviewer.ui.events;

import nyaa.alice.jviewer.drawing.imaging.dithering.IErrorDiffusion;
import nyaa.alice.jviewer.drawing.imaging.dithering.IPixelTransform;

public interface DitherPanelListener
{
    /**
     * Previews the dither
     */
    public void previewDither(IPixelTransform transform, IErrorDiffusion dither);
    
    /**
     * Applies the preview dither
     */
    public void applyDither();

    /**
     * Discards the current dither preview
     */
    public void cancelDither();
    
    /**
     * Cancels the dither operation
     */
    public void discardDither();
}
