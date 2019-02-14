public class foo {
/** Perform action of waiting for a specific view id. */
public static ViewAction waitId(final int viewId, final long millis) {
    return new ViewAction() {
        @Override
        public Matcher<View> getConstraints() {
            return isRoot();
        }

        @Override
        public String getDescription() {
            return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            uiController.loopMainThreadUntilIdle();
            final long startTime = System.currentTimeMillis();
            final long endTime = startTime + millis;
            final Matcher<View> viewMatcher = withId(viewId);

            do {
                for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                    // found view with required ID
                    if (viewMatcher.matches(child)) {
                        return;
                    }
                }

                uiController.loopMainThreadForAtLeast(50);
            }
            while (System.currentTimeMillis() < endTime);

            // timeout happens
            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new TimeoutException())
                    .build();
        }
    };
}
}