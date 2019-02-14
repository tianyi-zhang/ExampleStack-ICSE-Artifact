public class foo {
/**
     * Returns a {@link ViewAction} that sets a date on a {@link DatePicker}.
     */
    public static ViewAction setDate(final int year, final int monthOfYear, final int dayOfMonth) {

        return new ViewAction() {

            @Override
            public void perform(UiController uiController, View view) {
                final DayPickerView dayPickerView = (DayPickerView) view;

                try {
                    Field f = null; //NoSuchFieldException
                    f = DayPickerView.class.getDeclaredField("mController");
                    f.setAccessible(true);
                    DatePickerController controller = (DatePickerController) f.get(dayPickerView); //IllegalAccessException
                    controller.onDayOfMonthSelected(year, monthOfYear, dayOfMonth);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String getDescription() {
                return "set date";
            }

            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(DayPickerView.class), isDisplayed());
            }
        };

    }
}