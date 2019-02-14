public class foo{
	private void initializePicker(final OnTimeSetListener callback)
	{
		try
		{
			// if you're only using Honeycomb+ then you can just call getTimePicker() instead of using reflection
			java.lang.reflect.Field pickerField = TimePickerDialog.class.getDeclaredField("mTimePicker");
			pickerField.setAccessible(true);
			final TimePicker picker = (TimePicker) pickerField.get(this);
			this.setCancelable(true);
			this.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getText(android.R.string.cancel), (OnClickListener) null);
			this.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getText(android.R.string.ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					picker.clearFocus(); // focus must be cleared so the value change listener is called
					callback.onTimeSet(picker, picker.getCurrentHour(), picker.getCurrentMinute());
				}
			});
		}
		catch(Exception e)
		{
			// reflection probably failed
		}
	}
}