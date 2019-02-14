public class foo{
    /**
     * Add a path to system property 'java.library.path', to allow JVM searching
     * into given directory when loading a library.
     * Note that it's a hack solution, used because of the fact that we must let
     * gnu.io package load its native libraries itself.
     *
     * Original code from <href url=http://stackoverflow.com/questions/15409223/adding-new-paths-for-native-libraries-at-runtime-in-java>here</href>.
     * @param pathToAdd
     */
    private static void addLibraryPath(final String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }
}