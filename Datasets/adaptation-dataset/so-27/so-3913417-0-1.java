public class foo {
    /**
     * Singleton contructor, needed to get access to the application context & strings for i18n
     * @param context Context
     * @return DateTimeUtils singleton instanec
     * @throws Exception
     */
     public static DateTimeUtils getInstance(Context context) {
         mCtx = context;
         if (instance == null) {
             instance = new DateTimeUtils();
             mTimestampLabelYesterday = context.getResources().getString(R.string.WidgetProvider_timestamp_yesterday);
             mTimestampLabelToday = context.getResources().getString(R.string.WidgetProvider_timestamp_today);
             mTimestampLabelJustNow = context.getResources().getString(R.string.WidgetProvider_timestamp_just_now);
             mTimestampLabelMinutesAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_minutes_ago);
             mTimestampLabelHoursAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_hours_ago);
             mTimestampLabelHourAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_hour_ago);
         }
         return instance;
     }
}