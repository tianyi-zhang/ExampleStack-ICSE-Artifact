public class foo{
	/**
	 * Checks whether the polygon contains the specified point.<br>
	 * <br>
	 * StackOverflow magic: http://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
	 * 
	 * @return true if the polygon contains the point, false otherwise
	 */
	public boolean contains(int x, int y) {
		boolean result = false;
		for (int i = 0, j = vertices - 1; i < vertices; j = i++) {
			if ((points[i * 2 + 1] > y) != (points[j * 2 + 1] > y) &&
					(x < (points[j * 2] - points[i * 2]) * (y - points[i * 2 + 1]) / (points[j * 2 + 1] - points[i * 2 + 1]) + points[i * 2])) {
				result = !result;
			}
		}
		return result;
	}
}