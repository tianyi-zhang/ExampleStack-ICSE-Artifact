public class foo {
    /**
     * Displays a user-friendly date difference string
     * @param timedate Timestamp to format as date difference from now
     * @return Friendly-formatted date diff string
     */
    public String getTimeDiffString(long timedate) {
        Calendar startDateTime = Calendar.getInstance();
        Calendar endDateTime = Calendar.getInstance();
        endDateTime.setTimeInMillis(timedate);
        long milliseconds1 = startDateTime.getTimeInMillis();
        long milliseconds2 = endDateTime.getTimeInMillis();
        long diff = milliseconds1 - milliseconds2;

        long hours = diff / (60 * 60 * 1000);
        long minutes = diff / (60 * 1000);
        minutes = minutes - 60 * hours;
        long seconds = diff / (1000);

        boolean isToday = DateTimeUtils.isToday(timedate);
        boolean isYesterday = DateTimeUtils.isYesterday(timedate);

        if (hours > 0 && hours < 12) {
            return hours==1? String.format(mTimestampLabelHourAgo,hours) : String.format(mTimestampLabelHoursAgo,hours);
        } else if (hours <= 0) {
            if (minutes > 0)
                return String.format(mTimestampLabelMinutesAgo,minutes);
            else {
                return mTimestampLabelJustNow;
            }
        } else if (isToday) {
            return mTimestampLabelToday;
        } else if (isYesterday) {
            return mTimestampLabelYesterday;
        } else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
            return weekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
        } else {
            return formatDateTime(mCtx, timedate, DateUtils.FORMAT_NUMERIC_DATE);
        }
    }
}