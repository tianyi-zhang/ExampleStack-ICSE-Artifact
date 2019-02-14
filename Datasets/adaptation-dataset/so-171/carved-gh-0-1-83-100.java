public class foo{
    private static void verify(PublicKey key, byte[] signature,
                                  byte[] data)
        throws IOException, GeneralSecurityException
    {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(key);
        
        ByteArrayOutputStream collector =
            new ByteArrayOutputStream(data.length);
        OutputStream checker = new SignatureOutputStream(collector, sig);
        checker.write(data);
        if(sig.verify(signature)) {
            System.err.println("Signature okay");
        }
        else {
            System.err.println("Signature falsed!");
        }
    }
}