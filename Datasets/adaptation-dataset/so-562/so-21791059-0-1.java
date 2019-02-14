public class foo {
public static Iterable<Integer> codePoints(final String string) {
  return new Iterable<Integer>() {
    public Iterator<Integer> iterator() {
      return new Iterator<Integer>() {
        int nextIndex = 0;
        public boolean hasNext() {
          return nextIndex < string.length();
        }
        public Integer next() {
          int result = string.codePointAt(nextIndex);
          nextIndex += Character.charCount(result);
          return result;
        }
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }
  };
}
}