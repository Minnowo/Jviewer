package nyaa.alice.jviewer.drawing.imaging.dithering;

import java.awt.Color;
import java.util.HashMap;

public class Transforms
{

    public static class MonochromePixelTransform implements IPixelTransform
    {
        private final Color _black;

        private final Color _white;

        private final int _threshold;

        public MonochromePixelTransform(Integer threshold)
        {
            _threshold = DitherHelper.clamp(threshold);
            _black = new Color(0, 0, 0);
            _white = new Color(255, 255, 255);
        }

        public Color Transform(Color pixel)
        {
            int gray = DitherHelper.clamp((int)(0.299 * pixel.getRed() + 0.587 * pixel.getGreen() + 0.114 * pixel.getBlue()));

            /*
             * I'm leaving the alpha channel untouched instead of making it fully opaque
             * otherwise the transparent areas become fully black, and I was getting annoyed
             * by this when testing images with large swathes of transparency!
             */

            return gray < _threshold ? _black : _white;
        }

    }

    public static class SimpleIndexedPalettePixelTransform implements IPixelTransform
    {
        private final Color[] _map;

        private final HashMap<Color, Integer> _mapLookup;

        public SimpleIndexedPalettePixelTransform(Color[] map)
        {
            _map = map;
            _mapLookup = new HashMap<Color, Integer>();
        }

        private int FindNearestColor(Color current)
        {
            int shortestDistance = Integer.MAX_VALUE;
            int index = 0;

            for (int i = 0; i < _map.length; i++)
            {
                Color match = _map[i];
                int distance = this.GetDistance(current, match);

                if (distance < shortestDistance)
                {
                    index = i;
                    shortestDistance = distance;
                }
            }

            return index;
        }

        private int GetDistance(Color current, Color match)
        {
            int redDifference = current.getRed() - match.getRed();
            int greenDifference = current.getGreen() - match.getGreen();
            int blueDifference = current.getBlue() - match.getBlue();

            return redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference;
        }

        public Color Transform(Color pixel)
        {
            int index;

            if (!_mapLookup.containsKey(pixel))
            {
                index = this.FindNearestColor(pixel);

                _mapLookup.put(pixel, index);
            }
            else
            {
                index = _mapLookup.get(pixel);
            }

            return _map[index];
        }

    }

    public static class SimpleIndexedPalettePixelTransform8 extends SimpleIndexedPalettePixelTransform
    {
      public SimpleIndexedPalettePixelTransform8()
      { 
          super(new Color[] {
                  new Color(0, 0, 0),
                  new Color(255, 0, 0),
                  new Color(0, 255, 0),
                  new Color(0, 0, 255),
                  new Color(255, 255, 0),
                  new Color(255, 0, 255),
                  new Color(0, 255, 255),
                  new Color(255, 255, 255)
          });
      }

    }
    
    public static class SimpleIndexedPalettePixelTransform16 extends SimpleIndexedPalettePixelTransform
    {
      public SimpleIndexedPalettePixelTransform16()
      { 
          super(new Color[] {
                  new Color(0, 0, 0),
                  new Color(128, 0, 0),
                  new Color(0, 128, 0),
                  new Color(128, 128, 0),
                  new Color(0, 0, 128),
                  new Color(128, 0, 128),
                  new Color(0, 128, 128),
                  new Color(128, 128, 128),
                  new Color(192, 192, 192),
                  new Color(255, 0, 0),
                  new Color(0, 255, 0),
                  new Color(255, 255, 0),
                  new Color(0, 0, 255),
                  new Color(255, 0, 255),
                  new Color(0, 255, 255),
                  new Color(255, 255, 255)
          });
      }

    }
    
    
    public static class SimpleIndexedPalettePixelTransform256 extends SimpleIndexedPalettePixelTransform
    {
      public SimpleIndexedPalettePixelTransform256()
      { 
          super(new Color[] {
                  new Color(0, 0, 0),
                  new Color(128, 0, 0),
                  new Color(0, 128, 0),
                  new Color(128, 128, 0),
                  new Color(0, 0, 128),
                  new Color(128, 0, 128),
                  new Color(0, 128, 128),
                  new Color(192, 192, 192),
                  new Color(128, 128, 128),
                  new Color(255, 0, 0),
                  new Color(0, 255, 0),
                  new Color(255, 255, 0),
                  new Color(0, 0, 255),
                  new Color(255, 0, 255),
                  new Color(0, 255, 255),
                  new Color(255, 255, 255),
                  new Color(25, 25, 25),
                  new Color(51, 51, 51),
                  new Color(76, 76, 76),
                  new Color(90, 90, 90),
                  new Color(102, 102, 102),
                  new Color(115, 115, 115),
                  new Color(128, 128, 128),
                  new Color(141, 141, 141),
                  new Color(153, 153, 153),
                  new Color(166, 166, 166),
                  new Color(178, 178, 178),
                  new Color(192, 192, 192),
                  new Color(204, 204, 204),
                  new Color(218, 218, 218),
                  new Color(230, 230, 230),
                  new Color(243, 243, 243),
                  new Color(63, 0, 0),
                  new Color(92, 0, 0),
                  new Color(120, 0, 0),
                  new Color(148, 0, 0),
                  new Color(177, 0, 0),
                  new Color(205, 0, 0),
                  new Color(233, 0, 0),
                  new Color(254, 7, 7),
                  new Color(255, 35, 35),
                  new Color(255, 63, 63),
                  new Color(255, 92, 92),
                  new Color(255, 120, 120),
                  new Color(254, 148, 148),
                  new Color(255, 177, 177),
                  new Color(254, 205, 205),
                  new Color(255, 233, 233),
                  new Color(63, 23, 0),
                  new Color(92, 34, 0),
                  new Color(120, 45, 0),
                  new Color(148, 55, 0),
                  new Color(177, 66, 0),
                  new Color(205, 77, 0),
                  new Color(233, 87, 0),
                  new Color(254, 100, 7),
                  new Color(255, 117, 35),
                  new Color(255, 135, 63),
                  new Color(255, 153, 92),
                  new Color(255, 170, 120),
                  new Color(254, 188, 148),
                  new Color(255, 206, 177),
                  new Color(254, 224, 205),
                  new Color(255, 241, 233),
                  new Color(63, 47, 0),
                  new Color(92, 69, 0),
                  new Color(120, 90, 0),
                  new Color(148, 111, 0),
                  new Color(177, 132, 0),
                  new Color(205, 154, 0),
                  new Color(233, 175, 0),
                  new Color(254, 193, 7),
                  new Color(255, 200, 35),
                  new Color(255, 207, 63),
                  new Color(255, 214, 92),
                  new Color(255, 221, 120),
                  new Color(254, 228, 148),
                  new Color(255, 235, 177),
                  new Color(254, 242, 205),
                  new Color(255, 249, 233),
                  new Color(55, 63, 0),
                  new Color(80, 92, 0),
                  new Color(105, 120, 0),
                  new Color(130, 148, 0),
                  new Color(154, 177, 0),
                  new Color(179, 205, 0),
                  new Color(204, 233, 0),
                  new Color(224, 254, 7),
                  new Color(227, 255, 35),
                  new Color(231, 255, 63),
                  new Color(234, 255, 92),
                  new Color(238, 255, 120),
                  new Color(241, 254, 148),
                  new Color(245, 255, 177),
                  new Color(248, 254, 205),
                  new Color(252, 255, 233),
                  new Color(31, 63, 0),
                  new Color(46, 92, 0),
                  new Color(60, 120, 0),
                  new Color(74, 148, 0),
                  new Color(88, 177, 0),
                  new Color(102, 205, 0),
                  new Color(116, 233, 0),
                  new Color(131, 254, 7),
                  new Color(145, 255, 35),
                  new Color(159, 255, 63),
                  new Color(173, 255, 92),
                  new Color(187, 255, 120),
                  new Color(201, 254, 148),
                  new Color(216, 255, 177),
                  new Color(230, 254, 205),
                  new Color(244, 255, 233),
                  new Color(7, 63, 0),
                  new Color(11, 92, 0),
                  new Color(15, 120, 0),
                  new Color(18, 148, 0),
                  new Color(22, 177, 0),
                  new Color(25, 205, 0),
                  new Color(29, 233, 0),
                  new Color(38, 254, 7),
                  new Color(62, 255, 35),
                  new Color(87, 255, 63),
                  new Color(112, 255, 92),
                  new Color(137, 255, 120),
                  new Color(162, 254, 148),
                  new Color(186, 255, 177),
                  new Color(211, 254, 205),
                  new Color(236, 255, 233),
                  new Color(0, 63, 15),
                  new Color(0, 92, 23),
                  new Color(0, 120, 30),
                  new Color(0, 148, 37),
                  new Color(0, 177, 44),
                  new Color(0, 205, 51),
                  new Color(0, 233, 58),
                  new Color(7, 254, 69),
                  new Color(35, 255, 90),
                  new Color(63, 255, 111),
                  new Color(92, 255, 132),
                  new Color(120, 255, 154),
                  new Color(148, 254, 175),
                  new Color(177, 255, 196),
                  new Color(205, 254, 217),
                  new Color(233, 255, 239),
                  new Color(0, 63, 39),
                  new Color(0, 92, 57),
                  new Color(0, 120, 75),
                  new Color(0, 148, 92),
                  new Color(0, 177, 110),
                  new Color(0, 205, 128),
                  new Color(0, 233, 146),
                  new Color(7, 254, 162),
                  new Color(35, 255, 172),
                  new Color(63, 255, 183),
                  new Color(92, 255, 193),
                  new Color(120, 255, 204),
                  new Color(148, 254, 215),
                  new Color(177, 255, 225),
                  new Color(205, 254, 236),
                  new Color(233, 255, 247),
                  new Color(0, 63, 63),
                  new Color(0, 92, 92),
                  new Color(0, 120, 120),
                  new Color(0, 148, 148),
                  new Color(0, 177, 177),
                  new Color(0, 205, 205),
                  new Color(0, 233, 233),
                  new Color(7, 254, 254),
                  new Color(35, 255, 255),
                  new Color(63, 255, 255),
                  new Color(92, 255, 255),
                  new Color(120, 255, 255),
                  new Color(148, 254, 254),
                  new Color(177, 255, 255),
                  new Color(205, 254, 254),
                  new Color(233, 255, 255),
                  new Color(0, 39, 63),
                  new Color(0, 57, 92),
                  new Color(0, 75, 120),
                  new Color(0, 92, 148),
                  new Color(0, 110, 177),
                  new Color(0, 128, 205),
                  new Color(0, 146, 233),
                  new Color(7, 162, 254),
                  new Color(35, 172, 255),
                  new Color(63, 183, 255),
                  new Color(92, 193, 255),
                  new Color(120, 204, 255),
                  new Color(148, 215, 254),
                  new Color(177, 225, 255),
                  new Color(205, 236, 254),
                  new Color(233, 247, 255),
                  new Color(0, 15, 63),
                  new Color(0, 23, 92),
                  new Color(0, 30, 120),
                  new Color(0, 37, 148),
                  new Color(0, 44, 177),
                  new Color(0, 51, 205),
                  new Color(0, 58, 233),
                  new Color(7, 69, 254),
                  new Color(35, 90, 255),
                  new Color(63, 111, 255),
                  new Color(92, 132, 255),
                  new Color(120, 154, 255),
                  new Color(148, 175, 254),
                  new Color(177, 196, 255),
                  new Color(205, 217, 254),
                  new Color(233, 239, 255),
                  new Color(7, 0, 63),
                  new Color(11, 0, 92),
                  new Color(15, 0, 120),
                  new Color(18, 0, 148),
                  new Color(22, 0, 177),
                  new Color(25, 0, 205),
                  new Color(29, 0, 233),
                  new Color(38, 7, 254),
                  new Color(62, 35, 255),
                  new Color(87, 63, 255),
                  new Color(112, 92, 255),
                  new Color(137, 120, 255),
                  new Color(162, 148, 254),
                  new Color(186, 177, 255),
                  new Color(211, 205, 254),
                  new Color(236, 233, 255),
                  new Color(31, 0, 63),
                  new Color(46, 0, 92),
                  new Color(60, 0, 120),
                  new Color(74, 0, 148),
                  new Color(88, 0, 177),
                  new Color(102, 0, 205),
                  new Color(116, 0, 233),
                  new Color(131, 7, 254),
                  new Color(145, 35, 255),
                  new Color(159, 63, 255),
                  new Color(173, 92, 255),
                  new Color(187, 120, 255),
                  new Color(201, 148, 254),
                  new Color(216, 177, 255),
                  new Color(230, 205, 254),
                  new Color(244, 233, 255),
                  new Color(55, 0, 63),
                  new Color(80, 0, 92),
                  new Color(105, 0, 120),
                  new Color(130, 0, 148),
                  new Color(154, 0, 177),
                  new Color(179, 0, 205),
                  new Color(204, 0, 233),
                  new Color(224, 7, 254),
                  new Color(227, 35, 255),
                  new Color(231, 63, 255),
                  new Color(234, 92, 255),
                  new Color(238, 120, 255),
                  new Color(241, 148, 254),
                  new Color(245, 177, 255),
                  new Color(248, 205, 254),
                  new Color(252, 233, 255)
          });
      }

    }
    
}
