public class foo {
@Override
public View onCreateView(String name, Context context, AttributeSet attrs) {
    // Allow super to try and create a view first
    final View result = super.onCreateView(name, context, attrs);
    if (result != null) {
        return result;
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
        // standard framework versions
        switch (name) {
            case "EditText":
                return new AppCompatEditText(this, attrs);
            case "Spinner":
                return new AppCompatSpinner(this, attrs);
            case "CheckBox":
                return new AppCompatCheckBox(this, attrs);
            case "RadioButton":
                return new AppCompatRadioButton(this, attrs);
            case "CheckedTextView":
                return new AppCompatCheckedTextView(this, attrs);
        }
    }

    return null;
}
}