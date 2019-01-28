package be.krispypen.RaceAnalyzer.helper;

import be.krispypen.RaceAnalyzer.model.Point;
import be.krispypen.RaceAnalyzer.model.Trackpoint;

public class GForceUtil {

	public static java.awt.geom.Point2D.Double calculateGForce(Trackpoint tp) {
		double accellgforce = 0;
		double lateralgforce = 0;
		{ // calculate accell
			Trackpoint tpSecAgo = null;
			Trackpoint temp = tp;
			while (temp.getPrevious() != null && tpSecAgo == null) {
				if (temp.getPrevious().getTime() <= tp.getTime() - 1000) {
					tpSecAgo = temp.getPrevious();
				}
				temp = temp.getPrevious();
			}
			if (tpSecAgo != null) {
				accellgforce = (tp.getSpeed() - tpSecAgo.getSpeed()) * 1000 / 3600 / 9.8;
			}
		}
		{ // calculate lateral
			if (tp.getPrevious() != null && tp.getPrevious().getPrevious() != null && tp.getPrevious().getPrevious().getPrevious() != null
					&& tp.getPrevious().getPrevious().getPrevious().getPrevious() != null && tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious() != null
					&& tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious() != null
					&& tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious() != null
					&& tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious() != null
					&& tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious() != null
					&& tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious() != null) {
				Trackpoint point1 = tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious();
				Trackpoint point2 = tp.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious();
				Trackpoint point3 = tp;
				Point middle = GForceUtil.getMiddle(point1, point2, point3);
				boolean isGoingLeft = GForceUtil.isGoingLeft(point1, point2, point3);
				// formulas: http://www.regentsprep.org/Regents/math/geometry/GCG6/RCir.htm
				double radius = distFrom(middle.getLat(), middle.getLon(), point3.getLat(), point3.getLon());
				double asinFrom = (distFrom(point2.getLat(), point2.getLon(), point3.getLat(), point3.getLon()) / 2) / radius;
				double fullcircle = 2 * Math.PI;
				double circlepart = Math.asin(asinFrom) * 2 / fullcircle;
				double timeToCompleteCircle = (point3.getTime() - point2.getTime()) / circlepart;
				double timeToCompleteCirlceSeconds = timeToCompleteCircle / 1000;
				// 3.2908399: meter to feet
				// g = (1.225 X R / T2) see http://speeddirect.com/index.aspx?nodeID=176
				lateralgforce = (1.225 * radius * 3.2808399) / Math.pow(timeToCompleteCirlceSeconds, 2);
				if (isGoingLeft) {
					lateralgforce = lateralgforce * (-1);
				}
			}
		}
		return new java.awt.geom.Point2D.Double(lateralgforce, accellgforce);
	}

	/**
	 * formulas: http://www.regentsprep.org/Regents/math/geometry/GCG6/RCir.htm
	 * 
	 * @param point1
	 * @param point2
	 * @param point3
	 * @return
	 */
	public static Point getMiddle(Point point1, Point point2, Point point3) {
		Point middle = null;
		double r, t, x, y;
		r = (point1.getLon() - point2.getLon()) / (point1.getLat() - point2.getLat());
		t = (point3.getLon() - point2.getLon()) / (point3.getLat() - point2.getLat());
		x = ((r * t * (point3.getLon() - point1.getLon())) + (r * (point2.getLat() + point3.getLat())) - (t * (point1.getLat() + point2.getLat()))) / (2 * (r - t));
		y = (-1 / r) * (x - ((point1.getLat() + point2.getLat()) / 2)) + ((point1.getLon() + point2.getLon()) / 2);
		middle = new Point(x, y);
		return middle;
	}

	/**
	 * Check if the vehicle is going to left or right
	 * 
	 * @param point1
	 * @param point2
	 * @param point3
	 * @return
	 */
	public static boolean isGoingLeft(Point point1, Point point2, Point point3) {
		double r = (point1.getLon() - point2.getLon()) / (point1.getLat() - point2.getLat());
		double t = (point3.getLon() - point2.getLon()) / (point3.getLat() - point2.getLat());
		return r < t;
	}

	/**
	 * distance between 2 points, see http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return new Double(dist * meterConversion).floatValue();
	}

}
