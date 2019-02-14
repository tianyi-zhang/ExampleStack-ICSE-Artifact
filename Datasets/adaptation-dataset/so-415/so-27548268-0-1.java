public class foo {
static <T> T getLast(Stream<T> stream) {
    Spliterator<T> sp=stream.spliterator();
    if(sp.hasCharacteristics(Spliterator.SIZED|Spliterator.SUBSIZED)) {
        for(;;) {
            Spliterator<T> part=sp.trySplit();
            if(part==null) break;
            if(sp.getExactSizeIfKnown()==0) {
                sp=part;
                break;
            }
        }
    }
    T value=null;
    for(Iterator<T> it=recursive(sp); it.hasNext(); )
        value=it.next();
    return value;
}
}