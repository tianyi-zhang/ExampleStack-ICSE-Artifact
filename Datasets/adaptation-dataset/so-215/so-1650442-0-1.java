public class foo {
public static String getClassName(InputStream is) throws Exception {
    DataInputStream dis = new DataInputStream(is);
    dis.readLong(); // skip header and class version
    int cpcnt = (dis.readShort()&0xffff)-1;
    int[] classes = new int[cpcnt];
    String[] strings = new String[cpcnt];
    for(int i=0; i<cpcnt; i++) {
        int t = dis.read();
        if(t==7) classes[i] = dis.readShort()&0xffff;
        else if(t==1) strings[i] = dis.readUTF();
        else if(t==5 || t==6) { dis.readLong(); i++; }
        else if(t==8) dis.readShort();
        else dis.readInt();
    }
    dis.readShort(); // skip access flags
    return strings[classes[(dis.readShort()&0xffff)-1]-1].replace('/', '.');
}
}