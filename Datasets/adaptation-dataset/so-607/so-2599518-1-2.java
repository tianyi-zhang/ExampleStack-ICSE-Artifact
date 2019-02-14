public class foo {
    public VerticalLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLabelView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalLabelView);

        CharSequence s = a.getString(R.styleable.VerticalLabelView_text);
        if (s != null) setText(s.toString());

        setTextColor(a.getColor(R.styleable.VerticalLabelView_textColor, 0xFF000000));

        int textSize = a.getDimensionPixelOffset(R.styleable.VerticalLabelView_textSize, 0);
        if (textSize > 0) setTextSize(textSize);

        a.recycle();
    }
}