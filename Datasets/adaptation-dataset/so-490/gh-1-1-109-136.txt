package com.basbrun;

//
// Code found here: http://stackoverflow.com/questions/6425702/layout-for-timepicker-based-dialogpreference
//

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private boolean is24HourFormat;
    private TimePicker picker=null;
    private TextView timeDisplay;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, 0);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        is24HourFormat = DateFormat.is24HourFormat(ctxt);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    public String toString() {
        if(is24HourFormat) {
            return ((lastHour < 10) ? "0" : "")
                    + Integer.toString(lastHour)
                    + ":" + ((lastMinute < 10) ? "0" : "")
                    + Integer.toString(lastMinute);
        } else {
            int myHour = lastHour % 12;
            return ((myHour == 0) ? "12" : ((myHour < 10) ? "0" : "") + Integer.toString(myHour))
                    + ":" + ((lastMinute < 10) ? "0" : "") 
                    + Integer.toString(lastMinute) 
                    + ((lastHour >= 12) ? " PM" : " AM");
        }
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext().getApplicationContext());
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(is24HourFormat);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected View onCreateView (ViewGroup parent) {
         View prefView = super.onCreateView(parent);
         LinearLayout layout = new LinearLayout(parent.getContext());
         LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT, 2);
         layout.addView(prefView, lp);
         timeDisplay = new TextView(parent.getContext());
         timeDisplay.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
         timeDisplay.setText(toString());
         LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT, 1);
         layout.addView(timeDisplay, lp2);
         return layout;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            picker.clearFocus();
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
                timeDisplay.setText(toString());
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

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

	@Override
	public void setDependency(String dependencyKey)
	{
		super.setDependency(dependencyKey);
	}

	@Override
	public String getDependency()
	{
		return super.getDependency();
	}
}