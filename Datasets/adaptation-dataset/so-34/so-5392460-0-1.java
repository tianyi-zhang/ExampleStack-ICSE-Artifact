public class foo {
    public static void main(String[] params) {

        byte[] barray= new byte[]{ 0x01, 0x02, 0x04, 0x08,
                                   0x10, 0x20, 0x40, (byte)0x80,
                                   };
        BitSet bits = new BitSet();

        if(barray!=null){
            for (int i=0; i<barray.length*8; i++) {
                if ((barray[barray.length-i/8-1]&(1<<(i%8))) > 0) {
                    bits.set(i);
                }
            }
        }
        System.out.println(bits);
    }
}