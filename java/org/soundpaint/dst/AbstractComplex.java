/*
 * AbstractComplex.java
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

public abstract class AbstractComplex implements MutableComplex
{
  protected static final double HALF_PI = 0.5 * Math.PI;
  protected static final double DOUBLE_PI = 2.0 * Math.PI;
  protected static final double INV_PI = 1.0 / Math.PI;
  public static final boolean RAISE_ON_NAN = true;
  public static final boolean RAISE_ON_INFINITY = true;

  private boolean immutable;

  public static AbstractComplex fromCartesian(final double real,
                                              final double imaginary,
                                              final boolean immutable)
  {
    final AbstractComplex complex = new CartesianComplex(real, imaginary);
    complex.immutable = immutable;
    return complex;
  }

  public static AbstractComplex fromCartesian(final Complex prototype,
                                              final boolean immutable)
  {
    final AbstractComplex complex = new CartesianComplex(prototype);
    complex.immutable = immutable;
    return complex;
  }

  public static AbstractComplex fromPolar(final double phi,
                                          final double length)
  {
    return fromPolar(phi, length, false);
  }

  public static AbstractComplex fromPolar(final double phi,
                                          final double length,
                                          final boolean immutable)
  {
    final AbstractComplex complex = CartesianComplex.fromPolar(phi, length);
    complex.immutable = immutable;
    return complex;
  }

  protected void checkMutable()
  {
    if (immutable)
      throw new IllegalStateException("can not modify immutable complex");
  }

  protected void checkValue(final double value, final String name)
  {
    if (RAISE_ON_NAN && Double.isNaN(value))
      throw new ArithmeticException("NaN(" + name + ")");
    if (RAISE_ON_INFINITY && Double.isInfinite(value))
      throw new ArithmeticException("Infinity(" + name + ")");
  }

  public abstract void copyFrom(final Complex other);

  public abstract void setRealPart(final double real);

  public abstract double getRealPart();

  public abstract void setImaginaryPart(final double imaginary);

  public abstract double getImaginaryPart();

  public abstract void setCartesianCoordinates(final double real,
                                               final double imaginary);

  public abstract void setPolarCoordinates(final double phi,
                                           final double length);

  public abstract void clear();

  public abstract double getNorm();

  public abstract double getLength();

  public abstract void setLength(final double length);

  public abstract double getPhi();

  public abstract void setPhi(final double phi);

  public abstract void neg();

  public abstract void conjugate();

  public abstract void add(final double real);

  public abstract void add(final double real, final double imaginary);

  public abstract void add(final Complex other);

  public abstract void sub(final double real);

  public abstract void sub(final double real, final double imaginary);

  public abstract void sub(final Complex other);

  public abstract void mul(final double real);

  public abstract void mul(final double real, final double imaginary);

  public abstract void mul(final Complex other);

  public abstract void inv();

  public abstract void div(final double real);

  public abstract void div(final double real, final double imaginary);

  public abstract void div(final Complex other);

  public abstract boolean equals(final Object obj);

  public abstract String toPolarString();

  /**
   * Workaround number formatting utility method that removes
   * additional whitespace of default formatting of Infinite values.
   */
  protected static String formatDouble(final double value)
  {
    final String formatted;
    if (value == Double.POSITIVE_INFINITY) {
      formatted = "+Infinity";
    } else if (value == Double.NEGATIVE_INFINITY) {
      formatted = "-Infinity";
    } else if (value == Double.NaN) {
      formatted = "NaN";
    } else {
      // For all other cases, String.format() should return a sane
      // result.
      formatted = String.format("%+10.4f", value);
    }
    return formatted;
  }

  public String toString()
  {
    return "(" + formatDouble(getRealPart()) + "+" +
      formatDouble(getImaginaryPart()) + "i)";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
