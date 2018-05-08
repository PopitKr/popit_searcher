package kr.popit.searcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PopitSearcher {
  public static final Log LOG = LogFactory.getLog(PopitSearcher.class);

  private Path indexPath;
  private IndexSearcher searcher;
  private IndexReader reader;
  private Object lock = new Object();

  public PopitSearcher(@Value("${indexer.data_dir}") String dataPath) {
    this.indexPath = Paths.get(dataPath);
  }

  public List<Long> searchPost(String keyword) throws ParseException, IOException, InvalidTokenOffsetsException {
    synchronized(lock) {
      if (this.searcher == null) {
        this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
        this.searcher = new IndexSearcher(reader);
      }
    }
    Analyzer analyzer = new KoreanAnalyzer();

    String[] fields = new String[]{"title", "tag", "category", "author", "content"};
    Map<String, Float> boost = new HashMap<>();
    boost.put("title", 3.0f);
    boost.put("tag", 3.0f);
    boost.put("category", 1.0f);
    boost.put("author", 3.0f);
    boost.put("content", 1.0f);

    Query finalQuery = new MultiFieldQueryParser(fields, analyzer, boost).parse(keyword);

    TopDocs hits = searcher.search(finalQuery, 50);
    Formatter formatter = new SimpleHTMLFormatter();
    QueryScorer scorer = new QueryScorer(finalQuery);
    Highlighter highlighter = new Highlighter(formatter, scorer);
    Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
    highlighter.setTextFragmenter(fragmenter);

    long totalHits = hits.totalHits;

    LOG.info(keyword + "==============> total hit:" + totalHits);
    List<Long> postIds = new ArrayList<Long>();
    for (ScoreDoc sd: hits.scoreDocs) {
      Document doc = searcher.doc(sd.doc);
      LOG.info(">>>>>" + doc.get("post_id") + ">" + doc.get("title"));
      postIds.add(Long.parseLong(doc.get("post_id")));
      String content = doc.get("content");
      TokenStream stream = TokenSources.getAnyTokenStream(this.reader, sd.doc, "content", doc, analyzer);
      String[] frags = highlighter.getBestFragments(stream, content, 5);
      for (String frag : frags)
      {
        System.out.println(frag);
      }
      System.out.println("=======================");
    }

    return postIds;
  }
}
