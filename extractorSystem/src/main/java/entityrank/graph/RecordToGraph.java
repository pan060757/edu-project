package entityrank.graph;

import java.util.ArrayList;
import java.util.List;
import common.ANSJ;
import entityrank.nlp.*;
import entityrank.entity.*;



public class RecordToGraph {
	
	private DocRecord record;

	public DocRecord getRecord() {
		return record;
	}

	public void setRecord(DocRecord record) {
		this.record = record;
	}
	
	public RecordToGraph(DocRecord record) {
		this.record=record;
	}
	
	public Graph toGraph() {
	//	System.out.println("content"+"\t"+record.getContent().length());
		Graph graph=new Graph();
		List<String> entityList=new ArrayList<String>();
		List<String> sentences = ANSJ.segToSentence(record.getContent());

				//add entity
		List<String> list =record.getEntityList();
		for (String s:list) {
			int tf=(int)SEWeight.tf(s, record.getContent());
			EntityNode e=new EntityNode(s,tf);
			double priorWeight=SEWeight.sf(s, sentences);
			if (priorWeight>0) {
				e.setPriorWeight(priorWeight);
				e.setCurrentWeight(priorWeight);
				graph.addEntity(e);
				entityList.add(s);
			}
		}
		
		//add sentence
		String title=record.getTitle();
		for (String s:sentences) {
			Sentence sen=new Sentence(s);
			double priorWeight=SSWeight.ssWeight(s, title);
			sen.setPriorWeight(priorWeight);
			sen.setCurrentWeight(priorWeight);
			graph.addSentence(sen);
		}
		graph.priorWeightNormalize();
		
		//add sentence-entity edge
		for (String s:sentences) {
			for (String entity:entityList) {
				if (s.indexOf(entity)>=0) {
					int entityId=graph.getEntityId(entity);
					int sentenceId=graph.getSentenceId(s);
					double seWeight=SEWeight.seWeight(entity, s, sentences);
					if (seWeight>0) {
						graph.addEdge(new Edge(sentenceId, entityId, true, seWeight));
						graph.addEdge(new Edge(entityId, sentenceId, true, seWeight));
					}
				}
			}
		}
		
		//add sentence-sentence edge
		for (int i=0;i<sentences.size();i++) {
			for (int j=0;j<sentences.size();j++) {
				if (i==j)
					continue;
				int sentenceId1=graph.getSentenceId(sentences.get(i));
				int sentenceId2=graph.getSentenceId(sentences.get(j));
				if (sentenceId1==sentenceId2)
					continue;
			//	System.out.println(sentenceId1+"\t"+sentenceId2);
				double ssWeight=SSWeight.ssWeight(sentences.get(i), sentences.get(j));
				if (ssWeight>0) {
					graph.addEdge(new Edge(sentenceId1, sentenceId2, false, ssWeight));
					graph.addEdge(new Edge(sentenceId2, sentenceId1, false, ssWeight));
				}
			}
		}
		
		return graph;
	}
	
	

}
