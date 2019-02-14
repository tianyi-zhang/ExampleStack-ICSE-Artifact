public class foo {
private String getCertificateSHA1Fingerprint() {
    PackageManager pm = mContext.getPackageManager();
    String packageName = mContext.getPackageName();
    int flags = PackageManager.GET_SIGNATURES;
    PackageInfo packageInfo = null;
    try {
        packageInfo = pm.getPackageInfo(packageName, flags);
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
    Signature[] signatures = packageInfo.signatures;
    byte[] cert = signatures[0].toByteArray();
    InputStream input = new ByteArrayInputStream(cert);
    CertificateFactory cf = null;
    try {
        cf = CertificateFactory.getInstance("X509");
    } catch (CertificateException e) {
        e.printStackTrace();
    }
    X509Certificate c = null;
    try {
        c = (X509Certificate) cf.generateCertificate(input);
    } catch (CertificateException e) {
        e.printStackTrace();
    }
    String hexString = null;
    try {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] publicKey = md.digest(c.getEncoded());
        hexString = byte2HexFormatted(publicKey);
    } catch (NoSuchAlgorithmException e1) {
        e1.printStackTrace();
    } catch (CertificateEncodingException e) {
        e.printStackTrace();
    }
    return hexString;
}
}