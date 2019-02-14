public class foo {
private void applySpacing() {
    SpannableString finalText;

    if (!(originalText instanceof SpannableString)) {
        if (this.originalText == null) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        finalText = new SpannableString(builder.toString());
    } else {
        finalText = (SpannableString) originalText;
    }

    for (int i = 1; i < finalText.length(); i += 2) {
        finalText.setSpan(new ScaleXSpan((spacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    super.setText(finalText, TextView.BufferType.SPANNABLE);
}
}