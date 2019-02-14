public class foo {
private static void breakManually(TextView tv, Editable editable)
{
    int width = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
    if(width == 0)
    {
        // Can't break with a width of 0.
        return false;
    }

    Paint p = tv.getPaint();
    float[] widths = new float[editable.length()];
    p.getTextWidths(editable.toString(), widths);
    float curWidth = 0.0f;
    int lastWSPos = -1;
    int strPos = 0;
    final char newLine = '\n';
    final String newLineStr = "\n";
    boolean reset = false;
    int insertCount = 0;

    //Traverse the string from the start position, adding each character's
    //width to the total until:
    //* A whitespace character is found.  In this case, mark the whitespace
    //position.  If the width goes over the max, this is where the newline
    //will be inserted.
    //* A newline character is found.  This resets the curWidth counter.
    //* curWidth > width.  Replace the whitespace with a newline and reset 
    //the counter.

    while(strPos < editable.length())
    {
        curWidth += widths[strPos];

        char curChar = editable.charAt(strPos);

        if(((int) curChar) == ((int) newLine))
        {
            reset = true;
        }
        else if(Character.isWhitespace(curChar))
        {
            lastWSPos = strPos;
        }
        else if(curWidth > width && lastWSPos >= 0)
        {
            editable.replace(lastWSPos, lastWSPos + 1, newLineStr);
            insertCount++;
            strPos = lastWSPos;
            lastWSPos = -1;
            reset = true;
        }

        if(reset)
        {
            curWidth = 0.0f;
            reset = false;
        }

        strPos++;
    }

    if(insertCount != 0)
    {
         tv.setText(editable);
    }
}
}