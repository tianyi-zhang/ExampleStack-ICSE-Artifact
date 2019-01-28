/**
 * Copyright (C) 2015 Johannes Schnatterer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.schnatterer.songbirdDbTools.Utils;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

/**
 * Utility class containing methods handling resources (files).
 * 
 * @author schnatterer
 * 
 */
public final class ResourceUtils {
	/** Don't instantiate utility classes! */
	private ResourceUtils() {
	}

	/**
	 * An array contains the most important characters that are not allowed in file names.
	 */
	public static final char[] ILLEGAL_FILE_NAME_CHARS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\',
			'<', '>', '|', '\"', ':' };
	/**
	 * The hard-coded default-value which is used to replace illegal characters in file names.
	 */
	public static final char DEFAULT_REPLACEMENT_CHAR = '-';

	/**
	 * Convenience method for {@link ResourceUtils#getRelativePath(String, String, String)}, using {@link File}
	 * parameters.
	 * 
	 * @param base
	 *            basePath is calculated from this file
	 * @param path
	 *            targetPath is calculated to this file
	 * @return <code>targetPath</code> relativized to <code>basePath</code>
	 */
	public static String getRelativePath(final File base, final File path) {
		// return base.toURI().relativize(path.toURI()).getPath();
		return getRelativePath(base.getAbsolutePath(), path.getAbsolutePath(), File.separator);
	}

	/**
	 * Convenience method for {@link ResourceUtils#getRelativePath(String, String, String)}, which uses the
	 * system-dependent default file separator {@link java.io.File#separator}.
	 * 
	 * @param basePath
	 *            basePath is calculated from this file
	 * @param targetPath
	 *            targetPath is calculated to this file
	 * @return <code>targetPath</code> relativized to <code>basePath</code>
	 */
	public static String getRelativePath(final String basePath, final String targetPath) {
		return getRelativePath(basePath, targetPath, File.separator);
	}

	/**
	 * Get the relative path from one file to another, specifying the directory separator. If one of the provided
	 * resources does not exist, it is assumed to be a file unless it ends with '/' or '\'.
	 * 
	 * @param targetPath
	 *            targetPath is calculated to this file
	 * @param basePath
	 *            basePath is calculated from this file
	 * @param pathSeparator
	 *            directory separator. The platform default is not assumed so that we can test Unix behavior when
	 *            running on Windows (for example)
	 * @return <code>targetPath</code> relativized to <code>basePath</code>
	 * 
	 * @author http://stackoverflow.com/questions/204784/how-to-construct-a-relative
	 *         -path-in-java-from-two-absolute-paths-or-urls
	 */
	public static String getRelativePath(final String basePath, final String targetPath, final String pathSeparator) {

		// Normalize the paths
		String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
		String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

		// Undo the changes to the separators made by normalization
		if (pathSeparator.equals("/")) {
			normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
			normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);

		} else if (pathSeparator.equals("\\")) {
			normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
			normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);

		} else {
			throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
		}

		String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
		String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		StringBuffer common = new StringBuffer();

		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length
				&& target[commonIndex].equals(base[commonIndex])) {
			common.append(target[commonIndex] + pathSeparator);
			commonIndex++;
		}

		if (commonIndex == 0) {
			// No single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized.
			throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '"
					+ normalizedBasePath + "'");
		}

		// The number of directories we have to backtrack depends on whether the
		// base is a file or a dir
		// For example, the relative path from
		//
		// /foo/bar/baz/gg/ff to /foo/bar/baz
		//
		// ".." if ff is a file
		// "../.." if ff is a directory
		//
		// The following is a heuristic to figure out if the base refers to a
		// file or dir. It's not perfect, because
		// the resource referred to by this path may not actually exist, but
		// it's the best I can do
		boolean baseIsFile = true;

		File baseResource = new File(normalizedBasePath);

		if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();

		} else if (basePath.endsWith(pathSeparator)) {
			baseIsFile = false;
		}

		StringBuffer relative = new StringBuffer();

		if (base.length != commonIndex) {

			int numDirsUp;
			if (baseIsFile) {
				numDirsUp = base.length - commonIndex - 1;
			} else {
				numDirsUp = base.length - commonIndex;
			}

			for (int i = 0; i < numDirsUp; i++) {
				relative.append(".." + pathSeparator);
			}
		}
		relative.append(normalizedTargetPath.substring(common.length()));
		return relative.toString();
	}

	/**
	 * Checks if a {@link String} would be a valid filename. Basing on the invalid characters in
	 * {@link #ILLEGAL_FILE_NAME_CHARS}.
	 * 
	 * @param potentialFileName
	 *            the string to validate.
	 * @return <code>true</code>, if the {@link String} would be a valid file name, otherwise <code>false</code>.
	 */
	public static boolean isLegalFilename(final String potentialFileName) {
		for (char c : ILLEGAL_FILE_NAME_CHARS) {
			if (potentialFileName.indexOf(c) > -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Replaces all illegal characters ({@link #ILLEGAL_FILE_NAME_CHARS}) by {@link #DEFAULT_REPLACEMENT_CHAR}.
	 * 
	 * @param potentialFileName
	 *            the file to be "legalized"
	 * @return a completely legal file name
	 */
	public static String legalizeFileName(final String potentialFileName) {
		String legalFileName = potentialFileName;
		for (char c : ILLEGAL_FILE_NAME_CHARS) {
			int illegalIndex = potentialFileName.indexOf(c);
			if (illegalIndex > -1) {
				legalFileName = potentialFileName.replace(c, DEFAULT_REPLACEMENT_CHAR);
			}
		}
		return legalFileName;
	}

	/**
	 * Exception thrown by {@link ResourceUtils#getRelativePath(String, String, String)}.
	 * 
	 * 
	 * @author schnatterer
	 * 
	 */
	public static class PathResolutionException extends RuntimeException {
		/** serialVersionUID. */
		private static final long serialVersionUID = 1121663230711844799L;

		/**
		 * Creates a {@link PathResolutionException} containing a specific message.
		 * 
		 * @param msg
		 *            the message to be transported by the exception
		 */
		PathResolutionException(final String msg) {
			super(msg);
		}
	}
}
