package probs;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TopN {
	private int topN;
	private TreeMap<Float, Integer> rankingMap;
	private float[] array;
	
	public TopN(float[] array, int topN) {
		this.array = array;
		this.topN = topN;
		rankingMap = new TreeMap<Float, Integer>();
	}
	
	public void ranking() {
		int arraySize = array.length;
		int i;
		for(i = 0; i < arraySize; i++) {
			if(i < topN)
				rankingMap.put(array[i], i);
			else {
				if(rankingMap.firstKey() < array[i]) {
					rankingMap.remove(rankingMap.firstKey());
					rankingMap.put(array[i], i);
				}
			}
		}
		
		Iterator<Entry<Float, Integer>> it = rankingMap.entrySet().iterator();
		Entry<Float, Integer> entry;
		while(it.hasNext()) {
			entry = it.next();
			System.out.printf("%d\t%4.3f\n", entry.getValue(),  entry.getKey());
		}
	}
}
