public class foo{
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
}