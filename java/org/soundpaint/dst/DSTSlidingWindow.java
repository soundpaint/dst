/*
 * DSTSlidingWindow.java
 * (C) 2009, 2017 by
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

public class DSTSlidingWindow implements SlidingWindowTransform
{
  private final static double DEFAULT_α =
    1.0 - Math.exp(1.0 / 150.0 * Math.log(1.0 / 3.0));

  public final static double DEFAULT_μ0 = 1.0 - DEFAULT_α;

  // @TODO: Clarify: Shall μ(FREQ) = μ0 be frequency dependent?
  // E.g. something like μ(FREQ) = 1.0 - Math.exp(FREQ *
  // Math.log(μ0))?

  public final static double DEFAULT_LOWER_BOUND = 0.001;
  public final static double DEFAULT_UPPER_BOUND = 0.2;

  private double μ0;
  private int size;
  private double lowerBound, upperBound;
  private MutableComplex window[];
  private Complex signalShift[];

  private DSTSlidingWindow() {}

  public DSTSlidingWindow(int size)
  {
    this(size, DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
  }

  public DSTSlidingWindow(int size,
                          double lowerBound, double upperBound)
  {
    this(DEFAULT_μ0, size, lowerBound, upperBound);
  }

  public DSTSlidingWindow(double μ0, int size,
                          double lowerBound, double upperBound)
  {
    this();
    if ((μ0 < 0.0) || (μ0 >= 1.0))
      throw new IllegalArgumentException("μ0 not in [0.0..1.0)");
    this.μ0 = μ0;
    if (size <= 0)
      throw new IllegalArgumentException("window size <= 0");
    this.size = size;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    window = new MutableComplex[size];
    for (int i = 0; i < size; i++) {
      window[i] = ComplexFactory.createMutableFromCartesian(0.0);
    }
    signalShift = new Complex[size];
    double bandWidthNatural = Math.log(upperBound / lowerBound);
    for (int i = 0; i < size; i++) {
      double frac =
	lowerBound * Math.exp(((double)i) / size * bandWidthNatural);
      signalShift[i] =
	ComplexFactory.createFromPolar(μ0, -2.0 * Math.PI * frac);
    }
  }

  public void printInfo(PrintStream out, double samplingFrequency)
  {
    double bandWidthNatural = Math.log(upperBound / lowerBound);
    double bandWidthInOctaves = bandWidthNatural / Math.log(2.0);
    double bandWidthInCent = bandWidthInOctaves * 1200.0;
    double resolution = bandWidthInCent / size;
    out.println("sampling frequency [Hz]: " + samplingFrequency);
    out.println("spectral lines [#]: " + size);
    out.println("lower frequency limit [Hz]: " +
		lowerBound * samplingFrequency);
    out.println("upper frequency limit [Hz]: " +
		upperBound * samplingFrequency);
    out.println("band width [octaves]: " + bandWidthInOctaves);
    out.println("band width [cent]: " + bandWidthInCent);
    out.println("resolution [cent per spectral line]: " + resolution);
  }

  public int getSize() { return size; }

  public double getLowerBound() { return lowerBound; }

  public double getUpperBound() { return upperBound; }

  public double distanceTo(DSTSlidingWindow other)
  {
    if (other.size != size)
      throw new IllegalArgumentException("can not compare spectrum for windows of different size");
    double sum = 0.0;
    for (int i = 0; i < size; i++) {
      double diff = window[i].getLength() - other.window[i].getLength();
      sum += diff * diff;
    }
    return sum;
  }

  public void putBin(double sample)
  {
    Complex insertSample = ComplexFactory.createFromCartesian(sample, 0.0);
    for (int i = 0; i < size; i++) {
      window[i].mul(signalShift[i]);
      window[i].add(insertSample);
      MutableComplex tmp =
	ComplexFactory.createMutableFromCartesian(signalShift[i]);
      tmp.mul(insertSample);
      window[i].sub(tmp);
    }
  }

  public Complex getLine(int index)
  {
    assert index >= 0 && index < size : "index out of range";
    return window[index];
  }

  public double getReconstructedSample()
  {
    return getReconstructedSample(null);
  }

  public double getReconstructedSample(TransferFunction filter)
  {
    assert
      (filter == null) || (filter.getLength() == size) : "bad filter length";
    MutableComplex sample = ComplexFactory.createMutableFromCartesian(0.0);
    for (int i = 0; i < size; i++) {
      MutableComplex line =
	ComplexFactory.createMutableFromCartesian(window[i]);
      if (filter != null) {
	Complex transferValue =
	  ComplexFactory.createFromCartesian(filter.getTransferValue(i));
	line.mul(transferValue);
      }
      sample.add(line);
    }
    sample.mul(+1.0 / size);
    return sample.getRealPart();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
