public class foo {
  @Override
  public CharSequence terminateToken(CharSequence text) {
    int i = text.length();

    while (i > 0 && text.charAt(i - 1) == ' ') {
      i--;
    }

    if (i > 0 && text.charAt(i - 1) == ' ') {
      return text;
    } else {
      if (text instanceof Spanned) {
        SpannableString sp = new SpannableString(text + " ");
        TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
        return sp;
      } else {
        return text + " ";
      }
    }
  }
}