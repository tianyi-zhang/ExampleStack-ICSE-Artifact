public class foo{
	private void collectAllReachableObjects(Object from, Set<Object> result, String indent) throws IllegalArgumentException, IllegalAccessException
	{
		if(result.contains(from))
		{
			return;
		}
		result.add(from);
		Class<?> c = from.getClass();

		// Detect & loop through arrays:
		if(c.isArray())
		{
			int length = Array.getLength(from);
			for(int i = 0; i < length; i++)
			{
				Object value = Array.get(from, i);
				if(value != null)
					collectAllReachableObjects(value, result, indent);
			}
			return;
		}
		// else: Deal with non-arrays:
		Class<?> leafClass = c;
		while(c != null)
		{
			// System.out.println(indent+"Class "+c);

			Field fields[] = c.getDeclaredFields();
			for(Field field : fields)
			{
				// System.out.println(indent+"Field "+field+" of "+c);

				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}

				boolean wasAccessible = field.isAccessible();
				field.setAccessible(true);
				Object value = field.get(from);
				if(value != null)
				{
					Set<Object> nextResult = stronglyReachable;
					if(field.equals(REFERENCE_REFERENT_FIELD))
					{
						if(leafClass.equals(SoftReference.class))
						{
							nextResult = softlyReachable;
						}
						else if(leafClass.equals(WeakReference.class))
						{
							nextResult = weaklyReachable;
						}
						else if(leafClass.equals(PhantomReference.class))
						{
							nextResult = phantomReachable;
						}
					}
					collectAllReachableObjects(value, nextResult, indent + "  ");
				}
				field.setAccessible(wasAccessible);
			}
			c = c.getSuperclass();
		}
	}
}