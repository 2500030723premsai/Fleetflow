package fleetflow.engine.randomized;
import java.util.*;
public final class ReservoirSampling { private ReservoirSampling(){} public static <T> List<T> sample(Iterable<T> stream,int k,Random random){if(k<0)throw new IllegalArgumentException("k must not be negative");List<T> result=new ArrayList<>(k);long seen=0;for(T item:stream){seen++;if(result.size()<k)result.add(item);else if(k>0){long j=random.nextLong(seen);if(j<k)result.set((int)j,item);}}return result;} }
