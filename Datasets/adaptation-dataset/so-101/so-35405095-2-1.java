public class foo {
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) dependency;
        }

        final boolean result = super.onDependentViewChanged(parent, child, dependency);
        final int bottomPadding = calculateBottomPadding(appBarLayout);
        final boolean paddingChanged = bottomPadding != child.getPaddingBottom();
        if (paddingChanged) {
            child.setPadding(
                child.getPaddingLeft(),
                child.getPaddingTop(),
                child.getPaddingRight(),
                bottomPadding);
            child.requestLayout();
        }
        return paddingChanged || result;
    }
}