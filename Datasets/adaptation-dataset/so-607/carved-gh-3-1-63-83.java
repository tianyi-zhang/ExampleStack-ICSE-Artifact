public class foo{
	public VerticalLabelView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "VerticalLabelView(Context mContext, AttributeSet attrs) called");
		initLabelView();

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalLabelView);

		final CharSequence s = a.getString(R.styleable.VerticalLabelView_text);
		if (s != null) {
			setText(s.toString());
		}

		setTextColor(a.getColor(R.styleable.VerticalLabelView_textColor, DEFAULT_COLOR));

		final int textSize = a.getDimensionPixelOffset(R.styleable.VerticalLabelView_textSize, 0);
		if (textSize > 0) {
			setTextSize(textSize);
		}

		a.recycle();
	}
}