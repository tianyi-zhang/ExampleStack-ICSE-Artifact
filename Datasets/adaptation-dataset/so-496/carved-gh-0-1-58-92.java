public class foo{
	private void typeNumPad(int digit) {
		// System.err.println(digit+"");
		switch (digit) {
		case 0:
			doType(KeyEvent.VK_NUMPAD0);
			break;
		case 1:
			doType(KeyEvent.VK_NUMPAD1);
			break;
		case 2:
			doType(KeyEvent.VK_NUMPAD2);
			break;
		case 3:
			doType(KeyEvent.VK_NUMPAD3);
			break;
		case 4:
			doType(KeyEvent.VK_NUMPAD4);
			break;
		case 5:
			doType(KeyEvent.VK_NUMPAD5);
			break;
		case 6:
			doType(KeyEvent.VK_NUMPAD6);
			break;
		case 7:
			doType(KeyEvent.VK_NUMPAD7);
			break;
		case 8:
			doType(KeyEvent.VK_NUMPAD8);
			break;
		case 9:
			doType(KeyEvent.VK_NUMPAD9);
			break;
		}
	}
}