public class foo{
	/**
	 * From http://stackoverflow.com/questions/120283/working-with-latitude-longitude-values-in-java
	 * All params are in radians
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double computeDistanceInMiles(double lat1, double lng1,
			double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = lat2-lat1;
		double dLng = lng2-lng1;
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		return dist;
	}
}