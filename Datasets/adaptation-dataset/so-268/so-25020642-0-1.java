public class foo {
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor source = super.query(uri, projection, selection, selectionArgs, sortOrder);

        String[] columnNames = source.getColumnNames();
        String[] newColumnNames = columnNamesWithData(columnNames);
        MatrixCursor cursor = new MatrixCursor(newColumnNames, source.getCount());

        source.moveToPosition(-1);
        while (source.moveToNext()) {
            MatrixCursor.RowBuilder row = cursor.newRow();
            for (int i = 0; i < columnNames.length; i++) {
                row.add(source.getString(i));
            }
        }

        return cursor;
    }
}