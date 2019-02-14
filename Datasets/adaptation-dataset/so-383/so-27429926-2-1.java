public class foo {
  @Override
  protected void showDialog(Bundle state) {
    mBuilder = new MaterialDialog.Builder(context);
    mBuilder.title(getTitle());
    mBuilder.icon(getDialogIcon());
    mBuilder.positiveText(null);
    mBuilder.negativeText(getNegativeButtonText());
    mBuilder.items(getEntries());
    mBuilder.itemsCallback(new MaterialDialog.ListCallback() {
      @Override
      public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
        onClick(null, DialogInterface.BUTTON_POSITIVE);
        dialog.dismiss();

        if (which >= 0 && getEntryValues() != null) {
          String value = getEntryValues()[which].toString();
          if (callChangeListener(value))
            setValue(value);
        }
      }
    });

    final View contentView = onCreateDialogView();
    if (contentView != null) {
      onBindDialogView(contentView);
      mBuilder.customView(contentView);
    }
    else
      mBuilder.content(getDialogMessage());

    mBuilder.show();
  }
}