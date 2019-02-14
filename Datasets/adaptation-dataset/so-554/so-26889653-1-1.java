public class foo {
    @Override
    public float[] getNormalizedComponents(Object pixel, float[] normComponents, int normOffset) {
        int numComponents = getNumComponents();

        if (normComponents == null) {
            normComponents = new float[numComponents + normOffset];
        }

        switch (transferType) {
            case DataBuffer.TYPE_INT:
                int[] ipixel = (int[]) pixel;
                for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                    normComponents[nc] = ((float) (ipixel[c] & 0xffffffffl)) / ((float) ((1l << getComponentSize(c)) - 1));
                }
                break;
            default:
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
        }

        return normComponents;
    }
}