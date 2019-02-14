public class foo{
    /**
     * Wrapper of cursor that runs in another process should implement CrossProcessCursor
     * http://stackoverflow.com/questions/3976515/cursor-wrapping-unwrapping-in-contentprovider
     */
    @Override
    public void fillWindow(int position, CursorWindow window) {
        if (position < 0 || position > getCount()) {
            return;
        }
        window.acquireReference();
        try {
            moveToPosition(position - 1);
            window.clear();
            window.setStartPosition(position);
            int columnNum = getColumnCount();
            window.setNumColumns(columnNum);
            while (moveToNext() && window.allocRow()) {
                for (int i = 0; i < columnNum; i++) {
                    boolean putNull = true;
                    ;
//                    if (IDX_ICON_CACHE == i) {
//                        byte[] iconData = getBlob(IDX_ICON_CACHE);
//                        if (iconData != null && iconData.length > 0) {
//                            putNull = false;
//                            if (!window.putBlob(iconData, getPosition(), i)) {
//                                window.freeLastRow();
//                                break;
//                            }
//                        }
//                    } else {
                        String field = getString(i);
                        if (field != null) {
                            putNull = false;
                            if (!window.putString(field, getPosition(), i)) {
                                window.freeLastRow();
                                break;
                            }
                        }
//                    }
                    if (putNull) {
                        if (!window.putNull(getPosition(), i)) {
                            window.freeLastRow();
                            break;
                        }
                    }
                }
            }
        } catch (IllegalStateException e) {
            // simply ignore it
        } finally {
            window.releaseReference();
        }
    }
}