public class foo{
    /**
     * Attempts to clear the decor view from the transition manager which causes a leak.
     * <p/><a href="http://stackoverflow.com/questions/32698049/sharedelement-and-custom-entertransition-causes-memory-leak">StackOverflow
     * Explanation</a>
     *
     * @param activity
     */
    public static void fixTransitionLeak(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Class transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            if (map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (NoSuchFieldException e) {
            // Nothing
        } catch (IllegalAccessException e) {
            // Nothing
        }
    }
}