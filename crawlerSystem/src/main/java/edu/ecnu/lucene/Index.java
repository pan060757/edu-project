package edu.ecnu.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ecnu.util.Config;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;




public class Index {
	
	
	static Index instance=null;
	String indexPath = Config.INDEX_PATH;
	Directory dir =null;
	SmartChineseAnalyzer analyzer=null;
    QueryParser queryParser = null;
	
    public static Index getInstance() {
		if (instance == null)
			instance = new Index(Config.INDEX_PATH);
		return instance;
	}


	public static Index getInstance(String indexPath) {
		if (instance == null)
			instance = new Index(indexPath);
		return instance;
	}

	private Index(String indexPath){
		this.indexPath=indexPath;
		initial();
	}
	public SmartChineseAnalyzer getAnalyzer() {
		return analyzer;
	}
	public Index(){
		initial();
	}


	private void initial(){
		try {
			dir = FSDirectory
					.open(new File(this.indexPath));
			analyzer = new SmartChineseAnalyzer(Version.LUCENE_46, true);
			queryParser=new QueryParser(Version.LUCENE_46, "title",new SmartChineseAnalyzer(Version.LUCENE_46, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/***
	 * add the docs into index in batch!
	 * @param docs
	 */
	public void addInBatch(List<Document> docs){
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer); 
			indexWriter = new IndexWriter(dir,iwc);
			for(int i=0;i<docs.size();i++){
				indexWriter.addDocument(docs.get(i));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/***
	 * add the docs into index in batch!
	 * @param docs
	 */
	public void addInBatch(Document[] docs){
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer);
			indexWriter = new IndexWriter(dir,iwc);
			for(int i=0;i<docs.length;i++){
				indexWriter.addDocument(docs[i]);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	/**
	 *  add the doc to index
	 * 
	 * @param doc
	 */
	public void add(Document doc) {
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer); 
			indexWriter = new IndexWriter(dir,iwc);
			indexWriter.addDocument(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void delete(Term term) {
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer); 
			indexWriter = new IndexWriter(dir,iwc);
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public  void merge(String[] fromPath, String toPath){
		Directory[] directories = new Directory[fromPath.length];
		try{

			for (int i = 0; i <fromPath.length ; i++) {
				directories[i] = FSDirectory.open(new File(fromPath[i]));
			}

			Directory output=FSDirectory.open(new File(toPath));//合并到索引3里面

			IndexWriter writer=new IndexWriter(output, new IndexWriterConfig(Version.LUCENE_46, analyzer));

			System.out.println("开始合并索引.........");
			writer.addIndexes(directories);//传入各自的Diretory或者IndexReader进行合并
			writer.commit();//提交索引
			writer.forceMerge(1);
			writer.close();
			System.out.println("合并索引完毕.........");


		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void optimize(){
		IndexWriter indexWriter=null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer); 
			indexWriter = new IndexWriter(dir,iwc);
			indexWriter.forceMerge(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update(Term term, Document doc) {
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer); 
			indexWriter = new IndexWriter(dir,iwc);
			indexWriter.updateDocument(term, doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(indexWriter!=null){
					indexWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * <pre>
	 * totalPage = recordCount / pageSize;
	 * if (recordCount % pageSize &gt; 0)
	 * 	totalPage++;
	 * </pre>
	 * 
	 * @param queryString
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
//	public QueryResult search(String queryString, int firstResult, int maxResults) {
//		try {
			// 
			/*
			 * 
			 * PhraseQuery
			PhraseQuery phraseQuery = new PhraseQuery();
			String[] phrase=new MMAnalyzer().segment(queryString, "\t").split("\t");
			for(int i=0;i<phrase.length;i++){
				phraseQuery.add(new Term("title", phrase[i]));
			}
			phraseQuery.setSlop(10);
			return search(phraseQuery, firstResult, maxResults);
			*/
			
			/*
			 * 
			 * TermQuery
			Term term=new Term("title",queryString);
			TermQuery query= new TermQuery(term);
			return search(query, firstResult, maxResults);
			*/
			
//			Query query = queryParser.parse(queryString);
//			return search(query, firstResult, maxResults);
			/*
			 * 
			 * WildcardQuery
			String[] phrase=new MMAnalyzer().segment(queryString, "\t").split("\t");
			for(int i=0;i<phrase.length;i++){
				//phraseQuery.add(new Term("title", phrase[i]));
				queryString+=phrase[i]+"*";
			}
			Term term = new Term("title", queryString);
			Query query = new WildcardQuery(term);

			return search(query, firstResult, maxResults);
						*/
//			
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}



	public QueryResult search(String[] queryString, int firstResult, int maxResults) {
		IndexSearcher indexSearcher = null;
		try {
			
			PhraseQuery query=new PhraseQuery();
			query.setSlop(3);
			
			for(int i=0;i<queryString.length;i++){
				query.add(new Term("title",queryString[i]));
			}
			
			indexSearcher = new IndexSearcher(DirectoryReader.open(dir));    
			Filter filter=null;
			Sort sort = new Sort();
			sort.setSort( new SortField("time", SortField.Type.LONG, false)); 
			
			TopDocs topDocs = indexSearcher.search(query, filter, 100, sort);
			int recordCount = topDocs.totalHits;
			Map<Integer,Document> recordList = new HashMap<Integer,Document>();

			int end = Math.min(firstResult + maxResults, topDocs.totalHits);
			for (int i = firstResult; i < end; i++) {
				ScoreDoc scoreDoc = topDocs.scoreDocs[i];
				
				int docSn = scoreDoc.doc; //
				Document doc = indexSearcher.doc(docSn); 
				recordList.put(i, doc);
			}
			return new QueryResult(recordCount, recordList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
