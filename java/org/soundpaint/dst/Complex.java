/*
 * Complex.java
 * (C) 2009, 2010, 2011, 2012, 2015 by
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

public interface Complex
{
  public static final Complex ZERO =
    ComplexFactory.createFromCartesian(0.0, 0.0);
  public static final Complex REAL_UNIT =
    ComplexFactory.createFromCartesian(1.0, 0.0);
  public static final Complex IMAGINARY_UNIT =
    ComplexFactory.createFromCartesian(0.0, 1.0);

  public double getRealPart();

  public double getImaginaryPart();

  public double getNorm();

  public double getLength();

  public double getPhi();

  public boolean equals(final Object obj);

  public String toPolarString();

  public String toString();
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
