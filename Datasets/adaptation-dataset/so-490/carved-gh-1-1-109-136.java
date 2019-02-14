public class foo{
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            if (defaultValue==null) {
                time="00:00";
            }
            else {
                time=defaultValue.toString();
            }
            if (shouldPersist()) {
                persistString(time);
            }
        }

        String[] timeParts=time.split(":");
        lastHour=Integer.parseInt(timeParts[0]);
        lastMinute=Integer.parseInt(timeParts[1]);;
    }
}