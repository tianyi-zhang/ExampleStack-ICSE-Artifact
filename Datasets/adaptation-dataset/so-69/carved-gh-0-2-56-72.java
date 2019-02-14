public class foo{
    // thanks to @commonsware https://stackoverflow.com/a/11417498
    public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(
            CharSequence original, Class<A> sourceType, SpanConverter<A, B> converter) {
        SpannableString result = new SpannableString(original);
        A[] spans = result.getSpans(0, result.length(), sourceType);

        for (A span : spans) {
            int start = result.getSpanStart(span);
            int end = result.getSpanEnd(span);
            int flags = result.getSpanFlags(span);

            result.removeSpan(span);
            result.setSpan(converter.convert(span), start, end, flags);
        }

        return (result);
    }
}