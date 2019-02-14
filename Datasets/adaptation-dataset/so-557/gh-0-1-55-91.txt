package com.dashtricks.pakistan.app.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adopted from http://stackoverflow.com/questions/3592717/choose-file-dialog
 *
 * Created by japacible on 5/24/14.
 */
public class FileDialog {
    private static final String PARENT_DIR = "..";
    private final Activity activity;
    private final String TAG = getClass().getName();

    private String[] fileList;
    private File currentPath;
    private boolean selectDirectoryOption;
    private String fileEndsWith;

    private ListenerList<FileSelectedListener> fileListenerList =
            new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList =
            new ListenerList<FileDialog.DirectorySelectedListener>();

    public interface FileSelectedListener {
        void fileSelected(File file);
    }

    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }

    /**
     * Create the File Dialog given the activity and file path
     *
     * @param activity Activity
     * @param path File the initial path
     */
    public FileDialog(Activity activity, File path) {
        this.activity = activity;
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    /**
     * Create and return dialog to select file
     *
     * @return Dialog
     */
    public Dialog createFileDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select Directory", new DialogInterface.OnClickListener() {
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
            } else {
                fireFileSelectedEvent(chosenFile);
            }
            }
        });

        dialog = builder.show();
        return dialog;
    }

    /**
     * Add the listener to list of listeners
     *
     * @param listener FileSelectedListener
     */
    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    /**
     * Remove the listener to list of listeners
     *
     * @param listener FileSelectedListener
     */
    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    /**
     * Set whether or not user can select a directory
     *
     * @param selectDirectoryOption boolean
     */
    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    /**
     * Add the listener to list of listeners
     *
     * @param listener DirectorySelectedListener
     */
    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    /**
     * Remove the listener to list of listeners
     *
     * @param listener DirectorySelectedListener
     */
    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show the dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new ListenerList.FireHandler<DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                        boolean endsWith = fileEndsWith == null ||
                                filename.toLowerCase().endsWith(fileEndsWith);
                        return endsWith || sel.isDirectory();
                    }
                }
            };

            String[] fileList1 = path.list(filter);
            if (fileList1 != null) {
                for (String file : fileList1) {
                    r.add(file);
                }
            }

        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : fileEndsWith;
    }
}