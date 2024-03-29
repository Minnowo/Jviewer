package nyaa.alice.jviewer.drawing.imaging.enums;

public interface ImageFormat
{
    public static final byte UNKNOWN = -1;

    public static final byte PNG = 0;

    public static final byte JPG = 1;

    public static final byte BMP = 2;

    public static final byte TIFF = 3;

    public static final byte GIF = 4;

    public static final byte ICO = 5;

    public static final byte CUR = 6;

    /*
     * requires imagemagick
     */
    public static final byte WEBP = 7;

    public static final byte PSD = 8;

    public static final byte JXL = 9;

    public static final byte QOI = 10;

    public static interface HEIF_BRAND
    {
        public static final byte UNKNOWN_BRAND = 13;
        public static final byte HEIC = 14; // the usual HEIF images
        public static final byte HEIX = 15; // 10bit images, or anything that uses h265 with range extension
        public static final byte HEVC = 16; // brand for image sequences
        public static final byte HEVX = 17; // brand for image sequences
        public static final byte HEIM = 18; // multiview
        public static final byte HEIS = 19; // scalable
        public static final byte HEVM = 20; // multiview sequence
        public static final byte HEVS = 21; // scalable sequence
        public static final byte MIF1 = 22; // image, any coding algorithm
        public static final byte MSF1 = 23; // sequence, any coding algorithm
        public static final byte AVIF = 24;
        public static final byte AVIS = 25;
    };

    public static interface UNOFFICIAL_SUPPORT
    {
        // .kra or krita file format, simple handling via zip extraction
        public static final byte KRA = 50;
    }

    public static boolean hasUnofficalSupport(byte format)
    {
        switch (format)
        {
        case UNOFFICIAL_SUPPORT.KRA:
            return true;
        }
        return false;
    }

    public static boolean hasNativeSupport(byte format)
    {
        switch (format)
        {
        case PNG:
        case JPG:
        case BMP:
        case GIF:
        case TIFF:
            return true;
        }

        return false;
    }

    public static String getFileExtension(byte format)
    {
        switch (format)
        {
        default:
            return "";
        case PNG:
            return "png";
        case BMP:
            return "bmp";
        case JPG:
            return "jpg";
        case GIF:
            return "gif";
        case TIFF:
            return "tiff";
        case WEBP:
            return "webp";
        case PSD:
            return "psd";
        case CUR:
            return "cur";
        case ICO:
            return "ico";

        case JXL:
            return "jxl";

        case HEIF_BRAND.AVIF:
        case HEIF_BRAND.AVIS:
            return "avif";

        case HEIF_BRAND.HEIC:
        case HEIF_BRAND.HEIX:
        case HEIF_BRAND.HEIM:
        case HEIF_BRAND.HEIS:
        case HEIF_BRAND.HEVC:
        case HEIF_BRAND.HEVX:
        case HEIF_BRAND.HEVM:
        case HEIF_BRAND.HEVS:
            return "heic";

        case HEIF_BRAND.MIF1:
        case HEIF_BRAND.MSF1:
            return "heif";

        case QOI:
            return "qoi";

        case UNOFFICIAL_SUPPORT.KRA:
            return "kra";
        }
    }

    public static String getMimeType(byte format)
    {
        switch (format)
        {
        default:
            return "application/unknown";
        case PNG:
            return "image/png";
        case BMP:
            return "image/bmp";
        case JPG:
            return "image/jpg";
        case GIF:
            return "image/gif";
        case TIFF:
            return "image/tiff";
        case WEBP:
            return "image/webp";
        case PSD:
            return "image/psd";
        case CUR:
            return "image/cursor";
        case ICO:
            return "image/ico";

        case JXL:
            return "image/jxl";

        case HEIF_BRAND.UNKNOWN_BRAND:
            return "image/heic-unknown";

        case HEIF_BRAND.HEIC:
        case HEIF_BRAND.HEIX:
        case HEIF_BRAND.HEIM:
        case HEIF_BRAND.HEIS:
            return "image/heic";

        case HEIF_BRAND.MIF1:
            return "image/heif";

        case HEIF_BRAND.HEVC:
        case HEIF_BRAND.HEVX:
        case HEIF_BRAND.HEVM:
        case HEIF_BRAND.HEVS:
            return "image/heic-sequence";

        case HEIF_BRAND.MSF1:
            return "image/heif-sequence";

        case HEIF_BRAND.AVIF:
            return "image/avif";

        case HEIF_BRAND.AVIS:
            return "image/avif-sequence";

        case QOI:
            return "image/qoi";

        case UNOFFICIAL_SUPPORT.KRA:
            return "image/krita-document";
        }
    }

    public static byte getFromFileExtension(String ext)
    {
        if (ext.startsWith("."))
            ext = ext.substring(1);

        // krita usually puts ~ at the end if the file was overwritten with a newer copy
        // i'm assuming like 99% of file formats don't have ~ so i'm gonna remove it
        // here
        // and assume krita or something else put it there for this reason
        if (ext.endsWith("~"))
            ext = ext.substring(0, ext.length() - 1);

        switch (ext.toLowerCase())
        {
        default:
            return UNKNOWN;

        case "png":
            return PNG;

        case "bmp":
            return BMP;

        case "tiff":
            return TIFF;

        case "gif":
            return GIF;

        case "ico":
            return ICO;
        case "cur":
            return CUR;

        case "jpg":
        case "jpe":
        case "jpeg":
        case "jfif":
            return JPG;

        case "webp":
            return WEBP;

        case "psd":
            return PSD;

        case "jxl":
            return JXL;

        case "avif":
            return HEIF_BRAND.AVIF;

        case "heif":
            return HEIF_BRAND.MIF1;
        case "heic":
            return HEIF_BRAND.HEIC;

        case "qoi":
            return QOI;

        case "kra":
            return UNOFFICIAL_SUPPORT.KRA;
        }
    }
}
