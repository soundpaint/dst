/*
 * SlidingWindowTransform.java
 * (C) 2010 by
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

import java.io.PrintStream;

public interface SlidingWindowTransform {

  /**
   * Returns the window size.
   */
  public int getSize();

  /**
   * Pretty print info about this transform when using it with the
   * specified sampling frequency.
   */
  public void printInfo(PrintStream out, double samplingFrequency);

  /**
   * Puts in the next sample and moves forward the sliding window by
   * one bin.
   */
  public void putBin(double sample);

  /**
   * Returns the spectral line with the specified index as complex
   * value that represents amplitude and phase of the spectral line.
   */
  public Complex getLine(int index);

  /**
   * Returns the normalized sum of the spectrum.
   */
  public double getReconstructedSample();

  /**
   * Applies the specified filter function onto the spectrum and then
   * returns the normalized sum of the spectrum.
   * @param filter The filter function to apply.  If
   * <code>null</code>, no filter is applied; i.e. the method behaves
   * as if an identity function would be passed as filter.
   */
  public double getReconstructedSample(TransferFunction filter);
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
