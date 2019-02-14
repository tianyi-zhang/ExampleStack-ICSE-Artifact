public class foo {
/**
 * a test method.
 */
public static void main(String[] params) 
    throws IOException, GeneralSecurityException
{
    if(params.length < 1) {
        params = new String[] {"Hello", "World!"};
    }

    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
    KeyPair pair = gen.generateKeyPair();


    ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();

    byte[] signature = signData(arrayStream, pair.getPrivate(), params);
    byte[] data = arrayStream.toByteArray();

    verify(pair.getPublic(), signature, data);

    // change one byte by one
    data[3]++;

    verify(pair.getPublic(), signature, data);

    data = arrayStream.toByteArray();

    verify(pair.getPublic(), signature, data);

    // change signature
    signature[4]++;

    verify(pair.getPublic(), signature, data);

}
}