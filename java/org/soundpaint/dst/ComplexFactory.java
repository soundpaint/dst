/*
 * ComplexFactory.java
 * (C) 2009, 2010, 2011, 2012, 2015, 2019 by
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

public class ComplexFactory
{
  public static Complex createFromCartesian()
  {
    return createFromCartesian(0.0);
  }

  public static Complex createFromCartesian(final double real)
  {
    return createFromCartesian(real, 0.0);
  }

  public static Complex createFromCartesian(final double real,
                                            final double imaginary)
  {
    return AbstractComplex.fromCartesian(real, imaginary, true);
  }

  public static MutableComplex createMutableFromCartesian()
  {
    return createMutableFromCartesian(0.0);
  }

  public static MutableComplex createMutableFromCartesian(final double real)
  {
    return createMutableFromCartesian(real, 0.0);
  }

  public static MutableComplex createMutableFromCartesian(final double real,
                                                          final double imaginary) {
    return AbstractComplex.fromCartesian(real, imaginary, false);
  }

  public static Complex createFromCartesian(final Complex prototype) {
    return AbstractComplex.fromCartesian(prototype, true);
  }

  public static MutableComplex createMutableFromCartesian(final Complex prototype)
  {
    return AbstractComplex.fromCartesian(prototype, false);
  }

  public static Complex createFromPolar(final double phi, final double length)
  {
    return AbstractComplex.fromPolar(phi, length, true);
  }

  public static MutableComplex createMutableFromPolar(final double phi,
                                                      final double length)
  {
    return AbstractComplex.fromPolar(phi, length, false);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
