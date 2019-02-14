public class foo{
    /**
     * Set Activity-s locale to SharedPreferences-setting.
     * Must be called before
     */
    public static void fixLocale(Context context)
    {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String language = prefs.getString(Global.PREF_KEY_USER_LOCALE, "");
        Locale locale = Global.systemLocale; // in case that setting=="use android-locale"
        if ((language != null) && (!language.isEmpty())) {
            locale = new Locale(language); // overwrite "use android-locale"
        }

        if (locale != null) {
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            Resources resources = context.getResources();
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            // recreate();

            if (context instanceof LocalizedActivity) {
                ((LocalizedActivity) context).myLocale = locale;
            }
        }
    }
}