public class foo{
    /**
     * Adds the specified path to the java library path
     * Source: http://stackoverflow.com/questions/15409223/adding-new-paths-for-native-libraries-at-runtime-in-java
     * @param pathToAdd the path to add
     */
    public static void addLibraryPath(String pathToAdd) {
        try {
            final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            usrPathsField.setAccessible(true);
            final String[] paths = (String[]) usrPathsField.get(null);
            final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
            newPaths[newPaths.length - 1] = pathToAdd;
            usrPathsField.set(null, newPaths);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
            e.printStackTrace();
            Status.print("Cannot add library path");
        }
    }
}