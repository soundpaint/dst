package org.soundpaint.dstx;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Given the function
 *
 * f(α) := l_p * (1 / α) * exp(1 / α) = d
 *
 * this program approximates its reverse function
 *
 * f⁻¹(d) = α
 *
 * for any given α₀/2 ≤ α ≤ 2α₀.
 *
 * and, from that approximation, computes
 *
 * δα/δt ≈ (α₁ - α₀) / (t₁ - t₀)
 *       = (α(t₁) - α(t₀)) / (t₁ - t₀)
 *       = (f⁻¹(d(t₁)) - f⁻¹(d(t₀))) / (t₁ - t₀)
 *
 * for various 1*a ≤ δt ≤ 10e8*a (with annum a = 365.242*24*60*60s =
 * 31556908.8s).
 *
 * The numerical results suggest (within the precision limits of the
 * approximation) a value of roughly -3.83e-15 / a.
 */
public class SolveExpEqBigDecimal
{
  private static final MathContext MC = MathContext.DECIMAL128;
  private static final BigDecimal HALF = new BigDecimal(0.5);
  private static final BigDecimal TWO = new BigDecimal(2.0);
  private static final BigDecimal α0 =
    new BigDecimal(7.29735256e-3); // fine-struct. constant now
  private static final BigDecimal L_P =
    new BigDecimal(1.616e-35); // [m] Plank Length
  private static final BigDecimal D_FOR_α0 = α2d(α0); // [m]
  private static final BigDecimal AGE = new BigDecimal(13.81e9); // [a]
  private static final BigDecimal EXP_NOW =
    new BigDecimal(8.8e26); // [m] ≈ 93e9 LY
  private static final BigDecimal EXP_RATE = EXP_NOW.divide(AGE, MC); // [m/a]

  private BigDecimal δt; // [a] delta time (years) to use for d/dt approximation
  private BigDecimal EXP_THEN; // [m] expected future expansion
  private BigDecimal EXP_REL; // [1] expansion rate
  private BigDecimal D_FOR_α1; // [m]
  private BigDecimal α1; // [1] future fine-structure constant
  private BigDecimal δα; // [1] fine-structure constants delta
  private BigDecimal δαδt; // [1/a] fine-structure constant drift per year

  private void compute(final BigDecimal δt)
  {
    this.δt = δt;
    EXP_THEN = EXP_NOW.add(EXP_RATE.multiply(δt));
    EXP_REL = EXP_THEN.divide(EXP_NOW, MC);
    System.out.println("EXP_REL=" + EXP_REL);
    D_FOR_α1 = D_FOR_α0.multiply(EXP_REL);
    System.out.println("d(α₀)=" + D_FOR_α0);
    System.out.println("d(α₁)=" + D_FOR_α1);
    α1 = d2α(D_FOR_α1);
    System.out.println("α₀=" + α0);
    System.out.println("α₁=" + α1);
    δα = α1.subtract(α0);
    System.out.println("δα=" + δα);
    δαδt = δα.divide(δt);
    System.out.println("δα/δt=" + δαδt);
  }

  private static BigDecimal α2d(final BigDecimal α)
  {
    return
      new BigDecimal(Math.exp(1 / α.doubleValue())).multiply(L_P).divide(α, MC);
  }

  private static boolean lessThanOrEqualTo(final BigDecimal a,
                                           final BigDecimal b)
  {
    return a.compareTo(b) <= 0;
  }

  private static boolean lessThan(final BigDecimal a, final BigDecimal b)
  {
    return a.compareTo(b) < 0;
  }

  private static boolean greaterThan(final BigDecimal a, final BigDecimal b)
  {
    return a.compareTo(b) > 0;
  }

  /**
   * Numerically approximate α for given d in equation
   *
   * l_p * (1 / α) * exp(1 / α) = d,
   *
   * starting with bounds [α₀/2, 2α₀].
   */
  private static BigDecimal d2α(final BigDecimal d)
  {
    System.out.println("d₁(α₁)=" + d);
    final BigDecimal ε = new BigDecimal(0.0000000000001);
    if (lessThanOrEqualTo(d, BigDecimal.ZERO)) {
      throw new IllegalArgumentException("d must be greater than 0");
    }
    BigDecimal lowerBound = α0.multiply(HALF);
    BigDecimal lowerValue = α2d(lowerBound);
    BigDecimal upperBound = α0.multiply(TWO);
    BigDecimal upperValue = α2d(upperBound);
    BigDecimal øBound = lowerBound.add(upperBound).multiply(HALF);
    while (greaterThan(upperBound,
                       ε.add(BigDecimal.ONE).multiply(lowerBound))) {
      final BigDecimal øValue = α2d(øBound);
      if (lessThan(øValue, d)) {
        upperBound = øBound;
        upperValue = øValue;
      } else {
        lowerBound = øBound;
        lowerValue = øValue;
      }
      øBound = lowerBound.add(upperBound).multiply(HALF);
    }
    System.out.println("d₁(ap)=" + α2d(øBound));
    return øBound;
  }

  public static void main(final String argv[])
  {
    final SolveExpEqBigDecimal solveExpEqBigDecimal =
      new SolveExpEqBigDecimal();
    for (final BigDecimal δt : new BigDecimal[] {
        new BigDecimal(1.0),
        new BigDecimal(100.0),
        new BigDecimal(10000.0),
        new BigDecimal(1000000.0),
        new BigDecimal(100000000.0)})
    {
      System.out.println("======== δt=" + δt + " ========");
      solveExpEqBigDecimal.compute(δt);
    }
  }
}
