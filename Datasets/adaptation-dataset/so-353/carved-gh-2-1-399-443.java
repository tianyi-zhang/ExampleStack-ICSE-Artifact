public class foo{
    /**
     * Returns the path of one File relative to another.
     *
     * @param target the target directory
     * @param base the base directory
     * @return target's path relative to the base directory
     */
    public static File getRelativeFile(File target, File base)
    {
        // This method is from
        // http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls

        String[] baseComponents;
        String[] targetComponents;
        try {
            baseComponents = base.getCanonicalPath().split(Pattern.quote(File.separator));
            targetComponents = target.getCanonicalPath().split(Pattern.quote(File.separator));
        } catch (IOException e) {
            throw new AppInfoError("Error resolving canonical path", e);
        }

        // skip common components
        int index = 0;
        for (; index < targetComponents.length && index < baseComponents.length; ++index)
        {
            if (!targetComponents[index].equals(baseComponents[index]))
                break;
        }

        StringBuilder result = new StringBuilder();
        if (index != baseComponents.length)
        {
            // backtrack to base directory
            for (int i = index; i < baseComponents.length; ++i)
                result.append("..").append(File.separator);
        }
        for (; index < targetComponents.length; ++index)
            result.append(targetComponents[index]).append(File.separator);
        if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\"))
        {
            // remove final path separator
            result.delete(result.length() - File.separator.length(), result.length());
        }
        return new File(result.toString());
    }
}