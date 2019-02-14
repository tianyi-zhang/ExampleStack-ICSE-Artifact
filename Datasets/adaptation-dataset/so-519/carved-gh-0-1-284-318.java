public class foo{
	private CompiledScript tryCompiling(final String string, final int lineCount,
		final int lastLineLength) throws ScriptException
	{
		CompiledScript result = null;
		try {
			final Compilable c = (Compilable) engine;
			result = c.compile(string);
		}
		catch (final ScriptException se) {
			boolean rethrow = true;
			if (se.getCause() != null) {
				final Integer col = columnNumber(se);
				final Integer line = lineNumber(se);
				// swallow the exception if it occurs at the last character
				// of the input (we may need to wait for more lines)
				if (isLastCharacter(col, line, lineCount, lastLineLength)) {
					rethrow = false;
				}
				else if (log != null && log.isDebug()) {
					final String msg = se.getCause().getMessage();
					log.debug("L" + line + " C" + col + "(" + lineCount + "," +
						lastLineLength + "): " + msg);
					log.debug("in '" + string + "'");
				}
			}

			if (rethrow) {
				reset();
				throw se;
			}
		}

		expectingMoreInput = result == null;
		return result;
	}
}