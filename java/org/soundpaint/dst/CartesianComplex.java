/*
 * CartesianComplex.java
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

public class CartesianComplex extends AbstractComplex
{
  private double real, imaginary;

  public CartesianComplex()
  {
    this(0.0);
  }

  public CartesianComplex(final double real)
  {
    this(real, 0.0);
  }

  public CartesianComplex(final double real, final double imaginary)
  {
    setCartesianCoordinates(real, imaginary);
  }

  public CartesianComplex(final Complex prototype)
  {
    this(prototype.getRealPart(), prototype.getImaginaryPart());
  }

  public static CartesianComplex fromPolar(final double length,
                                           final double phi)
  {
    final CartesianComplex complex = new CartesianComplex();
    complex.checkValue(length, "length");
    complex.checkValue(phi, "phi");
    complex.setPolarCoordinates(length, phi);
    return complex;
  }

  public void copyFrom(final Complex other)
  {
    setCartesianCoordinates(other.getRealPart(), other.getImaginaryPart());
  }

  public void setRealPart(final double real)
  {
    checkMutable();
    checkValue(real, "r");
    this.real = real;
  }

  public double getRealPart()
  {
    return real;
  }

  public void setImaginaryPart(final double imaginary)
  {
    checkMutable();
    checkValue(imaginary, "i");
    this.imaginary = imaginary;
  }

  public double getImaginaryPart()
  {
    return imaginary;
  }

  public void setCartesianCoordinates(final double real,
                                      final double imaginary)
  {
    setRealPart(real);
    setImaginaryPart(imaginary);
  }

  public void setPolarCoordinates(final double length,
                                  final double phi)
  {
    checkValue(length, "length");
    checkValue(phi, "phi");
    setCartesianCoordinates(length * Math.cos(phi), length * Math.sin(phi));
  }

  public void clear()
  {
    setCartesianCoordinates(0.0, 0.0);
  }

  public double getNorm()
  {
    return real * real + imaginary * imaginary;
  }

  public double getLength()
  {
    return Math.sqrt(getNorm());
  }

  public void setLength(final double length)
  {
    checkValue(length, "length");
    mul(length / getLength());
  }

  public double getPhi()
  {
    return Math.atan2(imaginary, real);
  }

  public void setPhi(final double phi)
  {
    setPolarCoordinates(getLength(), phi);
  }

  public void neg()
  {
    checkMutable();
    real = -real;
    conjugate();
  }

  public void conjugate()
  {
    checkMutable();
    imaginary = -imaginary;
  }

  public void add(final double real)
  {
    add(real, 0.0);
  }

  public void add(final double real, final double imaginary)
  {
    checkValue(real, "r");
    checkValue(imaginary, "i");
    setCartesianCoordinates(this.real + real, this.imaginary + imaginary);
  }

  public void add(final Complex other)
  {
    add(other.getRealPart(), other.getImaginaryPart());
  }

  public void sub(final double real)
  {
    sub(real, 0.0);
  }

  public void sub(final double real, final double imaginary)
  {
    checkValue(real, "r");
    checkValue(imaginary, "i");
    setCartesianCoordinates(this.real - real, this.imaginary - imaginary);
  }

  public void sub(final Complex other)
  {
    sub(other.getRealPart(), other.getImaginaryPart());
  }

  public void mul(final double real)
  {
    mul(real, 0.0);
  }

  public void mul(final double real, final double imaginary)
  {
    checkValue(real, "r");
    checkValue(imaginary, "i");
    setCartesianCoordinates(this.real * real -
                            this.imaginary * imaginary,
                            this.real * imaginary +
                            this.imaginary * real);
  }

  public void mul(final Complex other)
  {
    mul(other.getRealPart(), other.getImaginaryPart());
  }

  public void inv()
  {
    final double scale = 1.0 / getNorm();
    setCartesianCoordinates(+scale * real, -scale * imaginary);
  }

  public void div(double real)
  {
    div(real, 0.0);
  }

  public void div(final double real, final double imaginary)
  {
    checkValue(real, "r");
    checkValue(imaginary, "i");
    final double scale = 1.0 / (real * real + imaginary * imaginary);
    mul(+scale * real, -scale * imaginary);
  }

  public void div(final Complex other)
  {
    div(other.getRealPart(), other.getImaginaryPart());
  }

  public boolean equals(final Object obj)
  {
    if (obj instanceof Complex) {
      final Complex other = (Complex)obj;
      return
        (other != null) &&
        (this.real == other.getRealPart()) &&
        (this.imaginary == other.getImaginaryPart());
    } else {
      return false;
    }
  }

  public int hashCode() {
    return Double.hashCode(real) ^ Double.hashCode(imaginary);
  }

  public String toPolarString() {
    final double length = getLength();
    return
      length != 0.0 ?
      String.format("(" + formatDouble(length) + "*e^(%+7.4f\u03c0))",
                    getPhi() * INV_PI) :
      "(" + formatDouble(length) + "*e^(+0.0000\u03c0))";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
