public class foo{
    @Override public int compareTo(final Version that) {
        if(that == null)
            return 1;
        final String[] thisParts = this.get().split("\\.");
        final String[] thatParts = that.get().split("\\.");
        final int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            final int thisPart = i < thisParts.length ?
                NumberParser.parseInteger(thisParts[i]) : 0;
            final int thatPart = i < thatParts.length ?
                NumberParser.parseInteger(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }
}