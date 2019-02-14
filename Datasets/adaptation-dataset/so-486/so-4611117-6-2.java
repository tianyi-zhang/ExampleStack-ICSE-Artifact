public class foo {
       private Dimension computeMinSize(Container target) {
          synchronized (target.getTreeLock()) {
             int minx = Integer.MAX_VALUE;
             int miny = Integer.MIN_VALUE;
             boolean found_one = false;
             int n = target.getComponentCount();

             for (int i = 0; i < n; i++) {
                Component c = target.getComponent(i);
                if (c.isVisible()) {
                   found_one = true;
                   Dimension d = c.getPreferredSize();
                   minx = Math.min(minx, d.width);
                   miny = Math.min(miny, d.height);
                }
             }
             if (found_one) {
                return new Dimension(minx, miny);
             }
             return new Dimension(0, 0);
          }
       }
}