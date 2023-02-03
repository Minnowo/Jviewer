package nyaa.alice.jviewer.system;

public class ImageMagickSettings
{
    public interface ImageDecodeFormat
    {
        public static final byte BMP = 0;

        public static final byte PNG = 1;

        public static final byte JPG = 2;

        public static final byte TIFF = 3;

        public static final byte GIF = 4;
    }

    public static boolean useImageMagick = true;

    /**
     * the {@link ImageDecodeFormat} which determines what decoded image type magick
     * will return
     */
    public static byte Image_Decode_Format = ImageDecodeFormat.BMP;

    /**
     * the decode formats java can read
     */
    public static final String[] STDOUT_DECODE_FORMATS = { "bmp", "png", "jpg", "tiff", "gif" };

    public static String getImageDecodeFormat()
    {
        switch (Image_Decode_Format)
        {
        default:
        case ImageDecodeFormat.BMP:
            return STDOUT_DECODE_FORMATS[ImageDecodeFormat.BMP];

        case ImageDecodeFormat.PNG:
            return STDOUT_DECODE_FORMATS[ImageDecodeFormat.PNG];

        case ImageDecodeFormat.JPG:
            return STDOUT_DECODE_FORMATS[ImageDecodeFormat.JPG];

        case ImageDecodeFormat.TIFF:
            return STDOUT_DECODE_FORMATS[ImageDecodeFormat.TIFF];

        case ImageDecodeFormat.GIF:
            return STDOUT_DECODE_FORMATS[ImageDecodeFormat.GIF];
        }
    }

    public static boolean readRequiresMergeLayers(byte format)
    {
        /*
         * going to not flatten anything for now will make it up to the user later
         * because using magick.flatten can be really slow and resource intensive
         * without flatten we just take the first image, which is good for most cases
         * stuff like gimp files only returns the first layer and would probably want to
         * be flattened
         */
//		switch (format) 
//		{
//			case ImageFormat.ICO:
//			case ImageFormat.CUR:
//			
//			
//			// psd files have flattened image when doing [0]
//			// so no need to merge layers
//			
//			case ImageFormat.PSD:
//				return true;
//			
//		}

        return false;
    }
}
