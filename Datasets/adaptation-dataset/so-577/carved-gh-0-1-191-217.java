public class foo{
    /**
     * Compat method so we can get type of column on API < 11.
     * Source: http://stackoverflow.com/a/20685546/2643666
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static int getColumnType(Cursor cursor, int col) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
            CursorWindow cursorWindow = sqLiteCursor.getWindow();
            int pos = cursor.getPosition();
            int type = -1;
            if (cursorWindow.isNull(pos, col)) {
                type = FIELD_TYPE_NULL;
            } else if (cursorWindow.isLong(pos, col)) {
                type = FIELD_TYPE_INTEGER;
            } else if (cursorWindow.isFloat(pos, col)) {
                type = FIELD_TYPE_FLOAT;
            } else if (cursorWindow.isString(pos, col)) {
                type = FIELD_TYPE_STRING;
            } else if (cursorWindow.isBlob(pos, col)) {
                type = FIELD_TYPE_BLOB;
            }
            return type;
        } else {
            return cursor.getType(col);
        }
    }
}