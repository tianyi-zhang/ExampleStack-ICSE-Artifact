public class foo{
  public static ViewAction setDate(final int year, final int monthOfYear, final int dayOfMonth) {
    return new ViewAction() {
      @Override
      public void perform(UiController uiController, View view) {
        final DayPickerView dayPickerView = (DayPickerView) view;

        try {
          Field field = DayPickerView.class.getDeclaredField("mController");
          field.setAccessible(true);
          DatePickerController controller = (DatePickerController) field.get(dayPickerView);
          controller.onDayOfMonthSelected(year, monthOfYear, dayOfMonth);
        } catch (Exception e) {
          Timber.e(e);
        }
      }

      @Override
      public String getDescription() {
        return "set date";
      }

      @Override
      public Matcher<View> getConstraints() {
        return allOf(isAssignableFrom(DayPickerView.class), isDisplayed());
      }
    };
  }
}