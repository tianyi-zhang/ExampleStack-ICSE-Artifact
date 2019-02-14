public class foo{
    // From http://stackoverflow.com/a/5759318
    private Intent assertResultEquals(int resultCode) {
        try {
            Field f = Activity.class.getDeclaredField("mResultCode");
            f.setAccessible(true);
            int actualResultCode = (Integer)f.get(welcomeActivity);
            assertThat(actualResultCode, is(resultCode));
            f = Activity.class.getDeclaredField("mResultData");
            f.setAccessible(true);
            return (Intent)f.get(welcomeActivity);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Looks like the Android Activity class has changed it's   private fields for mResultCode or mResultData.  Time to update the reflection code.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}