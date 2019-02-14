public class foo{
	public ReachableObjects(Object object)
	{
		softlyReachable = new LinkedHashSet<Object>();
		weaklyReachable = new LinkedHashSet<Object>();
		phantomReachable = new LinkedHashSet<Object>();
		stronglyReachable = new LinkedHashSet<Object>();

		try
		{
			collectAllReachableObjects(object, stronglyReachable, "");
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		softlyReachable.removeAll(weaklyReachable);
		softlyReachable.removeAll(phantomReachable);
		softlyReachable.removeAll(stronglyReachable);

		weaklyReachable.removeAll(softlyReachable);
		weaklyReachable.removeAll(phantomReachable);
		weaklyReachable.removeAll(stronglyReachable);

		phantomReachable.removeAll(softlyReachable);
		phantomReachable.removeAll(weaklyReachable);
		phantomReachable.removeAll(stronglyReachable);
	}
}