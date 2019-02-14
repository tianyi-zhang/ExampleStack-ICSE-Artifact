public class foo{
    public String getTimes() {
        StringBuilder res = new StringBuilder();
        long duration = System.currentTimeMillis() - time;
        long temp = 0;
        if (duration <= ONE_SECOND)
            return "0 second";

        temp = duration / ONE_DAY;
        if (temp == 1)
            return "day";
        else if (temp > 0)
            return res.append(temp).append(" day").append(temp > 1 ? "s" : "").toString();

        temp = duration / ONE_HOUR;
        if (temp == 1)
            return "hour";
        else if (temp > 0)
            return res.append(temp).append(" hour").append(temp > 1 ? "s" : "").toString();

        temp = duration / ONE_MINUTE;
        if (temp == 1)
            return "minute";
        else if (temp > 0)
            return res.append(temp).append(" minute").append(temp > 1 ? "s" : "").toString();

        temp = duration / ONE_SECOND;
        if (temp == 1)
            return "second";
        else if (temp > 0)
            return res.append(temp).append(" second").append(temp > 1 ? "s" : "").toString();
        else
            return "";
    }
}