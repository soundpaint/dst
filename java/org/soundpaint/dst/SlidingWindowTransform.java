/*
 * SlidingWindowTransform.java
 * (C) 2010, 2019 by
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

public interface SlidingWindowTransform
{
  /**
   * Default value for the lower bound frequency of the frequency
   * range to be considered, specified as fraction of the sampling
   * frequency.
   */
  public static final double DEFAULT_LOWER_BOUND = 0.001;

  /**
   * Default value for the upper bound frequency of the frequency
   * range to be considered, specified as fraction of the sampling
   * frequency.
   */
  public static final double DEFAULT_UPPER_BOUND = 0.2; // [samplingFrequency]

  /**
   * Returns the window size.
   */
  int getSize();

  /**
   * Pretty print info about this transform when using it with the
   * specified sampling frequency.
   */
  void printInfo(final PrintStream out, final double samplingFrequency);

  /**
   * Puts in the next sample and moves forward the sliding window by
   * one bin.
   */
  void putBin(final double sample);

  /**
   * Returns the spectral line with the specified index as complex
   * value that represents amplitude and phase of the spectral line.
   */
  Complex getLine(final int index);

  /**
   * Returns the normalized sum of the spectrum.
   */
  double getReconstructedSample();

  /**
   * Applies the specified filter function onto the spectrum and then
   * returns the normalized sum of the spectrum.
   * @param filter The filter function to apply.  If
   * <code>null</code>, no filter is applied; i.e. the method behaves
   * as if an identity function would be passed as filter.
   */
  double getReconstructedSample(final TransferFunction filter);
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
