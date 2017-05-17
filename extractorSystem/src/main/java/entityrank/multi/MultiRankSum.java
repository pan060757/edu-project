package entityrank.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import entityrank.entity.*;

public class MultiRankSum {
	
	//sum of ranks
	public static List<Pair> rank(List<List<Pair>> list) {
		List<Pair> pairs=new ArrayList<Pair>();
		double total=0;
		Map<String, Double> entityMap=new HashMap<String, Double>();
		for (List<Pair> li:list) {
			for (Pair pair:li) {
				if (!entityMap.containsKey(pair.getEntity()))
					entityMap.put(pair.getEntity(),0d);
				double totalRank=entityMap.get(pair.getEntity());

				totalRank+=pair.getRank();
				total+=pair.getRank();
				entityMap.put(pair.getEntity(), totalRank);
			}
		}
		Iterator<String> iter=entityMap.keySet().iterator();
		while (iter.hasNext()) {
			String entity=iter.next();
			double rank=entityMap.get(entity);
			rank=rank/total;
			pairs.add(new Pair(entity, rank));
		}
		return pairs;
	}

}
