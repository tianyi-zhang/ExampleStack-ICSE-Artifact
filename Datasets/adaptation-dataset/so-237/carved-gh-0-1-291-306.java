public class foo{
    // see http://stackoverflow.com/questions/1302605/how-do-i-convert-from-int-to-long-in-java/2904999#2904999
    private final Long getLong(Object obj) throws IllegalArgumentException {
        Long rv;

        if((obj.getClass() == Integer.class) || (obj.getClass() == Long.class) || (obj.getClass() == Double.class)) {
            rv = Long.parseLong(obj.toString());
        } else if((obj.getClass() == int.class) || (obj.getClass() == long.class) || (obj.getClass() == double.class)) {
            rv = (Long) obj;
        } else if(obj.getClass() == String.class) {
            rv = Long.parseLong(obj.toString());
        } else {
            throw new IllegalArgumentException("getLong: type " + obj.getClass() + " = \"" + obj.toString() + "\" unaccounted for");
        }

        return rv;
    }
}