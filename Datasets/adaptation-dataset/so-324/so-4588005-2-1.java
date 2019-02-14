public class foo {
    private void peekAndReplace() throws IOException {
        int read = super.read(readBuf, 0, REPLACEMENT.length);
        for (int i1 = read - 1; i1 >= 0; i1--) {
            backBuf.push(readBuf[i1]);
        }
        for (int i = 0; i < REPLACEMENT.length; i++) {
            if (read != REPLACEMENT.length || readBuf[i] != REPLACEMENT[i]) {
                for (int j = REPLACEMENT.length - 1; j >= 0; j--) {
                    // In reverse order
                    backBuf.push(REPLACEMENT[j]);
                }
                return;
            }
        }
    }
}