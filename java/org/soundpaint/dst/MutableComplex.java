/*
 * MutableComplex.java
 * (C) 2009, 2010, 2011, 2012 by
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

public interface MutableComplex extends Complex
{
  public void copyFrom(final Complex other);
  public void setRealPart(final double real);
  public void setImaginaryPart(final double imaginary);
  public void setCartesianCoordinates(final double real,
                                      final double imaginary);
  public void setPolarCoordinates(final double phi, final double length);
  public void clear();
  public void setLength(final double length);
  public void setPhi(final double phi);
  public void neg();
  public void conjugate();
  public void add(final double real);
  public void add(final double real, final double imaginary);
  public void add(final Complex other);
  public void sub(final double real);
  public void sub(final double real, final double imaginary);
  public void sub(final Complex other);
  public void mul(final double real);
  public void mul(final double real, final double imaginary);
  public void mul(final Complex other);
  public void inv();
  public void div(final double real);
  public void div(final double real, final double imaginary);
  public void div(final Complex other);
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
