package fleetflow.engine.randomized;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Parallel stream reduction utilities for high-throughput aggregation.
 */
public final class ParallelReduce {
    private ParallelReduce() {}

    public static long sum(Collection<Integer> values) {
        if (values == null || values.isEmpty()) return 0L;
        return values.parallelStream().mapToLong(Integer::longValue).sum();
    }

    public static long count(Collection<?> values) {
        if (values == null || values.isEmpty()) return 0L;
        return values.parallelStream().count();
    }

    public static <T> long countMatching(Collection<T> values, Predicate<T> predicate) {
        if (values == null || values.isEmpty() || predicate == null) return 0L;
        return values.parallelStream().filter(predicate).count();
    }
}

