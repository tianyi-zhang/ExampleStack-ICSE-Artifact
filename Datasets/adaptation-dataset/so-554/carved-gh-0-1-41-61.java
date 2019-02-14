public class foo{
	@Override
	public float[] getNormalizedComponents(Object pixel, float[] normComponents, int normOffset) {
		int numComponents = getNumComponents();
		
		if (normComponents == null || normComponents.length < numComponents + normOffset) {
			normComponents = new float[numComponents + normOffset];
		}

		switch (transferType) {
			case DataBuffer.TYPE_INT:
				int[] ipixel = (int[]) pixel;
				for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
					normComponents[nc] = ipixel[c] / ((float) ((1L << getComponentSize(c)) - 1));
				}
				break;
			default: // I don't think we can ever come this far. Just in case!!!
				throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
        }

        return normComponents;
    }
}