/*
 * MutableComplex.java
 * (C) 2009, 2010, 2011, 2012, 2019 by
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
  void copyFrom(final Complex other);
  void setRealPart(final double real);
  void setImaginaryPart(final double imaginary);
  void setCartesianCoordinates(final double real,
                               final double imaginary);
  void setPolarCoordinates(final double phi, final double length);
  void clear();
  void setLength(final double length);
  void setPhi(final double phi);
  void neg();
  void conjugate();
  void add(final double real);
  void add(final double real, final double imaginary);
  void add(final Complex other);
  void sub(final double real);
  void sub(final double real, final double imaginary);
  void sub(final Complex other);
  void mul(final double real);
  void mul(final double real, final double imaginary);
  void mul(final Complex other);
  void inv();
  void div(final double real);
  void div(final double real, final double imaginary);
  void div(final Complex other);
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
