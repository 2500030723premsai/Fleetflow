package fleetflow.engine.suffix;
public final class LCPKasai { private LCPKasai(){} public static int[] build(String text,int[] sa){int n=text.length();int[] rank=new int[n],lcp=new int[n];for(int i=0;i<n;i++)rank[sa[i]]=i;int h=0;for(int i=0;i<n;i++){int r=rank[i];if(r==0)continue;int j=sa[r-1];while(i+h<n&&j+h<n&&text.charAt(i+h)==text.charAt(j+h))h++;lcp[r]=h;if(h>0)h--;}return lcp;} }
