package hw3.hash;

import java.util.List;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {
        int N = oomages.size();
        int[] buckets = new int[M];
        for (Oomage o : oomages) {
            int i = (o.hashCode() & 0x7FFFFFFF) % M;
            buckets[i]++;
        }
        int lowBound = (int) Math.ceil(N / 50);
        int highBound = (int) (N / 2.5);
        for (int bucket : buckets) {
            if (bucket < lowBound || bucket > highBound) {
                return false;
            }
        }
        return true;
    }
}
