public class foo{
        /**
         * Steps the iterator on the top of the stack to the
         * next element, and store this in {@link #current}.
         *
         * If this iterator is already the end, we recursively
         * step the iterator to the next element.
         *
         * If there are no more subsets at all, we set {@link #current}
         * to null.
         */
        void step() {
            Iterator<X> lastIt = stack.peek();
            current = current.next;
            while(!lastIt.hasNext()) {
                if(current == null) {
                    // no more elements in the first level iterator
                    // ==> no more subsets at all.
                    return;
                }

                // last iterator has no more elements
                // step iterator before and recreate last iterator.
                stack.pop();
                assert current != null;
                step();
                if(current == null) {
                    // after recursive call ==> at end of iteration.
                    return;
                }
                assert current != null;
                
                // new iterator at the top level
                lastIt = baseSet.iterator();
                if(!scrollTo(lastIt, current.element)) {
                    // Element not available anymore => some problem occured
                    current = null;
                    throw new ConcurrentModificationException
                        ("Element " + current.element + " not found!");
                }
                stack.push(lastIt);
            }
            // now we know the top iterator has more elements
            // ==> put the next one in `current`.

            X lastElement = lastIt.next();
            current = new Node(current, lastElement);

        }  // step()
}