public class foo{
    /**
     * Calculate the rough distance in meters between two points
     * taken from http://stackoverflow.com/questions/120283/working-with-latitude-longitude-values-in-java
     * @param lat1 
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius =  6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }
}