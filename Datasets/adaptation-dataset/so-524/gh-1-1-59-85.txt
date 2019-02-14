package edu.unc.utils;

/**
 * Custom sorting utilities see:
 * http://stackoverflow.com/questions/951848/java-array
 * -sort-quick-way-to-get-a-sorted-list-of-indices-of-an-array
 * 
 * @author timpalpant
 *
 */
public class SortUtils {
  /**
   * Sort an array in ascending order, but return the index of each sorted
   * element in the original array
   * 
   * @param main
   *          an array to sort in ascending order
   * @return the index of each sorted element in main
   */
  public static int[] sortIndices(float[] main) {
    int[] index = new int[main.length];
    for (int i = 0; i < index.length; i++) {
      index[i] = i;
    }

    quicksort(main, index, 0, index.length - 1);

    return index;
  }

  /**
   * Return the rank (in ascending order) of each element in an array
   * 
   * @param main
   *          an array to rank
   * @return the rank of each element in main
   */
  public static int[] rank(float[] main) {
    int[] sortedIndices = sortIndices(main);
    int[] rank = new int[main.length];
    for (int i = 0; i < rank.length; i++) {
      rank[sortedIndices[i]] = i + 1;
    }

    return rank;
  }

  // quicksort a[left] to a[right]
  private static void quicksort(float[] a, int[] index, int left, int right) {
    if (right <= left) {
      return;
    }

    int i = partition(a, index, left, right);
    quicksort(a, index, left, i - 1);
    quicksort(a, index, i + 1, right);
  }

  // partition a[left] to a[right], assumes left < right
  private static int partition(float[] a, int[] index, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      // find item on left to swap
      while (a[index[++i]] < a[index[right]])
        ; // a[right] acts as sentinel
      // find item on right to swap
      while (a[index[right]] < a[index[--j]]) {
        // don't go out-of-bounds
        if (j == left) {
          break;
        }
      }

      // check if pointers cross
      if (i >= j) {
        break;
      }

      swap(a, index, i, j); // swap two elements into place
    }

    swap(a, index, i, right); // swap with partition element
    return i;
  }

  // exchange a[i] and a[j]
  private static void swap(float[] a, int[] index, int i, int j) {
    int tmp = index[i];
    index[i] = index[j];
    index[j] = tmp;
  }
}
