package nyaa.alice.jviewer.data;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringComparator
{
    public static final WindowsExplorerComparator NATURAL_SORT_WIN_EXPLORER = new WindowsExplorerComparator();

    public static final NaturalOrderComparator NATURAL_SORT = new NaturalOrderComparator();

    public static class NaturalOrderComparator implements Comparator<String>
    {
        /*
         * this is a modified copy of https://github.com/paour/natorder changed to work
         * how i wanted it, cleaned up some functions and remove use of substr
         * 
         * --- original notice --- NaturalOrderComparator.java -- Perform 'natural
         * order' comparisons of strings in Java. Copyright (C) 2003 by Pierre-Luc Paour
         * <natorder@paour.com> Based on the C version by Martin Pool, of which this is
         * more or less a straight conversion. Copyright (C) 2000 by Martin Pool
         * <mbp@humbug.org.au> This software is provided 'as-is', without any express or
         * implied warranty. In no event will the authors be held liable for any damages
         * arising from the use of this software. Permission is granted to anyone to use
         * this software for any purpose, including commercial applications, and to
         * alter it and redistribute it freely, subject to the following restrictions:
         * 1. The origin of this software must not be misrepresented; you must not claim
         * that you wrote the original software. If you use this software in a product,
         * an acknowledgment in the product documentation would be appreciated but is
         * not required. 2. Altered source versions must be plainly marked as such, and
         * must not be misrepresented as being the original software. 3. This notice may
         * not be removed or altered from any source distribution.
         */

        private int compareRight(String a, String b, int ia, int ib)
        {
            int bias = 0;

            // The longest run of digits wins. That aside, the greatest
            // value wins, but we can't know that it will until we've scanned
            // both numbers to know that they have the same magnitude, so we
            // remember it in BIAS.
            for (;; ia++, ib++)
            {
                char ca = charAt(a, ia);
                char cb = charAt(b, ib);

                if (!isDigit(ca) && !isDigit(cb))
                {
                    return bias;
                }
                if (!isDigit(ca))
                {
                    return -1;
                }
                if (!isDigit(cb))
                {
                    return +1;
                }
                if (ca == 0 && cb == 0)
                {
                    return bias;
                }

                if (bias == 0)
                {
                    if (ca < cb)
                    {
                        bias = -1;
                    }
                    else if (ca > cb)
                    {
                        bias = +1;
                    }
                }
            }
        }

        public int compare(String a, String b)
        {
            int ia = 0, ib = 0;
            char ca, cb;

            while (true)
            {
                ca = charAt(a, ia);
                cb = charAt(b, ib);

                // Process run of digits
                if (Character.isDigit(ca) && Character.isDigit(cb))
                {
                    int bias = compareRight(a, b, ia, ib);
                    if (bias != 0)
                    {
                        return bias;
                    }
                }

                if (ca == 0 && cb == 0)
                {
                    // The strings compare the same. Perhaps the caller
                    // will want to call strcmp to break the tie.
                    return compareEqual(a, b);
                }
                if (ca < cb)
                {
                    return -1;
                }
                if (ca > cb)
                {
                    return +1;
                }

                ++ia;
                ++ib;
            }
        }

        public static boolean isDigit(char c)
        {
            return Character.isDigit(c) || c == '.' || c == ',';
        }

        public static char charAt(String s, int i)
        {
            return i >= s.length() ? 0 : s.charAt(i);
        }

        public static int compareEqual(String a, String b)
        {
            if (a.length() == b.length())
                return a.compareTo(b);

            return a.length() - b.length();
        }
    }

    public static class WindowsExplorerComparator implements Comparator<String>
    {
        private static final Pattern splitPattern = Pattern.compile("\\d+|\\.|\\s");

        @Override
        public int compare(String str1, String str2)
        {
            Iterator<String> i1 = splitStringPreserveDelimiter(str1).iterator();
            Iterator<String> i2 = splitStringPreserveDelimiter(str2).iterator();

            while (true)
            {
                // Til here all is equal.
                if (!i1.hasNext() && !i2.hasNext())
                {
                    return 0;
                }
                // first has no more parts -> comes first
                if (!i1.hasNext() && i2.hasNext())
                {
                    return -1;
                }
                // first has more parts than i2 -> comes after
                if (i1.hasNext() && !i2.hasNext())
                {
                    return 1;
                }

                String data1 = i1.next();
                String data2 = i2.next();
                int result;
                try
                {
                    // If both datas are numbers, then compare numbers
                    result = Long.compare(Long.valueOf(data1), Long.valueOf(data2));
                    // If numbers are equal than longer comes first
                    if (result == 0)
                    {
                        result = -Integer.compare(data1.length(), data2.length());
                    }
                }
                catch (NumberFormatException ex)
                {
                    // compare text case insensitive
                    result = data1.compareToIgnoreCase(data2);
                }

                if (result != 0)
                {
                    return result;
                }
            }
        }

        private List<String> splitStringPreserveDelimiter(String str)
        {
            Matcher matcher = splitPattern.matcher(str);
            // List<String> list = new ArrayList<String>();
            LinkedList<String> list = new LinkedList<String>();
            int pos = 0;

            while (matcher.find())
            {
                list.add(str.substring(pos, matcher.start()));
                list.add(matcher.group());
                pos = matcher.end();
            }

            list.add(str.substring(pos));
            return list;
        }
    }
}
