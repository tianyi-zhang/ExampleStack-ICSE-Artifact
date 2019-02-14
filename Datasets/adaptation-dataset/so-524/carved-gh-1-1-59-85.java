public class foo{
  // partition a[left] to a[right], assumes left < right
  private static int partition(float[] a, int[] index, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      // find item on left to swap
      while (a[index[++i]] < a[index[right]])
        ; // a[right] acts as sentinel
      // find item on right to swap
      while (a[index[right]] < a[index[--j]]) {
        // don't go out-of-bounds
        if (j == left) {
          break;
        }
      }

      // check if pointers cross
      if (i >= j) {
        break;
      }

      swap(a, index, i, j); // swap two elements into place
    }

    swap(a, index, i, right); // swap with partition element
    return i;
  }
}