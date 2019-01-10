/*
 * DFT.java
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

public class DFT
{
  private final DFTSlidingWindow window;
  private boolean frozen;

  private DFT()
  {
    throw new UnsupportedOperationException("unsupported empty constructor");
  }

  public DFT(final int resolution)
  {
    window = new DFTSlidingWindow(resolution);
    frozen = false;
  }

  /**
   * Add the next sample from the slice for building the fingerprint
   * of the associated slice.
   */
  public void addSample(final double sample)
  {
    if (frozen)
      throw new IllegalStateException("already frozen");
    window.putBin(sample);
  }

  /**
   * Turns this object into an immutable state.
   */
  public void freeze()
  {
    if (frozen)
      throw new IllegalStateException("already frozen");
    frozen = true;
  }

  public double distanceTo(final DFT other)
  {
    if (!frozen)
      throw new IllegalStateException("need to freeze before evaluation");
    return window.distanceTo(other.window);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
