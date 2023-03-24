package nyaa.alice.jviewer.drawing.imaging;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.tinylog.Logger;

import nyaa.alice.jviewer.drawing.imaging.enums.ImageFormat;

public class ImageDetector
{
    public static final int MAX_HEADER_LENGTH = 12;

    public static final byte[] BMP_BYTE_IDENTIFIER = new byte[] { 0x42, 0x4D };

    public static final byte[] ICO_BYTE_IDENTIFIER = new byte[] { 0x00, 0x00, 0x01, 0x00 };

    public static final byte[] CUR_BYTE_IDENTIFIER = new byte[] { 0x00, 0x00, 0x02, 0x00 };

    public static final byte[] JPEG_BYTE_IDENTIFIER = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };

    // https://github.com/libjxl/libjxl/blob/main/lib/jxl/decode.cc#L92
    public static final byte[] JXL_BYTE_IDENTIFIER_1 = new byte[] { (byte) 0xFF, (byte) 0x0A };

    // https://github.com/libjxl/libjxl/blob/main/lib/jxl/decode.cc#L105
    public static final byte[] JXL_BYTE_IDENTIFIER_2 = new byte[] { 0x00, 0x00, 0x00, 0xC, 0x4A, 0x58, 0x4C, 0x20, 0xD,
            0xA, (byte) 0x87, 0xA };

    public static final byte[] PNG_BYTE_IDENTIFIER = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A,
            0x0A };

    public static final byte[] TIFF_BYTE_IDENTIFIER_LE = new byte[] { 0x49, 0x49, 0x2A, 0x00 };

    public static final byte[] TIFF_BYTE_IDENTIFIER_BE = new byte[] { 0x4D, 0x4D, 0x00, 0x2A };

    public static final byte[] WEBP_BYTE_IDENTIFIER = new byte[] { 0x52, 0x49, 0x46, 0x46 };

    public static final byte[] GIF_BYTE_IDENTIFIER_1 = new byte[] { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61 };

    public static final byte[] GIF_BYTE_IDENTIFIER_2 = new byte[] { 0x47, 0x49, 0x46, 0x38, 0x37, 0x61 };

    public static final byte[] PSD_BYTE_IDENTIFIER = new byte[] { 0x38, 0x42, 0x50, 0x53 };

    // https://qoiformat.org/qoi-specification.pdf
    public static final byte[] QOI_BYTE_IDENTIFIER = new byte[] { 0x71, 0x6F, 0x69, 0x66 };

    // this comes after 4 bytes stating the length
    // see https://nokiatech.github.io/heif/technical.html
    public static final byte[] HEIF_FTYP_IDENTIFIER = new byte[] { 0x66, 0x74, 0x79, 0x70 };

    static class ImageFormatHeader
    {
        byte[] header;
        byte imageFormat;
        int offset;

        public ImageFormatHeader(byte[] h, byte i, int offset)
        {
            this.header = h;
            this.imageFormat = i;
            this.offset = offset;
        }
    }

    public static final ImageFormatHeader[] HEADER_MAP = new ImageFormatHeader[] {
            new ImageFormatHeader(JPEG_BYTE_IDENTIFIER, ImageFormat.JPG, 0),
            new ImageFormatHeader(PNG_BYTE_IDENTIFIER, ImageFormat.PNG, 0),
            new ImageFormatHeader(GIF_BYTE_IDENTIFIER_1, ImageFormat.GIF, 0),
            new ImageFormatHeader(GIF_BYTE_IDENTIFIER_2, ImageFormat.GIF, 0),
            new ImageFormatHeader(WEBP_BYTE_IDENTIFIER, ImageFormat.WEBP, 0),
            new ImageFormatHeader(TIFF_BYTE_IDENTIFIER_LE, ImageFormat.TIFF, 0),
            new ImageFormatHeader(TIFF_BYTE_IDENTIFIER_BE, ImageFormat.TIFF, 0),
            new ImageFormatHeader(PSD_BYTE_IDENTIFIER, ImageFormat.PSD, 0),
            new ImageFormatHeader(JXL_BYTE_IDENTIFIER_1, ImageFormat.JXL, 0),
            new ImageFormatHeader(JXL_BYTE_IDENTIFIER_2, ImageFormat.JXL, 0),
            new ImageFormatHeader(BMP_BYTE_IDENTIFIER, ImageFormat.BMP, 0),
            new ImageFormatHeader(QOI_BYTE_IDENTIFIER, ImageFormat.QOI, 0),
            new ImageFormatHeader(ICO_BYTE_IDENTIFIER, ImageFormat.ICO, 0),
            new ImageFormatHeader(CUR_BYTE_IDENTIFIER, ImageFormat.CUR, 0) };

    public static boolean startsWith(byte[] thisBytes, byte[] thatBytes, int offset)
    {
        int shortest = thisBytes.length;

        if (thatBytes.length < shortest)

            shortest = thatBytes.length;

        for (int i = 0; i < shortest; i += 1)

            if (thatBytes[i] != thisBytes[i + offset])

                return false;

        return true;
    }

    public static byte heifBrand(final byte[] brand)
    {
        // most of this stuff is more or less from
        // https://github.com/strukturag/libheif/blob/master/libheif/heif.cc

        // heic
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x69, 0x63, }))
            return ImageFormat.HEIF_BRAND.HEIC;

        // heix
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x69, 0x78, }))
            return ImageFormat.HEIF_BRAND.HEIX;

        // hevc
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x76, 0x63, }))
            return ImageFormat.HEIF_BRAND.HEVC;

        // hevx
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x76, 0x78, }))
            return ImageFormat.HEIF_BRAND.HEVX;

        // heim
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x69, 0x6d, }))
            return ImageFormat.HEIF_BRAND.HEIM;

        // heis
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x69, 0x73, }))
            return ImageFormat.HEIF_BRAND.HEIS;

        // hevm
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x76, 0x6d, }))
            return ImageFormat.HEIF_BRAND.HEVM;

        // hevs
        if (Arrays.equals(brand, new byte[] { 0x68, 0x65, 0x76, 0x73, }))
            return ImageFormat.HEIF_BRAND.HEVS;

        // mif1
        if (Arrays.equals(brand, new byte[] { 0x6d, 0x69, 0x66, 0x31, }))
            return ImageFormat.HEIF_BRAND.MIF1;

        // msf1
        if (Arrays.equals(brand, new byte[] { 0x6d, 0x73, 0x66, 0x31, }))
            return ImageFormat.HEIF_BRAND.MSF1;

        // avif
        if (Arrays.equals(brand, new byte[] { 0x61, 0x76, 0x69, 0x66, }))
            return ImageFormat.HEIF_BRAND.AVIF;

        // avis
        if (Arrays.equals(brand, new byte[] { 0x61, 0x76, 0x69, 0x73, }))
            return ImageFormat.HEIF_BRAND.AVIS;

        return ImageFormat.HEIF_BRAND.UNKNOWN_BRAND;
    }

    public static byte getImageFormat(String path)
    {
        File f = new File(path);

        try (FileInputStream fis = new FileInputStream(f);
                BufferedInputStream bis = new BufferedInputStream(fis);
                DataInputStream is = new DataInputStream(bis))
        {

            byte[] magicBytes = new byte[MAX_HEADER_LENGTH];

            for (int i = 0; i < MAX_HEADER_LENGTH; i++)
            {
                magicBytes[i] = is.readByte();

                for (ImageFormatHeader ifh : HEADER_MAP)
                {
                    if (startsWith(magicBytes, ifh.header, ifh.offset))
                    {
                        return ifh.imageFormat;
                    }
                }

                if (i == 7 && startsWith(magicBytes, HEIF_FTYP_IDENTIFIER, 4))
                {
                    byte[] buff = new byte[4];

                    int read = is.read(buff);

                    if (read != 4)
                        break;

                    byte majorBrand = heifBrand(buff);

                    return majorBrand;

                    /*
                     * don't need minor brand detection
                     * 
                     * 
                     * // read the first 4 bytes as an int big endian order to get box length
                     * 
                     */
//                	 int boxLen = ((magicBytes[0] & 0xFF) << 24) | ((magicBytes[1] & 0xFF) << 16)
//                		        | ((magicBytes[2] & 0xFF) << 8)  | (magicBytes[3] & 0xFF);
//                	 
//                	 if(boxLen <= 12)
//                		 return majorBrand;
//                	 
//                	 byte[] minorBrands = new byte[boxLen - 12];
//                	 
//                	 read = is.read(minorBrands);
//                	 
//                	 System.out.println("minor brands length " + read);
//                	 
//                	 if(read < 4)
//                		 return majorBrand;
//                	 
//                	 byte minorBrand = ImageFormat.HEIF_BRAND.UNKNOWN_BRAND;
//                	 
//                	 read = 4 * (int)(read / 4);
//                	 
//                	 for(int j = 0; j < read; j += 4)
//                	 {
//                		 if(minorBrand != ImageFormat.HEIF_BRAND.UNKNOWN_BRAND && minorBrand != majorBrand)
//                			 return minorBrand;
//                		 
//                		 minorBrand = heifBrand(new byte[] { minorBrands[j], minorBrands[j + 1], minorBrands[j + 2], minorBrands[j + 3]});
//                		 System.out.println(minorBrand);
//                	 }
//                	 
//                	 return majorBrand;

                }
            }
        }
        catch (FileNotFoundException e)
        {
            return ImageFormat.UNKNOWN;
        }
        catch (IOException e)
        {
            return ImageFormat.UNKNOWN;
        }

        Logger.warn("Unknown file format for: {}", path);

        return ImageFormat.UNKNOWN;
    }
}
