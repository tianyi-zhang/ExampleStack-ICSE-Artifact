public class foo{
    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final Cursor source = super.query(uri, projection, selection, selectionArgs, sortOrder);

        /*
            It's not necessary to overwrite
                public Cursor query(final Uri uri, final String[] projection, final String selection, final String[]
                    selectionArgs, final String sortOrder, final CancellationSignal cancellationSignal);
            cause it calls this method.

            == What's happening here? ==
            Some apps rely on the MediaStore.MediaColumns.DATA column in the cursor. To prevent them
            from crashing, we have to copy the initial cursor and add the MediaStore.MediaColumns.DATA
            column. The values of this column will always be null, but we assume that they are able
            to handle that case.
        */

        if(source == null) {
            Log.w(LOG_TAG, "Not able to apply workaround, super.query(...) returned null");
            return null;
        }

        String[] columnNames = source.getColumnNames();
        String[] newColumnNames = columnNamesWithData(columnNames);

        final MatrixCursor cursor = new MatrixCursor(newColumnNames, source.getCount());

        source.moveToPosition(-1);
        while (source.moveToNext()) {
            MatrixCursor.RowBuilder row = cursor.newRow();
            for (int i = 0; i < columnNames.length; i++) {
                row.add(source.getString(i));
            }
        }
        source.close();

        return cursor;
    }
}