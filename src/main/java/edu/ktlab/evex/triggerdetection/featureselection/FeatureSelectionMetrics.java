package edu.ktlab.evex.triggerdetection.featureselection;

public class FeatureSelectionMetrics extends FeatureOccurrenceCounter {

	/** Mutual information score */
	private Double mi;

	/** Chi-square score */
	private Double chiSquare;

	/** Information gain score */
	private Double ig;

	/**
	 * Constructor.
	 */
	public FeatureSelectionMetrics() {
		super();
	}

	/**
	 * Constructor taking respective counts.
	 * 
	 * @param n11
	 * @param n10
	 * @param n01
	 * @param n00
	 */
	public FeatureSelectionMetrics(double n11, double n10, double n01, double n00) {
		super(n11, n10, n01, n00);
	}

	/**
	 * Calculates and returns the mutual information score.
	 * 
	 * @return the mutual information score
	 */
	public Double getMI() {
		calculateMutualInformation();
		return mi;
	}

	/**
	 * Calculates and returns the chi-square score.
	 * 
	 * @return the chi-square score
	 */
	public Double getChiSquare() {
		calculateChiSquare();
		return chiSquare;
	}

	/**
	 * Calculates and returns the information gain score.
	 * 
	 * @return the information gain score
	 */
	public Double getIG() {
		calculateInformationGain();
		return ig;
	}

	/**
	 * Calculates mutual information given the counts from n11 to n00. For more
	 * information, see (Manning et al., 2008).
	 */
	private void calculateMutualInformation() {
		if (n11 == 0 || n10 == 0 || n01 == 0 || n00 == 0) {
			// Boundary cases.
			mi = null;
			return;
		}

		calculateSum();
		double n1x = n10 + n11;
		double n0x = n00 + n01;
		double nx1 = n01 + n11;
		double nx0 = n00 + n10;

		mi = (n11 / n) * log2((n * n11) / (n1x * nx1)) + (n01 / n) * log2((n * n01) / (n0x * nx1))
				+ (n10 / n) * log2((n * n10) / (n1x * nx0)) + (n00 / n)
				* log2((n * n00) / (n0x * nx0));
	}

	/**
	 * Calculates the chi-square score given the counts from n11 to n00. In
	 * order to know statistical significance of the score, you can refer to the
	 * following relationship between the p value and chi-square score (Manning
	 * et al., 2008).
	 * 
	 * p value chi-square 0.1 2.71 0.05 3.84 0.01 6.63 0.005 7.88 0.001 10.83
	 */
	private void calculateChiSquare() {
		if (n11 + n01 == 0 || n11 + n10 == 0 || n10 + n00 == 0 || n01 + n00 == 0) {
			// Boundary cases.
			chiSquare = null;
			return;
		}

		calculateSum();
		// An arithmetically simpler way of computing chi-square.
		chiSquare = ((n11 + n10 + n01 + n00) * (n11 * n00 - n10 * n01) * (n11 * n00 - n10 * n01))
				/ ((n11 + n01) * (n11 + n10) * (n10 + n00) * (n01 + n00));
	}

	/**
	 * Calculates the information gain score given the counts from n11 to n00.
	 * For more information, see (Forman et al., 2003).
	 */
	private void calculateInformationGain() {
		if (n11 == 0 || n10 == 0 || n01 == 0 || n00 == 0) {
			// Boundary cases.
			ig = null;
			return;
		}

		calculateSum();
		double n1x = n10 + n11;
		double n0x = n00 + n01;
		double nx1 = n01 + n11;
		double nx0 = n00 + n10;

		ig = (n11 / n) * Math.log((n11 / n) / ((n11 / nx1) * (n11 / n1x))) + (n10 / n)
				* Math.log((n10 / n) / ((n10 / nx0) * (n10 / n1x))) + (n01 / n)
				* Math.log((n01 / n) / ((n01 / nx1) * (n01 / n0x))) + (n00 / n)
				* Math.log((n00 / n) / ((n00 / nx0) * (n00 / n0x)));
	}

	private double log2(double value) {
		return (Math.log(value) / Math.log(2));
	}

	/**
	 * A simple tester with a couple of examples.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FeatureSelectionMetrics fsm1 = new FeatureSelectionMetrics(49, 141, 27652, 774106);
		Double mi1 = fsm1.getMI();
		Double chiSquare1 = fsm1.getChiSquare();
		Double ig1 = fsm1.getIG();

		FeatureSelectionMetrics fsm2 = new FeatureSelectionMetrics(0, 4, 0, 164);
		Double mi2 = fsm2.getMI();
		Double chiSquare2 = fsm2.getChiSquare();
		Double ig2 = fsm2.getIG();

		System.out.println("mi1: " + mi1); // Should be approximately 0.0001105
		System.out.println("chiSquare1: " + chiSquare1); // Should be
															// approximately 284
		System.out.println("ig1: " + ig1);

		// The scores below should be undefined (null) due to boundary cases.
		System.out.println("mi2: " + mi2);
		System.out.println("chiSquare2: " + chiSquare2);
		System.out.println("ig2: " + ig2);
	}

}
