public class foo{
    /**
     * searchs the smallest neighbourhood (by diameter) which
     * contains at least minSize elements.
     *
     * @return an array of two arrays:
     *     {   { the middle point of the neighborhood,
     *           the diameter of the neighborhood  },
     *        all the elements of the neigborhood }
     *
     * TODO: better return an object of a class encapsuling these.
     */
    public double[][] findSmallNeighbourhood(int minSize) {
        int currentLeft = -1;
        int currentRight = -1;
        double currentMinDiameter = Double.POSITIVE_INFINITY;

        for(int i = 0; i + minSize-1 < data.length; i++) {
            double diameter = data[i+minSize-1] - data[i];
            if(diameter < currentMinDiameter) {
                currentMinDiameter = diameter;
                currentLeft = i;
                currentRight = i + minSize-1;
            }
        }
        return
            new double[][] {
            { 
                (data[currentRight] + data[currentLeft])/2.0,
                currentMinDiameter
            },
            Arrays.copyOfRange(data, currentLeft, currentRight+1)
        };
    }
}