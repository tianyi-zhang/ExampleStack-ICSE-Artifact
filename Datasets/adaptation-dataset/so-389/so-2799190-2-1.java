public class foo {
 public T[] next() {
  if (arr == null)
   return null;

  T[] res = Arrays.copyOf(arr, permSwappings.length);
  //Prepare next
  int i = permSwappings.length-1;
  while (i >= 0 && permSwappings[i] == arr.length - 1) {
   swap(i, permSwappings[i]); //Undo the swap represented by permSwappings[i]
   permSwappings[i] = i;
   i--;
  }

  if (i < 0)
   arr = null;
  else {   
   int prev = permSwappings[i];
   swap(i, prev);
   int next = prev + 1;
   permSwappings[i] = next;
   swap(i, next);
  }

  return res;
 }
}