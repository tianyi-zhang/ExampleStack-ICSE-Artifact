public class foo{
    public static ECPoint scalarMult(ECPoint p, BigInteger kin, EllipticCurve curve) {
        ECPoint r = ECPoint.POINT_INFINITY;
        BigInteger prime = ((ECFieldFp) curve.getField()).getP();
        BigInteger k = kin.mod(prime);
        int length = k.bitLength();
        byte[] binarray = new byte[length];
        for (int i = 0; i <= length-1; i++) {
            binarray[i] = k.mod(TWO).byteValue();
            k = k.divide(TWO);
        }

        for (int i = length-1; i >= 0; i--) {
            // i should start at length-1 not -2 because the MSB of binarry may not be 1
            r = doublePoint(r, curve);
            if (binarray[i] == 1) 
                r = addPoint(r, p, curve);
        }
        return r;
    }
}