public class foo{
    // Pulled from http://stackoverflow.com/a/30073528/681493
    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with " + childPosition + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup viewGroup = (ViewGroup) view.getParent();

                return parentMatcher.matches(view.getParent()) &&
                        viewGroup.getChildAt(childPosition).equals(view);
            }
        };
    }
}