public class foo {
public int[][][] copyOf3Dim(int[][][] array) {
    int[][][] copy;
    copy = new int[array.length][][];
    for (int i = 0; i < array.length; i++) {
        copy[i] = new int[array[i].length][];
        for (int j = 0; j < array[i].length; j++) {
            copy[i][j] = new int[array[i][j].length];
            System.arraycopy(array[i][j], 0, copy[i][j], 0, 
                array[i][j].length);
        }
    }
    return copy;
}
}