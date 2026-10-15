package fleetflow.engine.approximation;
import java.util.*;
public final class VertexCoverApprox { private VertexCoverApprox(){} /** 2-approximation for an undirected graph represented by edge pairs. */ public static Set<Integer> approximate(int[][] edges){Set<Integer> cover=new LinkedHashSet<>();for(int[] e:edges){if(e.length<2)continue;int u=e[0],v=e[1];if(!cover.contains(u)&&!cover.contains(v)){cover.add(u);cover.add(v);}}return cover;} }
