public class foo {
public void foo(){
String input = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
List<String> result = new ArrayList<String>();
int start = 0;
boolean inQuotes = false;
for (int current = 0; current < input.length(); current++) {
    if (input.charAt(current) == '\"') inQuotes = !inQuotes; // toggle state
    boolean atLastChar = (current == input.length() - 1);
    if(atLastChar) result.add(input.substring(start));
    else if (input.charAt(current) == ',' && !inQuotes) {
        result.add(input.substring(start, current));
        start = current + 1;
    }
}
}
}