public class foo {
public static double HaverSineDistance(double lat1, double lng1, double lat2, double lng2) 
{
    // mHager 08-12-2012
    // http://en.wikipedia.org/wiki/Haversine_formula
    // Implementation

    // convert to radians
    lat1 = Math.toRadians(lat1);
    lng1 = Math.toRadians(lng1);
    lat2 = Math.toRadians(lat2);
    lng2 = Math.toRadians(lng2);

    double dlon = lng2 - lng1;
    double dlat = lat2 - lat1;

    double a = Math.pow((Math.sin(dlat/2)),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2),2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    return EARTH_RADIUS * c;
}
}