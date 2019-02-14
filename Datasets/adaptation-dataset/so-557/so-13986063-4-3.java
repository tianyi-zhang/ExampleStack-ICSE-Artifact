public class foo {
                /**
                 * @return file dialog
                 */
                public Dialog createFileDialog() {
                    Dialog dialog = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle(currentPath.getPath());
                    if (selectDirectoryOption) {
                        builder.setPositiveButton("Select directory", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, currentPath.getPath());
                                fireDirectorySelectedEvent(currentPath);
                            }
                        });
                    }

                    builder.setItems(fileList, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String fileChosen = fileList[which];
                            File chosenFile = getChosenFile(fileChosen);
                            if (chosenFile.isDirectory()) {
                                loadFileList(chosenFile);
                                dialog.cancel();
                                dialog.dismiss();
                                showDialog();
                            } else fireFileSelectedEvent(chosenFile);
                        }
                    });

                    dialog = builder.show();
                    return dialog;
                }
}