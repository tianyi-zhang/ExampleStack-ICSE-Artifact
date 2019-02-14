public class foo{
    /**
     * Algorithm from http://stackoverflow.com/a/12822025/4158442
     */
    private static BitSet randomBitSet(int size, int cardinality, Random random) {
        BitSet result = new BitSet(size);
        int[] chosen = new int[cardinality];

        for (int i = 0; i < cardinality; ++i) {
            chosen[i] = i;
            result.set(i);
        }
        for (int i = cardinality; i < size; ++i) {
            int j = random.nextInt(i+1);
            if (j < cardinality) {
                result.clear(chosen[j]);
                result.set(i);
                chosen[j] = i;
            }
        }
        return result;
    }
}