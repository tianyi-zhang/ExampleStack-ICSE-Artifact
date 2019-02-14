public class foo{
    @Override
    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
        if (!inBounds((JInternalFrame) f, newX, newY, newWidth, newHeight)) {
            Container parent = f.getParent();
            Dimension parentSize = parent.getSize();

            if (parentSize.getWidth() < newWidth)
                newWidth = (int) parentSize.getWidth();

            if (parentSize.getHeight() < newHeight)
                newHeight = (int) parentSize.getHeight();

            int boundedX = (int) Math.max(0, Math.min(Math.max(0, newX), parentSize.getWidth() - newWidth));
            int boundedY = (int) Math.max(0, Math.min(Math.max(0, newY), parentSize.getHeight() - newHeight));

//            boundedX = (int) Math.max(0, parentSize.getWidth() - newWidth)
            f.setBounds(boundedX, boundedY, newWidth, newHeight);
        } else {
            f.setBounds(newX, newY, newWidth, newHeight);
        }
        if(didResize) {
            f.validate();
        }
    }
}