public class foo {
private static int partition(float[] a, int[] index, 
int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
        while (less(a[++i], a[right]))      // find item on left to swap
            ;                               // a[right] acts as sentinel
        while (less(a[right], a[--j]))      // find item on right to swap
            if (j == left) break;           // don't go out-of-bounds
        if (i >= j) break;                  // check if pointers cross
        exch(a, index, i, j);               // swap two elements into place
    }
    exch(a, index, i, right);               // swap with partition element
    return i;
}
}