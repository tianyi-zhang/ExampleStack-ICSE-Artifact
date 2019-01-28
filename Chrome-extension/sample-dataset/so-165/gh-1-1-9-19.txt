package com.logicalpractice.geofun.search;

/**
 *
 */
public class Points {

  // http://stackoverflow.com/a/837957/31480
  public static int distFrom(float lat1, float lng1, float lat2, float lng2) {
    double earthRadius = 6371; //kilometers
    double dLat = Math.toRadians(lat2-lat1);
    double dLng = Math.toRadians(lng2-lng1);
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    return Math.abs((int) (earthRadius * c));
  }
}
