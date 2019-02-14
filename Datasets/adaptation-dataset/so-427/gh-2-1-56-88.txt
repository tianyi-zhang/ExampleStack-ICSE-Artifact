/*
 * #%L
 * Docbkx Maven Plugin
 * %%
 * Copyright (C) 2006 - 2014 Wilfred Springer, Cedric Pronzato
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.agilejava.docbkx.maven;

import java.io.*;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * File utility class for copying files from various forms and especially jars. More
 * information are available at:
 * http://stackoverflow.com/questions/1386809/copy-a-directory-from-a-jar-file
 */
public class FileUtils {
  /**
   * DOCUMENT ME!
   *
   * @param toCopy DOCUMENT ME!
   * @param destFile DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean copyFile(final File toCopy, final File destFile) {
    try {
      return FileUtils.copyStream(new FileInputStream(toCopy), new FileOutputStream(destFile));
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * DOCUMENT ME!
   *
   * @param toCopy DOCUMENT ME!
   * @param destDir DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean copyFilesRecusively(final File toCopy, final File destDir) {
    assert destDir.isDirectory();

    if (!toCopy.isDirectory()) {
      return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
    } else {
      final File newDestDir = new File(destDir, toCopy.getName());

      if (!newDestDir.exists() && !newDestDir.mkdir()) {
        return false;
      }

      File[] files = toCopy.listFiles();

      for (int i = 0; i < files.length; i++) {
        File child = files[i];

        if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * DOCUMENT ME!
   *
   * @param destDir DOCUMENT ME!
   * @param jarConnection DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws IOException DOCUMENT ME!
   */
  public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection)
      throws IOException {
    final JarFile jarFile = jarConnection.getJarFile();

    Enumeration e = jarFile.entries();

    while (e.hasMoreElements()) {
      final JarEntry entry = (JarEntry) e.nextElement();

      if (entry.getName().startsWith(jarConnection.getEntryName())) {
        final String filename = entry.getName().replace(jarConnection.getEntryName(), "");

        final File f = new File(destDir, filename);

        if (!entry.isDirectory()) {
          final InputStream entryInputStream = jarFile.getInputStream(entry);

          if (!FileUtils.copyStream(entryInputStream, f)) {
            return false;
          }

          entryInputStream.close();
        } else {
          if (!FileUtils.ensureDirectoryExists(f)) {
            throw new IOException("Could not create directory: " + f.getAbsolutePath());
          }
        }
      }
    }

    return true;
  }

  /**
   * DOCUMENT ME!
   *
   * @param originUrl DOCUMENT ME!
   * @param destination DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean copyResourcesRecursively( //
      final URL originUrl, final File destination) {
    try {
      final URLConnection urlConnection = originUrl.openConnection();

      if (urlConnection instanceof JarURLConnection) {
        return FileUtils.copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
      } else {
        final File sourceFile = new File(originUrl.getPath());

        if (sourceFile.isDirectory()) {
          final File[] files = sourceFile.listFiles();

          for (int i = 0; i < files.length; i++) {
            final File child = files[i];

            if (!FileUtils.copyFilesRecusively(child, destination)) {
              return false;
            }
          }
        } else {
          return FileUtils.copyFilesRecusively(sourceFile, destination);
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  private static boolean copyStream(final InputStream is, final File f) {
    try {
      return FileUtils.copyStream(is, new FileOutputStream(f));
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    return false;
  }

  private static boolean copyStream(final InputStream is, final OutputStream os) {
    try {
      final byte[] buf = new byte[1024];

      int len = 0;

      while ((len = is.read(buf)) > 0) {
        os.write(buf, 0, len);
      }

      is.close();
      os.close();

      return true;
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  private static boolean ensureDirectoryExists(final File f) {
    return f.exists() || f.mkdir();
  }
}
