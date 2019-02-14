public class foo{
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
        int columnNum=getColumnCount();
        window.setNumColumns(columnNum);
        while (moveToNext() && window.allocRow()) {
          for (int i=0; i < columnNum; i++) {
            String field=getString(i);
            if (field != null) {
              if (!window.putString(field, getPosition(), i)) {
                window.freeLastRow();
                break;
              }
            }
            else {
              if (!window.putNull(getPosition(), i)) {
                window.freeLastRow();
                break;
              }
            }
          }
        }
      }
      catch (IllegalStateException e) {
        // simply ignore it
      }
      finally {
        window.releaseReference();
      }
    }
}