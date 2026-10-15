package fleetflow.engine.randomized;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomized QuickSort implementation.
 * Uses random pivot selection to achieve expected O(N log N) performance
 * and avoid worst-case O(N^2) behavior on already sorted input.
 */
public final class RandomizedQuickSort {
    private RandomizedQuickSort() {}

    public static void sort(int[] a) {
        if (a == null || a.length <= 1) return;
        sort(a, 0, a.length - 1);
    }

    private static void sort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = ThreadLocalRandom.current().nextInt(lo, hi + 1);
        swap(a, p, hi);
        int pivot = a[hi], i = lo;
        for (int j = lo; j < hi; j++) {
            if (a[j] <= pivot) swap(a, i++, j);
        }
        swap(a, i, hi);
        sort(a, lo, i - 1);
        sort(a, i + 1, hi);
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * Sorts a list in-place using randomized quicksort with a comparator.
     */
    public static <T> void sort(List<T> list, Comparator<? super T> comp) {
        if (list == null || list.size() <= 1) return;
        sort(list, 0, list.size() - 1, comp);
    }

    private static <T> void sort(List<T> list, int lo, int hi, Comparator<? super T> comp) {
        if (lo >= hi) return;
        int p = ThreadLocalRandom.current().nextInt(lo, hi + 1);
        swap(list, p, hi);
        T pivot = list.get(hi);
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (comp.compare(list.get(j), pivot) <= 0) {
                swap(list, i++, j);
            }
        }
        swap(list, i, hi);
        sort(list, lo, i - 1, comp);
        sort(list, i + 1, hi, comp);
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}

