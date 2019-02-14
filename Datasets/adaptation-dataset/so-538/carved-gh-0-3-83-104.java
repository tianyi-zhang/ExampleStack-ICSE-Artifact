public class foo{
    /**
     * Formats the specified number as a string of the form HH:MM:SS.
     *
     * @param number  the number to format.
     * @param toAppendTo  the buffer to append to (ignored here).
     * @param pos  the field position (ignored here).
     *
     * @return The string buffer.
     */
    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo,
            FieldPosition pos) {
        StringBuffer sb = new StringBuffer();
        long hours = number / 3600;
        sb.append(this.formatter.format(hours)).append(":");
        long remaining = number - (hours * 3600);
        long minutes = remaining / 60;
        sb.append(this.formatter.format(minutes)).append(":");
        long seconds = remaining - (minutes * 60);
        sb.append(this.formatter.format(seconds));
        return sb;
    }
}