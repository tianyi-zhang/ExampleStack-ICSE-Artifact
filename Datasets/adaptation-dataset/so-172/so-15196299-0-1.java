public class foo {
public void foo(){
Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
elementField.setAccessible(true);
Object element = elementField.get(xmlReader);
Field attsField = element.getClass().getDeclaredField("theAtts");
attsField.setAccessible(true);
Object atts = attsField.get(element);
Field dataField = atts.getClass().getDeclaredField("data");
dataField.setAccessible(true);
String[] data = (String[])dataField.get(atts);
Field lengthField = atts.getClass().getDeclaredField("length");
lengthField.setAccessible(true);
int len = (Integer)lengthField.get(atts);

String myAttributeA = null;
String myAttributeB = null;

for(int i = 0; i < len; i++) {
    if("attrA".equals(data[i * 5 + 1])) {
        myAttributeA = data[i * 5 + 4];
    } else if("attrB".equals(data[i * 5 + 1])) {
        myAttributeB = data[i * 5 + 4];
    }
}
}
}