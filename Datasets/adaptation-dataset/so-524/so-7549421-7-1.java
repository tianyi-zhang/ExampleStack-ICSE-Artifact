public class foo {
private static int partition(Array a, int left, int right) {
    int mid = mid(a, left, right);
    a.swap(right, mid);
    int i = left - 1;
    int j = right;

    while (true) {
        while (a.cmp(++i, right) < 0)
            ;
        while (a.cmp(right, --j) < 0)
            if (j == left) break;
        if (i >= j) break;
        a.swap(i, j);
    }
    a.swap(i, right);
    return i;
}
}