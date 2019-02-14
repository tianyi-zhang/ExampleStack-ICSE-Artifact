public class foo {
      /**
       * converts time (in milliseconds) to human-readable format
       *  "<w> days, <x> hours, <y> minutes and (z) seconds"
       */
      public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
          temp = duration / ONE_DAY;
          if (temp > 0) {
            duration -= temp * ONE_DAY;
            res.append(temp).append(" day").append(temp > 1 ? "s" : "")
               .append(duration >= ONE_MINUTE ? ", " : "");
          }

          temp = duration / ONE_HOUR;
          if (temp > 0) {
            duration -= temp * ONE_HOUR;
            res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
               .append(duration >= ONE_MINUTE ? ", " : "");
          }

          temp = duration / ONE_MINUTE;
          if (temp > 0) {
            duration -= temp * ONE_MINUTE;
            res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
          }

          if (!res.toString().equals("") && duration >= ONE_SECOND) {
            res.append(" and ");
          }

          temp = duration / ONE_SECOND;
          if (temp > 0) {
            res.append(temp).append(" second").append(temp > 1 ? "s" : "");
          }
          return res.toString();
        } else {
          return "0 second";
        }
      }
}