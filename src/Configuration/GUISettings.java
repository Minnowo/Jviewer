package Configuration;

import java.util.logging.Level;

import javax.swing.UIManager;

public class GUISettings 
{
	public static final boolean DEBUG_MODE = true;
	
	public static int MAIN_SPLIT_PANE_DIVISOR_SIZE = (Integer) UIManager.get("SplitPane.dividerSize");
	
	public static double MAIN_ZOOM_SPINNER_CHANGE_VALUE = 15d;
	
	public static boolean WRAP_TABS = false;
	
	public static String MAIN_WINDOW_TITLE = "Jviewer";
	
	public static Level LOG_LEVEL = Level.ALL;
	
	public static boolean CENTER_IMAGE_ON_RESIZE = true;
	
	public static String IMAGE_FILTER_NO_MAGICK =String.join(",", 
			"*.png", "*.jpg", "*.jpeg", "*.jpe", "*.jfif", "*.gif", "*.bmp", "*.tif", "*.tiff");
	
	public static String IMAGE_FILTER = String.join(",", 
			IMAGE_FILTER_NO_MAGICK,
			
			// https://imagemagick.org/script/formats.php
			"*.jxl", "*.avif", "*.heic", "*.heif", "*.webp", "*.psd",
			
						
			// AAI | RW | AAI Dune image 
			"*.aai" ,
			
			// APNG | RW | Animated Portable Network Graphics 
			"*.apng",
			
			// ART | RW | PFS: 1st Publisher 
			"*.art",
			
			// ARW | R | Sony Digital Camera Alpha Raw Image Format 
			"*.arw" ,
			
			// AVI | R | Microsoft Audio/Visual Interleaved 
			"*.avi",
			
			// AVIF | RW | Format derived from the keyframes of AV1 video 
			"*.avif",
			
			// AVS | RW | AVS X image 
			"*.avs" ,
			
			// BAYER | RW | Raw mosaiced samples 
			".bayer",
			
			// BPG | RW | Better Portable Graphics 
			"*.bpg" ,
			
			// BMP, BMP2, BMP3 | RW | Microsoft Windows bitmap 
			"*.bmp,*.bmp2,*.bmp3",
			
			// BRF | W | Braille Ready Format 
			// WRITE ONLY
			
			// CALS | R | Continuous Acquisition and Life-cycle Support Type 1 image 
			"*.cals",
			
			// CIN | RW | Kodak Cineon Image Format 
			"*.cin",
			
			// CIP | W | Cisco IP phone image format 
			// WRITE ONLY
			
			// CMYK | RW | Raw cyan, magenta, yellow, and black samples 
			"*.cmyk",
			
			// CMYKA | RW | Raw cyan, magenta, yellow, black, and alpha samples 
			"*.cmyka",
			
			// CR2 | R | Canon Digital Camera Raw Image Format 
			"*.cr2",
			
			// CRW | R | Canon Digital Camera Raw Image Format 
			"*.crw",
			
			// CUBE | R | Cube Color lookup table converted to a HALD image 
			"*.cube",
			
			// CUR | R | Microsoft Cursor Icon 
			"*.cur",
			
			// CUT | R | DR Halo 
			"*.cut",
			
			// DCM | R | Digital Imaging and Communications in Medicine (DICOM) image 
			"*.dcm",
			
			// DCR | R | Kodak Digital Camera Raw Image File 
			"*.dcr",
			
			// DCX | RW | ZSoft IBM PC multi-page Paintbrush image 
			"*.dcx",
			
			// DDS | RW | Microsoft Direct Draw Surface 
			"*.dds",
			
			// DEBUG | W | Raw pixel debug file, likely only useful to the developers 
			// WRITE ONLY
			
			// DIB | RW | Microsoft Windows Device Independent Bitmap 
			"*.dib",
			
			// DJVU | R	
			"*.djvu",
			
			// DNG | R | Digital Negative 
			"*.dng",
			
			// DOT | R | Graph Visualization 
			"*.dot",
			
			// DPX | RW | SMPTE Digital Moving Picture Exchange 2.0 (SMPTE 268M-2003) 
			"*.dpx",
			
			// EMF | R | Microsoft Enhanced Metafile (32-bit) 
			"*.emf",
			
			// EPDF | RW | Encapsulated Portable Document Format 
			"*.epdf",
			
			// EPI | RW | Adobe Encapsulated PostScript Interchange format 
			"*.epi",
			
			// EPS | RW | Adobe Encapsulated PostScript 
			// REQUIRES Ghostscript to READ.
			"*.eps",
			
			// EPS2 | W | Adobe Level II Encapsulated PostScript
			// REQUIRES Ghostscript to READ.
			"*.eps2",
			
			// EPS3 | W | Adobe Level III Encapsulated PostScript 	
			// REQUIRES Ghostscript to read.
			"*.eps3",
			
			// EPSF | RW | Adobe Encapsulated PostScript 	
			// Requires Ghostscript to read.
			"*.epsf",
			
			// EPSI | RW | Adobe Encapsulated PostScript Interchange format 	
			// Requires Ghostscript to read.
			"*.epsi",
			
			// EPT | RW | Adobe Encapsulated PostScript Interchange format with TIFF preview 	
			// Requires Ghostscript to read.
			"*.ept",
			
			// EXR | RW | High dynamic-range (HDR) file format developed by Industrial Light & Magic 	
			"*.exr",
			
			// FARBFELD | RW | Farbfeld lossless image format
			"*.farbfeld",
			
			// FAX | RW | Group 3 TIFF
			"*.fax",
			
			// FITS | RW | Flexible Image Transport System 
			"*.fits",
			
			// FL32 | RW | FilmLight floating point image format 	
			"*.fl32",
			
			// FLIF | RW | Free Lossless Image Format 	
			"*.flif",
			
			// FPX | RW | FlashPix Format 
			"*.fpx",
			
			// FTXT | RW | Read and write multispectral channels as formatted text 	
			"*.ftxt",
			
			// GIF | RW | CompuServe Graphics Interchange Format
			"*.gif",
			
			// GPLT | R | Gnuplot plot files
			// Requires gnuplot4.0.tar.Z or later.
			"*.gplt",
			
			// GRAY | RW | Raw gray samples 
			"*.gray",
			
			// GRAYA | RW | Raw gray and alpha samples 
			"*.graya",
			
			// HDR | RW | Radiance RGBE image format 
			".hdr",
			
			// HDR | RW | Radiance RGBE image format 
			"*.hdr",
			
			// HEIC | RW | Apple High efficiency Image Format
			"*.heic",
			
			// HPGL | R | HP-GL plotter language
			// Requires hp2xx-3.4.4.tar.gz
			"*.hpgl",
			
			// HRZ | RW | Slow Scan TeleVision 	
			"*.hrz",
			
			// HTML | RW | Hypertext Markup Language with a client-side image map.
			// Requires html2ps to read.
			"*.html",
			
			// ICO | R | Microsoft icon Also known as ICON.
			"*.ico,*.icon",
			
			// INFO | W | Format and characteristics of the image
			// WRITE ONLY
			
			// ISOBRL | W | ISO/TR 11548-1 BRaiLle
			// WRITE ONLY
			
			// ISOBRL6 | W | ISO/TR 11548-1 BRaiLle 6 dots 
			// WRITE ONLY
			
			// JBIG | RW | Joint Bi-level Image experts Group file interchange format. 
			// Requires jbigkit-1.6.tar.gz.
			"*.jbig",
			
			// JNG | RW | Multiple-image Network Graphics   
			// Requires libjpeg and libpng-1.0.11 or later, libpng-1.2.5 or later recommended.
			"*.jng",
			
			// JP2 | RW | JPEG-2000 JP2 File Format Syntax 
			"*.jp2",
			
			// JPT | RW | JPEG-2000 Code Stream Syntax 
			"*.jpt",
			
			// J2C | RW | JPEG-2000 Code Stream Syntax 
			"*.j2c",
			
			// J2K | RW | JPEG-2000 Code Stream Syntax 
			"*.j2k",
			
			// JPEG | RW | Joint Photographic Experts Group JFIF format 			
			// Requires jpegsrc.v8c.tar.gz.
			"*.jpeg",
			
			// JXR | RW | JPEG extended range 	
			// Requires the jxrlib delegate library. 
			"*.jxr",
			
			// JSON | W | JavaScript Object Notation, a lightweight data-interchange format 
			// WRITE ONLY
			
			// JXL | RW | JPEG XL image coding system
			// Requires the JPEG XL delegate library.
			"*.jxl",
			
			// KERNEL | W | Morphology kernel format
			// WRITE ONLY
			
			// MAN | R | Linux reference manual pages 	
			// Requires that GNU groff and Ghostcript are installed.
			"*.man",
			
			// MAT | R | MATLAB image format 	
			"*.mat",
			
			// MIFF | RW | Magick multispectral image file format
			"*.miff",
			
			// MONO | RW | Bi-level bitmap in least-significant-byte first order 	
			"*.mono",
			
			// MNG | RW | Multiple-image Network Graphics  
			// Requires libpng-1.0.11 or later, libpng-1.2.5 or later recommended. 
			"*.mng",
			
			// M2V | RW | Motion Picture Experts Group file interchange format (version 2) 	
			// Requires ffmpeg.
			"*.m2v",
			
			// MPEG | RW | Motion Picture Experts Group file interchange format (version 1)
			// Requires ffmpeg.
			"*.mpeg",
			
			// MPC | RW | Magick Pixel Cache image file format 
			"*.mpc",
			
			// MPR | RW | Magick Persistent Registry
			"*.mpr",
			
			// MRW | R | Sony (Minolta) Raw Image File 
			"*.mrw",
			
			// MSL | RW | Magick Scripting Language. 
			// requires the libxml2 delegate library.
			"*.msl",
			
			// MTV | RW | MTV Raytracing image format 
			"*.mtv",
			
			// MVG | RW | Magick Vector Graphics.
			"*.mvg",
			
			// NEF | R | Nikon Digital SLR Camera Raw Image File 
			"*.nef",
			
			// ORF | R | Olympus Digital Camera Raw Image File 
			"*.orf",
			
			// ORA | R | open exchange format for layered raster based graphics 
			"*.ora",
			
			// OTB | RW | On-the-air Bitmap 
			"*.otb",
			
			// P7 | RW | Xvs Visual Schnauzer thumbnail format 
			"*.p7",
			
			// PALM | RW | Palm pixmap 
			"*.palm",
			
			// PAM | W | Common 2-dimensional bitmap format 
			"*.pam",
			
			// PBM | RW | Portable bitmap format (black and white) 
			"*.pbm",
			
			// PCD | RW | Photo CD 
			"*.pcd",
			
			// PCDS | RW | Photo CD 
			"*.pcds",
			
			// PCL | W | HP Page Control Language 
			// WRITE ONLY
			
			// PCX | RW | ZSoft IBM PC Paintbrush file 
			"*.pcx",
			
			// PDB | RW | Palm Database ImageViewer Format 
			"*.pdb",
			
			// PDF | RW | Portable Document Format 	
			// Requires Ghostscript to read.
			"*.pdf",
			
			// PEF | R | Pentax Electronic File 
			"*.pef",
			
			// PES | R | Embrid Embroidery Format
			"*.pes",
			
			// PFA | R | Postscript Type 1 font (ASCII)
			"*.pfa",
			
			// PFB | R | Postscript Type 1 font (binary)
			"*.pfb",
			
			// PFM | RW | Portable float map format 
			"*.pfm",
			
			// PGM | RW | Portable graymap format (gray scale) 
			"*.pgm",
			
			// PHM | RW | Portable float map format 16-bit half
			"*.phm",
			
			// PICON | RW | Personal Icon 
			"*.picon",
			
			// PICT | RW | Apple Macintosh QuickDraw/PICT file 
			"*.pict",
			
			// PIX | R | Alias/Wavefront RLE image format 
			"*.pix",
			
			// PNG | RW | Portable Network Graphics
			// Requires libpng-1.0.11 or later, libpng-1.2.5 or later recommended.
			"*.png",
			
			// PNG8 | RW | Portable Network Graphics 
			"*.png8",
			
			// PNG00 | RW | Portable Network Graphics 
			"*.png00",
			
			// PNG24 | RW | Portable Network Graphics 
			"*.png24",
			
			// PNG32 | RW | Portable Network Graphics 
			"*.png32",
			
			// PNG48 | RW | Portable Network Graphics 
			"*.png48",
			
			// PNG64 | RW | Portable Network Graphics 
			"*.png64",
			
			// PNM | RW | Portable anymap 
			"*.pnm",
			
			// POCKETMOD | RW | Pocketmod personal organizer format
			"*.pocketmod",
			
			// PPM | RW | Portable pixmap format (color) 
			"*.ppm",
			
			// PS | RW | Adobe PostScript file 	
			// Requires Ghostscript to read.
			"*.ps",
			
			// PS2 | RW | Adobe Level II PostScript file 	
			// Requires Ghostscript to read.
			"*.ps2",
			
			// PS3 | RW | Adobe Level III PostScript file 	
			// Requires Ghostscript to read.
			"*.ps3",
			
			// PSB | RW | Adobe Large Document Format 
			"*.psb",
			
			// PSD | RW | Adobe Photoshop multispectral bitmap file
			"*.psd"
			,
			// PTIF | RW | Pyramid encoded TIFF 
			"*.ptif",
			
			// PWP | R | Seattle File Works multi-image file 
			"*.pwp"
			,
			// QOI | RW | Quite OK Image Format
			"*.qoi"
			,
			// RAD | R | Radiance image file 	
			// Requires that ra_ppm from the Radiance software package be installed.
			"*.rad",
			
			// RAF | R | Fuji CCD-RAW Graphic File 
			"*.raf",
			
			// RAW | RW | Raw gray samples
			"*.raw"
			,
			// RGB | RW | Raw red, green, and blue samples 
			"*.rgb"
			,
			// RGB565 | R | Raw red, green, blue pixels in the 5-6-5 format
			"*.rgb565"
			,
			// RGBA | RW | Raw red, green, blue, and alpha samples
			"*.rgba"
			,
			// RGF | RW | LEGO Mindstorms EV3 Robot Graphics File
			"*.rgf"
			,
			// RLA | R | Alias/Wavefront image file 
			"*.rla"
			,
			// RLE | R | Utah Run length encoded image file 
			"*.rle"
			,
			// SCT | R | Scitex Continuous Tone Picture 
			"*.sct"
			,
			// SFW | R | Seattle File Works image 
			"*.sfw"
			,
			// SGI | RW | Irix RGB image 
			"*.sgi"
			,
			// SHTML | W | Hypertext Markup Language client-side image map 
			"*.shtml"
			,
			// SID, MrSID | R | Multiresolution seamless image
			// Requires the mrsidgeodecode command line utility that decompresses MG2 or MG3 SID image files.
			"*.sid,*.mrsid"
			,
			// SPARSE-COLOR | W | Raw text file 
			// WRITE ONLY
			
			// STRIMG | RW | String to images and back 
			"*.string",
			
			// SUN | RW | SUN Rasterfile 
			"*.sun",
			
			// SVG | RW | Scalable Vector Graphics
			"*.svg",
			
			// TEXT | R | text file
			// Requires an explicit format specifier to read, e.g. text:README.txt.
			"*.text"
			,
			// TGA | RW | Truevision Targa image
			"*.tga",
			
			// TIFF | RW | Tagged image file multispectral format Also known as TIF. 
			// Requires tiff-v3.6.1.tar.gz or later.
			"*.tiff,*.tif",
			
			// TIM | R | PSX TIM file 
			"*.tim",
			
			// TTF | R | TrueType font file 	Requires freetype 2. 
			"*.ttf"
			,
			// TXT | RW | Multispectral raw text file
			"*.txt"
			,
			// UBRL | W | Unicode BRaiLle 
			"*.ubrl",
			
			// UBRL6 | W | Unicode BRaiLle 6 dots 
			"*.ubrl6",
			
			// UIL | W | X-Motif UIL table 
			"*.uil",
			
			// UYVY | RW | Interleaved YUV raw image
			"*.uyvy",
			
			// VICAR | RW | VICAR rasterfile format 
			"*.vicar",
			
			// VIDEO | RW | Various video formats such as APNG, AVI, MP4, WEBM, etc.
			"*.apng,*.avi,*.mp4,*.webm",
			
			// VIFF | RW | Khoros Visualization Image File Format 
			"*.viff",
			
			// WBMP | RW | Wireless bitmap 	Support for uncompressed monochrome only.
			"*wbmp"
			,
			// WDP | RW | JPEG extended range 	
			// Requires the jxrlib delegate library. Put the JxrDecApp and JxrEncApp applications in your execution path.
			"*.wdp",
			
			// WEBP | RW | Weppy image format 	Requires the WEBP delegate library.
			"*.webp",
			
			// WMF | R | Windows Metafile 	Requires libwmf. 
			"*.wmf",
			
			// WPG | R | Word Perfect Graphics File 
			"*.wpg",
			
			// X | RW | display or import an image to or from an X11 server
			
			// XBM | RW | X Windows system bitmap, black and white only
			"*.xbm",
			
			// XCF | R | GIMP image 
			"*.xcf",
			
			// XPM | RW | X Windows system pixmap 
			"*.xpm"
			,
			// XWD | RW | X Windows system window dump 
			"*.xwd"
			,
			// X3F | R | Sigma Camera RAW Picture File 
			"*.x3f"
			,
			// YAML | W | human-readable data-serialization language
			"*.yaml"
			,
			// YCbCr | RW | Raw Y, Cb, and Cr samples 
			"*.ycbcr"
			,
			// YCbCrA | RW | Raw Y, Cb, Cr, and alpha samples 
			"*.ycbcra"
			,
			// YUV | RW | CCIR 601 4:1:1 
			"*.yuv"


			);
	
	
}
