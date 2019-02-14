public class foo {
private int efficientTextSizeSearch(int start, int end,
        SizeTester sizeTester, RectF availableSpace) {
    if (!mEnableSizeCache) {
        return binarySearch(start, end, sizeTester, availableSpace);
    }
    String text = getText().toString();
    int key = text == null ? 0 : text.length();
    int size = mTextCachedSizes.get(key);
    if (size != 0) {
        return size;
    }
    size = binarySearch(start, end, sizeTester, availableSpace);
    mTextCachedSizes.put(key, size);
    return size;
}
}