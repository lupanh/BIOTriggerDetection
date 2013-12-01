package edu.ktlab.evex.triggerdetection.featureselection;

public class FeatureOccurrenceCounter {

	protected double n11;

	protected double n10;

	protected double n01;

	protected double n00;

	protected double n;

	/**
	 * Constructor.
	 */
	public FeatureOccurrenceCounter() {
		n11 = 0.0;
		n10 = 0.0;
		n01 = 0.0;
		n00 = 0.0;
	}

	/**
	 * Constructor with respective counts.
	 * 
	 * @param n11
	 * @param n10
	 * @param n01
	 * @param n00
	 */
	public FeatureOccurrenceCounter(double n11, double n10, double n01, double n00) {
		this.n11 = n11;
		this.n10 = n10;
		this.n01 = n01;
		this.n00 = n00;
	}

	public void calculateSum() {
		n = n11 + n10 + n01 + n00;
	}

	public void incrementN11() {
		n11++;
	}

	public void incrementN10() {
		n10++;
	}

	public void incrementN01() {
		n01++;
	}

	public void incrementN00() {
		n00++;
	}

	public double getN11() {
		return n11;
	}

	public void setN11(double n11) {
		this.n11 = n11;
	}

	public double getN10() {
		return n10;
	}

	public void setN10(double n10) {
		this.n10 = n10;
	}

	public double getN01() {
		return n01;
	}

	public void setN01(double n01) {
		this.n01 = n01;
	}

	public double getN00() {
		return n00;
	}

	public void setN00(double n00) {
		this.n00 = n00;
	}

	public double getN() {
		calculateSum();
		return n;
	}

}
