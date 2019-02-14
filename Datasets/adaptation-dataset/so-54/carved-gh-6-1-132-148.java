public class foo{
        @Override public int compareTo(mavenVersion other) {
            if(other == null)
                return 1;
            String[] otherParts = other.parts;
            int length = Math.max(parts.length, otherParts.length);
            for(int i = 0; i < length; i++) {
                int thisPart = i < parts.length ?
                        Integer.parseInt(parts[i]) : 0;
                int thatPart = i < otherParts.length ?
                        Integer.parseInt(otherParts[i]) : 0;
                if(thisPart < thatPart)
                    return -1;
                if(thisPart > thatPart)
                    return 1;
            }
            return 0;
        }
}