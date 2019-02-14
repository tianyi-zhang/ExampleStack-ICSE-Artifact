public class foo{
    /**
     * Returns the path of one File relative to another.
     * <p/>
     * From http://stackoverflow.com/questions/204784
     *
     * @param target the target directory
     * @param base   the base directory
     * @return target's path relative to the base directory
     */
    public static String getRelativePath(File target, File base) {
        String[] baseComponents;
        String[] targetComponents;
        baseComponents = getFullPath(base).split(Pattern.quote(SEPARATOR));
        targetComponents = getFullPath(target).split(Pattern.quote(SEPARATOR));

        // skip common components
        int index = 0;
        for (; index < targetComponents.length && index < baseComponents.length; ++index) {
            if (!targetComponents[index].equals(baseComponents[index]))
                break;
        }

        StringBuilder result = new StringBuilder();
        if (index != baseComponents.length) {
            // backtrack to base directory
            for (int i = index; i < baseComponents.length; ++i)
                result.append("..").append(SEPARATOR);
        }
        for (; index < targetComponents.length; ++index)
            result.append(targetComponents[index]).append(SEPARATOR);
        if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
            // remove final path separator
            result.delete(result.length() - SEPARATOR.length(), result.length());
        }
        return result.toString();
    }
}