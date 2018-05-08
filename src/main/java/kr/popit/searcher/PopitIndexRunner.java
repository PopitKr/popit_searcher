package kr.popit.searcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class PopitIndexRunner {
  public static final Log LOG = LogFactory.getLog(PopitIndexRunner.class);

  @Autowired
  private PopitIndexer popitIndexer;
  @Autowired
  private PostFetcher postFetcher;

  private AtomicBoolean running = new AtomicBoolean(false);

  public void startIndex() {
    synchronized (running) {
      if (running.get()) {
        LOG.info("Index job is running");
        return;
      }
      running.set(true);
    }
    Thread t = new Thread() {
      public void run() {
        LOG.info("Index job started");
        doIndex();
        synchronized (running) {
          running.set(false);
        }
        LOG.info("Index job finished");
      }
    };
    t.start();
  }

  private void doIndex() {
    try {
      List<Post> posts = postFetcher.fetchAllPosts();
      for (Post eachPost: posts) {
        popitIndexer.createPostIndex(eachPost);
      }
      popitIndexer.commit();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
