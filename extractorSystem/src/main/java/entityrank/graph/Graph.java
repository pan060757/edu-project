package entityrank.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entityrank.entity.*;

public class Graph {
	
	private Map<Integer, Node> nodeMap=new HashMap<Integer, Node>();
	private Map<Integer, Set<Edge>> edgeMap=new HashMap<Integer, Set<Edge>>();
	//private List<String> tokens=new ArrayList<String>();

	public Graph() {
		super();

	}

//	public Graph(List<String> tokens) {
//		super();
//		this.tokens=tokens;
//	}
	
	public void addEntity(EntityNode entity) {
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		boolean found=false;
		while (iter.hasNext()) {
			int nodeId=iter.next();
			Node node2=nodeMap.get(nodeId);
			if (node2 instanceof EntityNode) {
				EntityNode e=(EntityNode) node2;
				if (e.getEntityName().equals(entity.getEntityName())) {
					found=true;
					break;
				}
			}
		}
		if (!found) {
			int size=nodeMap.keySet().size();
			int currentId=size++;
			nodeMap.put(currentId, entity);
		}
	}
	
	public void addSentence(Sentence sentence) {
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		boolean found=false;
		while (iter.hasNext()) {
			int nodeId=iter.next();
			Node node2=nodeMap.get(nodeId);
			if (node2 instanceof Sentence) {
				Sentence e=(Sentence) node2;
				if (e.getSentenceStr().equals(sentence.getSentenceStr())) {
					found=true;
					break;
				}
			}
		}
		if (!found) {
			int size=nodeMap.keySet().size();
			int currentId=size++;
			nodeMap.put(currentId, sentence);
		}
	}
	
	
	public void addEdge(Edge edge) {
		int from=edge.getFromId();
		if (!edgeMap.containsKey(from))
			edgeMap.put(from, new HashSet<Edge>());
		Set<Edge> edges=edgeMap.get(from);
		Iterator<Edge> iterator=edges.iterator();
		boolean found=false;
		while (iterator.hasNext()) {
			Edge edge2=iterator.next();
			if (edge.getToId()==edge2.getToId()) {
				found=true;
				break;
			}
		}
		if (!found)
			edges.add(edge);
		edgeMap.put(from, edges);
	}
	
	public int getEntityId(String entityName) {
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int id=iter.next();
			Node node=nodeMap.get(id);
			if (node instanceof EntityNode) {
				EntityNode entity2=(EntityNode) node;
				if (entity2.getEntityName().equals(entityName))
					return id;
			}
		}
		return -1;
	}
	
	public int getSentenceId(String sentenceStr) {
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int id=iter.next();
			Node node=nodeMap.get(id);
			if (node instanceof Sentence) {
				Sentence entity2=(Sentence) node;
				if (entity2.getSentenceStr().equals(sentenceStr))
					return id;
			}
		}
		return -1;
	}
	
	public void priorWeightNormalize() {
		int sentenceSize=0;
		int entitySize=0;
		double totalSentenceWeight=0;
		double totalEntityWeight=0;
		
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int id=iter.next();
			Node node=nodeMap.get(id);
			if (node instanceof Sentence) {
				sentenceSize++;
				Sentence sen=(Sentence) node;
				totalSentenceWeight+=sen.getPriorWeight();
			} else if (node instanceof EntityNode) {
				entitySize++;
				EntityNode entity=(EntityNode) node;
				totalEntityWeight+=entity.getPriorWeight();
			}
		}
		
		iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int id=iter.next();
			Node node=nodeMap.get(id);
			if (node instanceof Sentence) {
				node.setPriorWeight(node.getPriorWeight()/totalSentenceWeight*sentenceSize/(sentenceSize+entitySize));
			} else if (node instanceof EntityNode) {
				node.setPriorWeight(node.getPriorWeight()/totalEntityWeight*entitySize/(sentenceSize+entitySize));
			}
		}
	}
	
	
//	public List<String> getTokens() {
//		return tokens;
//	}

	@Override
	public String toString() {
		String outcome="";
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int nodeId=iter.next();
			Node node=nodeMap.get(nodeId);
			if (node instanceof EntityNode) {
				EntityNode e=(EntityNode)node;
				outcome+=nodeId+"\t"+e.getEntityName()+"\t"+e.getPriorWeight()+"\n";
			} else if (node instanceof Sentence) {
				Sentence s=(Sentence)node;
				outcome+=nodeId+"\t"+s.getSentenceStr()+"\t"+s.getPriorWeight()+"\n";
			}
		}
		
		Iterator<Integer> iter2=edgeMap.keySet().iterator();
		while (iter2.hasNext()) {
			int fromId=iter2.next();
			Set<Edge> edges=edgeMap.get(fromId);
			
			Iterator<Edge> iter3=edges.iterator();
			while (iter3.hasNext()) {
				Edge edge=iter3.next();
				outcome+=edge.getFromId()+"\t"+edge.getToId()+"\t"+edge.getWeight()+"\n";
			}
		}
		return outcome;
	}
	
	public int numOfEntity() {
		int count=0;
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int nodeId=iter.next();
			Node node=nodeMap.get(nodeId);
			if (node instanceof EntityNode) {
				count++;
			}
		}
		return count;
	}
	
	public void iterativeWeightUpdate(int n, double lamda) {
		for (int i=0;i<n;i++)
			weightUpdate(lamda);
	}
	
	public void weightUpdateUtilConverage(double lamda) {
		int numOfE=numOfEntity();
		int numOfS=nodeMap.keySet().size()-numOfE;
		double[] weightVector=new double[numOfE+numOfS];
		for (int i=0;i<weightVector.length;i++)
			weightVector[i]=nodeMap.get(i).getCurrentWeight();
		double con=0;
		do {
			weightUpdate(lamda);
			con=0;
			double[] newWeightVector=new double[numOfE+numOfS];
			for (int i=0;i<newWeightVector.length;i++) {
				newWeightVector[i]=nodeMap.get(i).getCurrentWeight();
				con+=Math.pow(newWeightVector[i]-weightVector[i], 2);
			}
			con=Math.sqrt(con);
		} while (con>=0.001);
	}
	
	private void weightUpdate(double lamda) {
		int numOfE=numOfEntity();
		int numOfS=nodeMap.keySet().size()-numOfE;
		
		double[] priorVector=new double[numOfE+numOfS];
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int nodeId=iter.next();
			Node node=nodeMap.get(nodeId);
			priorVector[nodeId]=node.getPriorWeight();
		//	System.out.print("prior"+priorVector[nodeId]+"\t");
		}
	//	System.out.println();
		double[] newWeightVector=new double[numOfE+numOfS];
		for (int i=0;i<newWeightVector.length;i++)
			newWeightVector[i]=0;
		double[] oldWeightVector=new double[numOfE+numOfS];
		for (int i=0;i<oldWeightVector.length;i++) {
			oldWeightVector[i]=nodeMap.get(i).getCurrentWeight();
		//	System.out.print(oldWeightVector[i]+"\t");
		}
	//	System.out.println();
		
		//from entity to sentence
		for (int i=0;i<numOfE;i++) {
			if (!edgeMap.containsKey(i))
				continue;
			Set<Edge> edges=edgeMap.get(i);
			double totalWeight=0;
			Iterator<Edge> iter2=edges.iterator();
			while (iter2.hasNext()) {
				Edge edge=iter2.next();
				totalWeight+=edge.getWeight();
			}
			iter2=edges.iterator();
			while (iter2.hasNext()) {
				Edge edge=iter2.next();
				double weight=edge.getWeight();
				int toId=edge.getToId();
				newWeightVector[toId]+=oldWeightVector[i]*weight/totalWeight;
			}
		}
		
		//from sentence to others
		if (numOfS==1) {
			//only from sentence to entities
			for (int i=numOfE;i<numOfE+numOfS;i++) {
				if (!edgeMap.containsKey(i))
					continue;
				Set<Edge> edges=edgeMap.get(i);
				double totalWeight=0;
				Iterator<Edge> iter2=edges.iterator();
				while (iter2.hasNext()) {
					Edge edge=iter2.next();
					totalWeight+=edge.getWeight();
				}
				iter2=edges.iterator();
				while (iter2.hasNext()) {
					Edge edge=iter2.next();
					double weight=edge.getWeight();
					int toId=edge.getToId();
					newWeightVector[toId]+=oldWeightVector[i]*weight/totalWeight;
				}
			}
		} else {
			for (int i=numOfE;i<numOfE+numOfS;i++) {
				if (!edgeMap.containsKey(i))
					continue;
				Set<Edge> edges=edgeMap.get(i);
				double totalEntityWeight=0;
				double totalSentenceWeight=0;
				Iterator<Edge> iter2=edges.iterator();
				while (iter2.hasNext()) {
					Edge edge=iter2.next();
					int toId=edge.getToId();
					if (toId<numOfE)
						totalEntityWeight+=edge.getWeight();
					else
						totalSentenceWeight+=edge.getWeight();
				}
				iter2=edges.iterator();
				while (iter2.hasNext()) {
					Edge edge=iter2.next();
					double weight=edge.getWeight();
					int toId=edge.getToId();
					if (toId<numOfE)
						newWeightVector[toId]+=oldWeightVector[i]*weight/totalEntityWeight/2;
					else
						newWeightVector[toId]+=oldWeightVector[i]*weight/totalSentenceWeight/2;
				}
			}
		}
		double totalWeight=0;
		for (int i=0;i<numOfE+numOfS;i++) {
		//	System.out.println(newWeightVector[i]+"\t"+priorVector[i]);
			newWeightVector[i]=(1-lamda)*newWeightVector[i]+lamda*priorVector[i];
			totalWeight+=newWeightVector[i];
		}
		for (int i=0;i<numOfE+numOfS;i++) {
			newWeightVector[i]=newWeightVector[i]/totalWeight;
		//	System.out.println("totalWeight"+totalWeight);
		//	System.out.print(newWeightVector[i]+"\t");
		}
//		System.out.println();
		for (int i=0;i<numOfE+numOfS;i++) {
			Node node=nodeMap.get(i);
			node.setCurrentWeight(newWeightVector[i]);
			nodeMap.put(i, node);
		}
	}
	
	public List<Pair> normalizedEntityRank() {
		List<Pair> list=new ArrayList<Pair>();
		int numOfE=numOfEntity();
		double totalWeight=0;
		double[] weightVector=new double[numOfE];
		for (int i=0;i<numOfE;i++) {
			weightVector[i]=nodeMap.get(i).getCurrentWeight();
			totalWeight+=weightVector[i];
		}
		for (int i=0;i<numOfE;i++) {
			EntityNode e=(EntityNode)nodeMap.get(i);
			Pair rankingStatistic=new Pair(e.getEntityName(), weightVector[i]/totalWeight);
			list.add(rankingStatistic);
		}
		/*
		if (list.size()<k) {
			double ratio=list.size()/(double)k;
			for (int i=0;i<list.size();i++) {
				Pair p=list.get(i);
				p.setRank(p.getRank()*ratio);
				list.set(i, p);
			}
		} else if (list.size()>k) {
			Collections.sort(list);
			Collections.reverse(list);
			while (list.size()>k) {
				list.remove(list.size()-1);
			}
			double total=0;
			for (int i=0;i<list.size();i++) {
				Pair p=list.get(i);
				total+=p.getRank();
			}
			for (int i=0;i<list.size();i++) {
				Pair p=list.get(i);
				p.setRank(p.getRank()/total);
				list.set(i, p);
			}
		}
		*/
		return list;
	}
	
//	public int getDocLength() {
//		return tokens.size();
//	}
	
	public List<String> getEntityTokens() {
		List<String> list=new ArrayList<String>();
		Iterator<Integer> iter=nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int nodeId=iter.next();
			Node node=nodeMap.get(nodeId);
			if (node instanceof EntityNode) {
				EntityNode entity=(EntityNode)node;
				int tf=entity.getTfInDoc();
				for (int i=0;i<tf;i++)
					list.add(entity.getEntityName());
			}
		}
		return list;
	}
}
