	
	// MinPQ taken from COSC 300 website provided by Dr. Simon for application to this problem
	
	import java.util.ArrayList;
	import java.util.HashMap;

	public class IndexedMinPQ<Key extends Comparable<Key>> {
		
	    private ArrayList<Key> pq; 
	    private int N = 0;
	    private HashMap<Key, Integer> map;
	    
	    public IndexedMinPQ(int cap) {
	        pq = new ArrayList<Key>(cap + 1);
	        map = new HashMap<Key, Integer>(cap + 1);
	        pq.add(null);
	    }

	    public IndexedMinPQ() { 
	    	this(1); 
	    }

	    public String toString() { 
	    	return pq.toString(); 
	    }
	    
	    private void swim(int k) {
	        Key x = pq.get(k);
	        while (k > 1 && pq.get(k/2).compareTo(x) > 0) {
	            pq.set(k, pq.get(k/2));
		    map.put(pq.get(k), k);
	            k = k/2;
	        }
	        pq.set(k, x);
		map.put(pq.get(k), k);
	    }

	    private void sink(int k) {
	        Key x = pq.get(k);
	        while (2*k <= N) {
	            int j = 2*k;
	            if (j<N && pq.get(j).compareTo(pq.get(j+1))>0) 
			j++;
	            if (x.compareTo(pq.get(j)) <= 0) break;
	            pq.set(k, pq.get(j)); 
		    map.put(pq.get(k), k);
	            k = j;
	        }
	        pq.set(k, x);
		map.put(pq.get(k), k);
	    }

	    public boolean isEmpty() { 
	    	return N == 0; 
	    }
	    
	    public int size() { 
	    	return N; 
	    }
	    
	    public Key min() { 
	    	return pq.get(1);
	    }
	    
	    public void insert(Key x) {
	        N++;
	        pq.add(x);
	        map.put(x,pq.size()-1);
	        swim(N);
	    }

	    public void swap(ArrayList<Key> a, int i, int j) {
	    	Key t = a.get(i); a.set(i,a.get(j)); a.set(j,t);
	    }
	    
	    public Key delMin() {
	        Key min = pq.get(1);
	        map.remove(min);
	        swap(pq, 1, N--);
	        sink(1);
	        pq.remove(N+1); 
	        return min;
	    }

	    public void change(Key x) {
		// Assumes the new value is less than the old value.
		int i = map.get(x);
		swim(i);
	    }
	}
