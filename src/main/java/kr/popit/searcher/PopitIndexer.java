package kr.popit.searcher;


import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

@Service
public class PopitIndexer {
  private IndexWriter indexWriter;

  public PopitIndexer(@Value("${indexer.data_dir}") String dataPath) throws Exception {
    this.indexWriter = this.createIndexer(Paths.get(dataPath));
  }

  private IndexWriter createIndexer(Path path) throws IOException {
    if (Files.notExists(path)) {
      Files.createDirectories(path);
    }

    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new KoreanAnalyzer());
    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

    return new IndexWriter(FSDirectory.open(path), indexWriterConfig);
  }

  public void close() throws IOException {
    this.indexWriter.close();
  }

  public void createPostIndex(Post post) throws IOException {
    Document doc = new Document();
    doc.add(new StringField("post_id", String.valueOf(post.getId()), Field.Store.YES));
    doc.add(new TextField("title", post.getTitle(), Field.Store.YES));
    doc.add(new TextField("content", Jsoup.parse(post.getContent()).text(), Field.Store.YES));
    doc.add(new TextField("author", post.getLoginName(), Field.Store.YES));
    doc.add(new TextField("author", post.getDisplayName(), Field.Store.YES));
    doc.add(new StringField("publishedAt", DateTools.timeToString(post.getPostDate().getTime(), DateTools.Resolution.MINUTE),
        Field.Store.YES));

    for (String eachCategory: post.getCategories()) {
      doc.add(new TextField("category", eachCategory, Field.Store.NO));
    }

    for (String eachTag: post.getTags()) {
      doc.add(new TextField("tag", eachTag, Field.Store.NO));
    }

    this.indexWriter.updateDocument(new Term("id", Long.toString(post.getId())), doc);
  }

  public void commit() throws IOException {
    this.indexWriter.flush();
    this.indexWriter.commit();
  }
}
