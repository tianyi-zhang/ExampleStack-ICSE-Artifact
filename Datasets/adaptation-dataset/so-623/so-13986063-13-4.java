public class foo {
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
                                    boolean endsWith = fileEndsWith != null ? filename.toLowerCase().endsWith(fileEndsWith) : true;
                                    return endsWith || sel.isDirectory();
                                }
                            }
                        };
                        String[] fileList1 = path.list(filter);
                        for (String file : fileList1) {
                            r.add(file);
                        }
                    }
                    fileList = (String[]) r.toArray(new String[]{});
                }
}