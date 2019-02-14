public class foo{
    /**
     * Demonstrating test method. We print all subsets (sorted by size) of
     * a set created from the command line parameters, or an example set, if
     * there are no parameters.
     */
    public static void main(String[] params) {
        Set<String> baseSet =
            new HashSet<String>(params.length == 0 ?
                                Arrays.asList("Hello", "World", "this",
                                              "is", "a", "Test"):
                                Arrays.asList(params));
        
        
        System.out.println("baseSet: " + baseSet);

        for(int i = 0; i <= baseSet.size()+1; i++) {
            Set<Set<String>> pSet = new FiniteSubSets<String>(baseSet, i);
            System.out.println("------");
            System.out.println("subsets of size "+i+":");
            int count = 0;
            for(Set<String> ss : pSet) {
                System.out.println("    " +  ss);
                count++;
            }
            System.out.println("in total: " + count + ", " + pSet.size());
        }
    }
}