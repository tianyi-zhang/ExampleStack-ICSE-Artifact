public class foo {
/**
 * Returns the path of one File relative to another.
 *
 * @param target the target directory
 * @param base the base directory
 * @return target's path relative to the base directory
 * @throws IOException if an error occurs while resolving the files' canonical names
 */
 public static File getRelativeFile(File target, File base) throws IOException
 {
   String[] baseComponents = base.getCanonicalPath().split(Pattern.quote(File.separator));
   String[] targetComponents = target.getCanonicalPath().split(Pattern.quote(File.separator));

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
       result.append(".." + File.separator);
   }
   for (; index < targetComponents.length; ++index)
     result.append(targetComponents[index] + File.separator);
   if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\"))
   {
     // remove final path separator
     result.delete(result.length() - File.separator.length(), result.length());
   }
   return new File(result.toString());
 }
}