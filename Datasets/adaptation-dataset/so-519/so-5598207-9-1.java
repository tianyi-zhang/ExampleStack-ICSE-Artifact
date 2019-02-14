public class foo {
    private CompiledScript tryCompiling(String string, int lineCount, int lastLineLength)
        throws ScriptException 
    {
        CompiledScript result = null;
        try
        {
            Compilable c = (Compilable)this.engine;
            result = c.compile(string);
        }
        catch (ScriptException se) {
            boolean rethrow = true;
            if (se.getCause() != null)
            {
                Integer col = columnNumber(se);
                Integer line = lineNumber(se);
                /* swallow the exception if it occurs at the last character
                 * of the input (we may need to wait for more lines)
                 */
                if (col != null
                 && line != null 
                 && line.intValue() == lineCount 
                 && col.intValue() == lastLineLength)
                {
                    rethrow = false;
                }
                else if (DEBUG)
                {
                    String msg = se.getCause().getMessage();
                    System.err.println("L"+line+" C"+col+"("+lineCount+","+lastLineLength+"): "+msg);
                    System.err.println("in '"+string+"'");
                }
            }

            if (rethrow)
            {
                reset();
                throw se;
            }
        }

        setExpectingMoreInput(result == null);
        return result;
    }
}