package org.soundpaint.dstx;

/**
 * Given the function
 *
 * f(α) := l_p * (1 / α) * exp(1/ α) = d
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
public class SolveExpEqDouble
{
  private static final double α0 = 7.29735256e-3; // fine-struct. constant now
  private static final double L_P = 1.616e-35; // [m] Plank Length
  private static final double D_FOR_α0 = α2d(α0); // [m]
  private static final double AGE = 13.81e9; // [a]
  private static final double EXP_NOW = 7.379e26; // [m] ≈ 78e9 LY
  private static final double EXP_RATE = EXP_NOW / AGE; // [m/a]

  private double δt; // [a] delta time (years) to use for δ/δt approximation
  private double EXP_THEN; // [m] expected future expansion
  private double EXP_REL; // [1] expansion rate
  private double D_FOR_α1; // [m]
  private double α1; // [1] future fine-structure constant
  private double δα; // [1] fine-structure constants delta
  private double δαδt; // [1/a] fine-structure constant drift per year

  private void compute(final double δt)
  {
    this.δt = δt;
    EXP_THEN = EXP_NOW + EXP_RATE * δt;
    EXP_REL = EXP_THEN / EXP_NOW;
    System.out.println("EXP_REL=" + EXP_REL);
    D_FOR_α1 = D_FOR_α0 * EXP_REL;
    System.out.println("d(α₀)=" + D_FOR_α0);
    System.out.println("d(α₁)=" + D_FOR_α1);
    α1 = d2α(D_FOR_α1);
    System.out.println("α₀=" + α0);
    System.out.println("α₁=" + α1);
    δα = α1 - α0;
    System.out.println("δα=" + δα);
    δαδt = δα / δt;
    System.out.println("δα/δt=" + δαδt);
  }

  private static double α2d(final double α)
  {
    return L_P * (1 / α) * Math.exp(1 / α);
  }

  /**
   * Numerically approximate α for given d in equation
   *
   * l_p * (1 / α) * exp(1 / α) = d,
   *
   * starting with bounds [α₀/2, 2α₀].
   */
  private static double d2α(final double d)
  {
    System.out.println("d₁(α₁)=" + d);
    final double ε = 0.0000000000001;
    if (d <= 0) {
      throw new IllegalArgumentException("d must be greater than 0");
    }
    double lowerBound = α0 * 0.5;
    double lowerValue = α2d(lowerBound);
    double upperBound = α0 * 2.0;
    double upperValue = α2d(upperBound);
    double øBound = 0.5 * (lowerBound + upperBound);
    while (upperBound / lowerBound > 1.0 + ε) {
      final double øValue = α2d(øBound);
      if (øValue < d) {
        upperBound = øBound;
        upperValue = øValue;
      } else {
        lowerBound = øBound;
        lowerValue = øValue;
      }
      øBound = 0.5 * (lowerBound + upperBound);
    }
    System.out.println("d₁(ap)=" + α2d(øBound));
    return øBound;
  }

  public static void main(final String argv[])
  {
    final SolveExpEqDouble solveExpEqDouble = new SolveExpEqDouble();
    for (final double δt : new double[] {
        1.0,
        100.0,
        10000.0,
        1000000.0,
        100000000.0})
    {
      System.out.println("======== δt=" + δt + " ========");
      solveExpEqDouble.compute(δt);
    }
  }
}
