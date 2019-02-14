public class foo {
public static ECPoint scalmult(ECPoint P, BigInteger kin){
    //ECPoint R=P; - incorrect
    ECPoint R = ECPoint.POINT_INFINITY,S = P;
    BigInteger k = kin.mod(p);
    int length = k.bitLength();
    //System.out.println("length is" + length);
    byte[] binarray = new byte[length];
    for(int i=0;i<=length-1;i++){
        binarray[i] = k.mod(TWO).byteValue();
        k = k.divide(TWO);
    }
    /*for(int i = length-1;i >= 0;i--){
        System.out.print("" + binarray[i]); 
    }*/

    for(int i = length-1;i >= 0;i--){
        // i should start at length-1 not -2 because the MSB of binarry may not be 1
        R = doublePoint(R);
        if(binarray[i]== 1) 
            R = addPoint(R, S);
    }
return R;
}
}