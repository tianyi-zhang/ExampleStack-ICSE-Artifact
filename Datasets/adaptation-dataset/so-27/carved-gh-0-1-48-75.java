public class foo{
    /**
     * Singleton contructor. needed to get access to the application context &
     * strings for i18n
     *
     * @param context
     *            Context
     * @return DateTimeUtils singleton instanec
     * @throws Exception
     */
    public static DateTimeUtils getInstance(Context context) {
        sCtx = context;
        if (sInstance == null) {
            sInstance = new DateTimeUtils();
            sTimestampLabelYesterday = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_yesterday);
            sTimestampLabelToday = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_today);
            sTimestampLabelJustNow = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_just_now);
            sTimestampLabelMinutesAgo = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_minutes_ago);
            sTimestampLabelHoursAgo = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_hours_ago);
            sTimestampLabelHourAgo = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_hour_ago);
        }
        return sInstance;
    }
}