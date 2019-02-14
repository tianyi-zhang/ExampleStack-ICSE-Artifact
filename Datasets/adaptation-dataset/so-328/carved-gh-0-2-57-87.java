public class foo{
	/**
	 * from https://stackoverflow.com/questions/15594424/line-crosses-rectangle-how-to-find-the-cross-points/15594751#15594751
	 */
	public static PointDouble getIntersectionPoint(Line lineA, Line lineB) {
		double x1 = lineA.getStart().getX();
		double y1 = lineA.getStart().getY();
		double x2 = lineA.getEnd().getX();
		double y2 = lineA.getEnd().getY();

		double x3 = lineB.getStart().getX();
		double y3 = lineB.getStart().getY();
		double x4 = lineB.getEnd().getX();
		double y4 = lineB.getEnd().getY();

		PointDouble p = null;

		double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (d != 0) {
			double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
			double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

			p = new PointDouble(xi, yi);

			// remove intersections which are not on the line (use 1 instead of 0 distance for tolerance)
			if (lineA.getDistanceToPoint(p) > 1 || lineB.getDistanceToPoint(p) > 1) {
				p = null;
			}
		}

		return p;
	}
}