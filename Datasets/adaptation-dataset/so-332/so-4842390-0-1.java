public class foo {
       /**
        * Constructor.
        *
        * @param context The context to operate in.
        * @param attrs The attributes defined in XML for this element.
        */
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = null;

        // Cache the check box drawable.
        typedArray = context.getTheme().obtainStyledAttributes(new int[] {android.R.attr.listChoiceIndicatorMultiple});

        if ((typedArray != null) && (typedArray.length() > 0)) {
            mCheckDrawable = typedArray.getDrawable(0);
        }
        else {
            // Fallback if the target theme doesn't define a check box drawable.
            // Perhaps an application specific drawable should be used instead of null.
            mCheckDrawable = null;
        }

        // Careful with resources like this, we don't need any memory leaks.
        typedArray.recycle();

        // Cache the radio button drawable.
        typedArray = context.getTheme().obtainStyledAttributes(new int[] {android.R.attr.listChoiceIndicatorSingle});

        if ((typedArray != null) && (typedArray.length() > 0)) {
            mRadioDrawable = typedArray.getDrawable(0);
        }
        else {
            // Fallback if the target theme doesn't define a radio button drawable.
            // Perhaps an application specific drawable should be used instead of null
            mRadioDrawable = null;
        }

        // Careful with resources like this, we don't need any memory leaks.
        typedArray.recycle();

        mIsChecked = false;
    }
}