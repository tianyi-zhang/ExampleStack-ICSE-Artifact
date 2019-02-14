public class foo{
	/**
	 * Code snippet: http://stackoverflow.com/questions/2171074/generating-a-probability-distribution Returns an array of
	 * size "n" with probabilities between 0 and 1 such that sum(array) = 1.
	 */
	private static double[] getProbabilityDistribution(int n, Random randomGenerator) {
		double a[] = new double[n];
		double s = 0.0d;
		for(int i = 0; i < n; i++) {
			a[i] = 1.0d - randomGenerator.nextDouble();
			a[i] = -1 * Math.log(a[i]);
			s += a[i];
		}
		for(int i = 0; i < n; i++) {
			a[i] /= s;
		}
		return a;
	}
}