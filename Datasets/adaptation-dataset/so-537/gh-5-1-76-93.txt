/*
 * Copyright (c) 2014 Swen Walkowski.
 * All rights reserved. Originator: Swen Walkowski.
 * Get more information about CardDAVSyncOutlook at https://github.com/somedevelopment/CardDAVSyncOutlook/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import main.Status;

public class Utilities {

    static public String loadFileInString(String strDescriptionFile, String strFileToLoad) {
        try {
            String strFile = null;

            Status.print("Load: " + strDescriptionFile);
            File file = new File(strFileToLoad);

            if (file.exists()) {
                try (BufferedReader in = new BufferedReader(new FileReader(strFileToLoad))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        strFile = strFile + line;
                    }
                }
            }

            return strFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static public void saveStringToFile(String strDescriptionFile, String strFileToSave, String strDestinationFile) {
        try {
            Status.print("Save: " + strDescriptionFile);

            File file = new File(strDestinationFile);
            FileWriter writer;

            writer = new FileWriter(file);
            writer.write(strFileToSave);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
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
