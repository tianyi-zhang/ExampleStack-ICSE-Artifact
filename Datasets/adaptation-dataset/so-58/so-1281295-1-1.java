public class foo {
private void add(File source, JarOutputStream target) throws IOException
{
  BufferedInputStream in = null;
  try
  {
    if (source.isDirectory())
    {
      String name = source.getPath().replace("\\", "/");
      if (!name.isEmpty())
      {
        if (!name.endsWith("/"))
          name += "/";
        JarEntry entry = new JarEntry(name);
        entry.setTime(source.lastModified());
        target.putNextEntry(entry);
        target.closeEntry();
      }
      for (File nestedFile: source.listFiles())
        add(nestedFile, target);
      return;
    }

    JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
    entry.setTime(source.lastModified());
    target.putNextEntry(entry);
    in = new BufferedInputStream(new FileInputStream(source));

    byte[] buffer = new byte[1024];
    while (true)
    {
      int count = in.read(buffer);
      if (count == -1)
        break;
      target.write(buffer, 0, count);
    }
    target.closeEntry();
  }
  finally
  {
    if (in != null)
      in.close();
  }
}
}