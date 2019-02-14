public class foo {
    /**
     * Returns a {@link ViewAction} that sets a time on a {@link TimePicker}.
     */
    public static ViewAction setTime(final int hours, final int minutes) {

        return new ViewAction() {

            @Override
            public void perform(UiController uiController, View view) {
                final RadialPickerLayout timePicker = (RadialPickerLayout) view;

                timePicker.setTime(new Timepoint(hours, minutes, 0));
            }

            @Override
            public String getDescription() {
                return "set time";
            }

            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(RadialPickerLayout.class), isDisplayed());
            }
        };

    }
}