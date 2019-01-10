/*
 * FiniteWave.java
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

import java.io.IOException;

/**
 * A finite wave is a wave whose method @code{eof()} is guaranteed to
 * eventually return the value @code{true}.
 */
public abstract class FiniteWave implements Wave
{
  private long position = 0;

  public abstract long getTotalNumberOfSamples();

  protected abstract double getNextSample(final long position)
    throws IOException;

  public double getNextSample() throws IOException
  {
    return getNextSample(position++);
  }

  /**
   * Returns the number of samples that have been fetched from this
   * wave so far.
   */
  public long getPosition()
  {
    return position;
  }

  /**
   * Skips @code{n} samples.
   */
  abstract void skip(final long n) throws IOException;
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
