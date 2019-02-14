public class foo {
  private void initializePicker(final OnDateSetListener callback) {
    try {
      //If you're only using Honeycomb+ then you can just call getDatePicker() instead of using reflection
      Field pickerField = DatePickerDialog.class.getDeclaredField("mDatePicker");
      pickerField.setAccessible(true);
      final DatePicker picker = (DatePicker) pickerField.get(this);
      this.setCancelable(true);
      this.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getText(android.R.string.cancel), (OnClickListener) null);
      this.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getText(android.R.string.ok),
          new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                picker.clearFocus(); //Focus must be cleared so the value change listener is called
                callback.onDateSet(picker, picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
              }
          });
    } catch (Exception e) { /* Reflection probably failed*/ }
  }
}