package de.skuzzle.polly.tools.math;


public final class MathUtil {

    public final static int limit(int num, int bound1, int bound2) {
        final int lowerBound = Math.min(bound1, bound2);
        final int upperBound = Math.max(bound1, bound2);
        return Math.max(lowerBound, Math.min(upperBound, num));
    }
    
    
    
    public final static double limit(double num, double bound1, double bound2) {
        final double lowerBound = Math.min(bound1, bound2);
        final double upperBound = Math.max(bound1, bound2);
        return Math.max(lowerBound, Math.min(upperBound, num));
    }
    
    
    
    public final static <T extends Comparable<T>> T limit(T elem, T bound1, T bound2) {
        final T lowerBound = min(bound1, bound2);
        final T upperBound = max(bound1, bound2);
        return max(lowerBound, min(upperBound, elem));
    }
    
    
    
    public final static <T extends Comparable<T>> T max(T e1, T e2) {
        final int comp = e1.compareTo(e2);
        if (comp < 0) {
            return e2;
        } else {
            return e1;
        }
    }
    
    
    
    public final static <T extends Comparable<T>> T min(T e1, T e2) {
        final int comp = e1.compareTo(e2);
        if (comp < 0) {
            return e2;
        } else {
            return e1;
        }
    }
    
    
    
    public final static <T extends Comparable<T>> boolean between(
            T elem, T bound1, T bound2) {
        
        final T lowerBound = min(bound1, bound2);
        final T upperBound = max(bound1, bound2);
        
        return (elem.compareTo(lowerBound) >= 0) && (elem.compareTo(upperBound) <= 0);
    }
    
    
    
    public final static boolean between(int num, int bound1, int bound2) {
        final int lowerBound = Math.min(bound1, bound2);
        final int upperBound = Math.max(bound1, bound2);
        return num >= lowerBound && num <= upperBound;
    }
    
    
    
    public final static boolean between(double num, double bound1, double bound2) {
        final double lowerBound = Math.min(bound1, bound2);
        final double upperBound = Math.max(bound1, bound2);
        return num >= lowerBound && num <= upperBound;
    }
    
    
    // parse roman literals
    // from http://stackoverflow.com/a/9073310/2489557
    public static int parseRoman(String romanNumber) {
        int decimal = 0;
        int lastNumber = 0;
        String romanNumeral = romanNumber.toUpperCase();
        for (int x = romanNumeral.length() - 1; x >= 0 ; x--) {
            char convertToDecimal = romanNumeral.charAt(x);

            switch (convertToDecimal) {
                case 'M':
                    decimal = processDecimal(1000, lastNumber, decimal);
                    lastNumber = 1000;
                    break;

                case 'D':
                    decimal = processDecimal(500, lastNumber, decimal);
                    lastNumber = 500;
                    break;

                case 'C':
                    decimal = processDecimal(100, lastNumber, decimal);
                    lastNumber = 100;
                    break;

                case 'L':
                    decimal = processDecimal(50, lastNumber, decimal);
                    lastNumber = 50;
                    break;

                case 'X':
                    decimal = processDecimal(10, lastNumber, decimal);
                    lastNumber = 10;
                    break;

                case 'V':
                    decimal = processDecimal(5, lastNumber, decimal);
                    lastNumber = 5;
                    break;

                case 'I':
                    decimal = processDecimal(1, lastNumber, decimal);
                    lastNumber = 1;
                    break;
            }
        }
        return decimal;
    }

    
    
    private static int processDecimal(int decimal, int lastNumber, int lastDecimal) {
        if (lastNumber > decimal) {
            return lastDecimal - decimal;
        } else {
            return lastDecimal + decimal;
        }
    }
    
    
    
    private MathUtil() {}
}
