/*
 * PPMStreamOutput.java
 * (C) 2009, 2010, 2019 by
 * Jürgen Reuter <http://www.juergen-reuter.de/>
 *
 * Project Website: http://www.soundpaint.org/spectral-transform/
 * Jürgen Reuter, Rheinstr. 86, 76185 Karlsruhe, Germany.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * $Author$
 * $Date$
 * $Id$
 */
package org.soundpaint.dst;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class PPMStreamOutput
{
  private final PrintWriter out;
  private final int width, height;
  private int count;

  public PPMStreamOutput(final String filename,
                         final int width, final int height)
    throws IOException
  {
    this.width = width;
    this.height = height;
    out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(filename)));
    out.printf("P3\n");
    out.printf("# %s\n", filename);
    out.printf("%d %d\n", width, height);
    out.printf("255\n");
    count = 0;
  }

  private void putValue(final int value, final char separator)
    throws IOException
  {
    out.printf("%4d%c", value, separator);
  }

  private static final double invDegree = 1.0 / 360.0;
  private static final double rad2grad = 180.0 / Math.PI;
  private static final double sixthCycle =  1.0 / 60.0;

  public void putPixel(final double hue, final double saturation,
                       final double value)
    throws IOException
  {
    /* implementation of this method follows the algorithm
       described on
       http://en.wikipedia.org/wiki/HSV_color_space#Conversion_from_HSV_to_RGB */
    double h = hue * rad2grad;
    if (h >= 360.0)
      h -= ((int)(h * invDegree)) * 360.0;
    else if (h < 0.0)
      h -= ((int)(h * invDegree)) * 360.0 - 360.0;
    if (h >= 360.0) h -= 360.0; // compensate possible rounding err
    if (h < 0.0) h = 0.0; // dto.
    //h = 0.5 * h; // DEBUG: Use only half of spectrum
    double s = saturation;
    if (s < 0.0) s = 0.0;
    if (s > 1.0) s = 1.0;
    double v = value;
    if (v < 0.0) v = 0.0;
    if (v > 1.0) v = 1.0;
    final double hSixthCycle = h * sixthCycle;
    final int intHSixthCycle = (int)hSixthCycle;
    final int hi = intHSixthCycle % 6;
    final double f = hSixthCycle - intHSixthCycle;
    final double p = v * (1.0 - s);
    final double q = v * (1.0 - f * s);
    final double t = v * (1.0 - (1.0 - f) * s);
    double r = 0.0, g = 0.0, b = 0.0;
    switch (hi) {
    case 0:
      r = v; g = t; b = p;
      break;
    case 1:
      r = q; g = v; b = p;
      break;
    case 2:
      r = p; g = v; b = t;
      break;
    case 3:
      r = p; g = q; b = v;
      break;
    case 4:
      r = t; g = p; b = v;
      break;
    case 5:
      r = v; g = p; b = q;
      break;
    default:
      throw new IllegalStateException("case fall-through");
    }
    int red = (int)(256.0 * r);
    if (red < 0) red = 0;
    if (red > 255) red = 255;
    int green = (int)(256.0 * g);
    if (green < 0) green = 0;
    if (green > 255) green = 255;
    int blue = (int)(256.0 * b);
    if (blue < 0) blue = 0;
    if (blue > 255) blue = 255;
    putPixel(red, green, blue);
  }

  public void putPixel(final int red, final int green, final int blue)
    throws IOException
  {
    if ((red < 0) || (red > 255))
      throw new IllegalArgumentException("red out of range: " + red);
    if ((green < 0) || (green > 255))
      throw new IllegalArgumentException("green out of range: " + blue);
    if ((blue < 0) || (blue > 255))
      throw new IllegalArgumentException("blue out of range: " + green);
    if (count >= (width * height))
      throw new IllegalStateException("no more pixel");
    if ((count % width) == 0)
      out.printf("\n# line %d:\n", count / width);
    count++;
    final char separator;
    if ((count & 0xf) == 0xf) {
      separator = '\n';
    } else {
      separator = ' ';
    }
    putValue(red, ' ');
    putValue(green, ' ');
    putValue(blue, separator);
  }

  public void close() throws IOException
  {
    if (count < (width * height)) {
      System.err.println("PPMStreamOutput: Warning: " +
                         "closing incomplete image: " +
                         count + " < " + (width * height));
    }
    out.printf("\n");
    out.close();
  }

  public void finalize() throws IOException
  {
    close();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
