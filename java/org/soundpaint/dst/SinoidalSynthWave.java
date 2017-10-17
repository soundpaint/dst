/*
 * SinoidalSynthWave.java
 * (C) 2017 by
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

public class SinoidalSynthWave implements Wave {

  private final double[] deltaPhases;
  private double[] phases;

  public SinoidalSynthWave(final double[] deltaPhases,
                           final double[] initialPhases) {
    this.deltaPhases = deltaPhases;
    this.phases = new double[deltaPhases.length];
    if (initialPhases != null) {
      if (phases.length != initialPhases.length) {
        final String message =
          String.format("sizes of arrays do not match: %d != %d",
                        phases.length, initialPhases.length);
          throw new IllegalArgumentException(message);
      }
      for (int i = 0; i < initialPhases.length; i++) {
        phases[i] = initialPhases[i];
      }
    }
  }

  public SinoidalSynthWave(final double sampleRate, final double[] frequencies)
  {
    this(sampleRate, frequencies, null);
  }

  public SinoidalSynthWave(final double sampleRate, final double[] frequencies,
                           final double[] initialPhases)
  {
    this(createDeltaPhases(sampleRate,frequencies), initialPhases);
  }

  private static final double[] createDeltaPhases(final double sampleRate,
                                                  final double[] frequencies)
  {
    final double[] deltaPhases = new double[frequencies.length];
    for (int i = 0; i < deltaPhases.length; i++) {
      deltaPhases[i] = 2.0 * Math.PI * frequencies[i] / sampleRate;
    }
    return deltaPhases;
  }

  public double getPhase(final int index)
  {
    return phases[index];
  }

  public void setPhase(final int index, final double phase)
  {
    phases[index] = phase;
  }

  public double getNextSample()
  {
    double sample = 0.0;
    for (int i = 0; i < phases.length; i++) {
      final double partial = Math.sin(phases[i]);
      phases[i] += deltaPhases[i];
      sample += partial;
    }
    return sample;
  }

  public boolean eof()
  {
    return false;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
