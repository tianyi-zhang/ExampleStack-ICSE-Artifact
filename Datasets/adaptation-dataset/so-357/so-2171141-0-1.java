public class foo {
public static double[] getArray(int n)
    {
        double a[] = new double[n];
        double s = 0.0d;
        Random random = new Random();
        for (int i = 0; i < n; i++)
        {
           a [i] = 1.0d - random.nextDouble();
           a [i] = -1 * Math.log(a[i]);
           s += a[i];
        }
        for (int i = 0; i < n; i++)
        {
           a [i] /= s;
        }
        return a;
    }
}