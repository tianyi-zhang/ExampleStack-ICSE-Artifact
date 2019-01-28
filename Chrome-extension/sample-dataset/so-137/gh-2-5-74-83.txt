package tuwien.inso.mnsa.clitestclient;

import java.net.InetSocketAddress;
import java.security.Security;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import tuwien.inso.mnsa.nokiaprovider.NokiaProvider;

@SuppressWarnings("restriction")
public class Main {

	public static void main(String[] args) throws Exception {
		Security.addProvider(new NokiaProvider());

		TerminalFactory terminalFactory = TerminalFactory.getInstance("NokiaProvider", new InetSocketAddress("localhost", 7989));

		CardTerminals cardTerminals = terminalFactory.terminals();
		List<CardTerminal> cardTerminalList = cardTerminals.list();

		out("Connected NokiaTerminals (" + cardTerminalList.size() + "): " + cardTerminalList);

		for (CardTerminal ct : cardTerminalList) {
			out("Connection to card" + ct.getName());
			Card card = ct.connect("*");

			out("Requesting ATR");
			ATR atr = card.getATR();
			out("ATR: " + new String(atr.getBytes()));

			CardChannel channel = card.getBasicChannel();
			CommandAPDU command = new CommandAPDU(new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00 });
			out("sending SELECT " + bytesToHex(command.getData()));
			ResponseAPDU response = channel.transmit(command);
			out(response.toString() + ": " + bytesToHex(response.getData()));
			out("Card present: " + ct.isCardPresent());

			out("Remove card now (you have 2 sec)");
			Thread.sleep(2000);
			out("Rechecking...");

			command = new CommandAPDU(new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00 });
			out("sending SELECT " + bytesToHex(command.getData()));
			try {
				response = channel.transmit(command);
				out(response.toString() + ": " + bytesToHex(response.getData()));
			} catch (CardException ex) {
				out("Caught IOException: " + ex);
			}
			out("Card present: " + ct.isCardPresent());

			card.disconnect(true);
			out("Disconnected.");
		}

	}

	private static void out(String out) {
		System.out.println(out);
	}

	// From http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
