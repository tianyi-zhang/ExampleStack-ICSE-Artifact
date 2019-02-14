public class foo{
	@Override
	public CharSequence terminateToken (final CharSequence text) {
		int i = text.length();
		while (i > 0 && text.charAt(i - 1) == ' ') {
			i--;
		}
		if (i > 0 && text.charAt(i - 1) == ' ') return text;
		if (text instanceof Spanned) {
			final SpannableString sp = new SpannableString(text + " ");
			TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
			return sp;
		}
		return text + " ";
	}
}