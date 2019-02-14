public class foo{
    protected String[] getCipherList()
    {
        String[] preferredCiphers = {

                // *_CHACHA20_POLY1305 are 3x to 4x faster than existing cipher suites.
                //   http://googleonlinesecurity.blogspot.com/2014/04/speeding-up-and-strengthening-https.html
                // Use them if available. Normative names can be found at (TLS spec depends on IPSec spec):
                //   http://tools.ietf.org/html/draft-nir-ipsecme-chacha20-poly1305-01
                //   http://tools.ietf.org/html/draft-mavrogiannopoulos-chacha-tls-02
                "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305",
                "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305",
                "TLS_ECDHE_ECDSA_WITH_CHACHA20_SHA",
                "TLS_ECDHE_RSA_WITH_CHACHA20_SHA",

                "TLS_DHE_RSA_WITH_CHACHA20_POLY1305",
                "TLS_RSA_WITH_CHACHA20_POLY1305",
                "TLS_DHE_RSA_WITH_CHACHA20_SHA",
                "TLS_RSA_WITH_CHACHA20_SHA",

                // Done with bleeding edge, back to TLS v1.2 and below
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",

                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",

                // TLS v1.0 (with some SSLv3 interop)
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",

                "TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
                "TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
                "SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA",
                "SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA",

                // RSA key transport sucks, but they are needed as a fallback.
                // For example, microsoft.com fails under all versions of TLS
                // if they are not included. If only TLS 1.0 is available at
                // the client, then google.com will fail too. TLS v1.3 is
                // trying to deprecate them, so it will be interesteng to see
                // what happens.
                "TLS_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_RSA_WITH_AES_256_CBC_SHA",
                "TLS_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_RSA_WITH_AES_128_CBC_SHA"
        };

        String[] availableCiphers = null;

        try
        {
            SSLSocketFactory factory = mCtx.getSocketFactory();
            availableCiphers = factory.getSupportedCipherSuites();
            Arrays.sort(availableCiphers);
        }
        catch(Exception e)
        {
            return new String[] {
                    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                    "TLS_RSA_WITH_AES_256_CBC_SHA256",
                    "TLS_RSA_WITH_AES_256_CBC_SHA",
                    "TLS_RSA_WITH_AES_128_CBC_SHA256",
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
            };
        }

        List<String> aa = new ArrayList<String>();
        for(int i = 0; i < preferredCiphers.length; i++)
        {
            int idx = Arrays.binarySearch(availableCiphers, preferredCiphers[i]);
            if(idx >= 0)
                aa.add(preferredCiphers[i]);
        }

        aa.add("TLS_EMPTY_RENEGOTIATION_INFO_SCSV");

        return aa.toArray(new String[0]);
    }
}