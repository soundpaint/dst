/*
 * Wave.java
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
 * A wave produces a (possibly finite) series of sample values.
 */
public interface Wave
{
  /**
   * Resets this wave to its start, such that the next sample returned
   * will be again the first one.
   */
  void reset() throws IOException;

  /**
   * @return The next sample value of the series.
   */
  double getNextSample() throws IOException;

  /**
   * @return false If there is no more sample value in the series to
   * return, that is, the end of the series has been reached.
   */
  boolean eof() throws IOException;
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
