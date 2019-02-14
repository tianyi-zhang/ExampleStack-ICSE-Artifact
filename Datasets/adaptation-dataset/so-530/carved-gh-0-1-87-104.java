public class foo{
    /**
     * Copies an array with three dimensions. Code by Kevin Brock from http://stackoverflow.com/questions/2068370/efficient-system-arraycopy-on-multidimensional-arrays
     * @param array
     * @return The copy of the array-
     */
    private Block[][][] copyOf3Dim(Block[][][] array) {
        Block[][][] copy;
        copy = new Block[array.length][][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = new Block[array[i].length][];
            for (int j = 0; j < array[i].length; j++) {
                copy[i][j] = new Block[array[i][j].length];
                System.arraycopy(array[i][j], 0, copy[i][j], 0, 
                    array[i][j].length);
            }
        }
        return copy;
    } 
}