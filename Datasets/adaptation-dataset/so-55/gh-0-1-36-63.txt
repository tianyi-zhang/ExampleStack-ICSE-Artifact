/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2014 InstantCom Ltd. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://raw.github.com/vnesek/instantcom-mm7/master/LICENSE.txt
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at appropriate location.
 */

package net.instantcom.mm7;

import java.io.InputStream;

import net.instantcom.mm7.Address.RecipientType;

/**
 * Answer to <a href=
 * "http://stackoverflow.com/questions/12373744/generating-mm7-soap-request-in-java"
 * > stackoverflow question generating-mm7-soap-request-in-java</a>
 */
public class Stackoverflow12373722 {

	private static InputStream load(String file) {
		return Stackoverflow12373722.class.getResourceAsStream(file);
	}

	public static void main(String[] args) throws Exception {
		SubmitReq sr = new SubmitReq();
		sr.setNamespace("http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2");
		sr.setMm7Version("5.3.0");
		sr.setVaspId("Ejada");
		sr.setVasId("Ejada");
		sr.setSubject("Allah Akbar");
		sr.setDeliveryReport(true);
		sr.addRecipient(new Address("1111", RecipientType.TO));
		sr.setTransactionId("1348056868070-1-");

		// Attach a SMIL presentation
		BinaryContent smil = new BinaryContent("application/smil", load("smil-sample2.xml"));
		smil.setContentId("smil");

		// Attach a picture
		BinaryContent image = new BinaryContent("image/jpeg", load("lorena.jpg"));
		image.setContentId("image");

		// Attach a text
		TextContent text = new TextContent("Lorena sends a lots of hugs!");
		text.setContentId("text");

		// Pack it all up
		sr.setContent(new BasicContent(smil, image, text));

		MM7Message.save(sr, System.out, new MM7Context());
	}
}
