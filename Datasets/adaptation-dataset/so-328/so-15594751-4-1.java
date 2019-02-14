public class foo {
        public Point2D getIntersectionPoint(Line2D lineA, Line2D lineB) {

            double x1 = lineA.getX1();
            double y1 = lineA.getY1();
            double x2 = lineA.getX2();
            double y2 = lineA.getY2();

            double x3 = lineB.getX1();
            double y3 = lineB.getY1();
            double x4 = lineB.getX2();
            double y4 = lineB.getY2();

            Point2D p = null;

            double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
            if (d != 0) {
                double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
                double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

                p = new Point2D.Double(xi, yi);

            }
            return p;
        }
}