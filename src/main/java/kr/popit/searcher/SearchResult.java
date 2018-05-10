package kr.popit.searcher;

import java.util.List;

public class SearchResult {
  private long totalHits;
  private List<Post> posts;

  public SearchResult(long totalHits, List<Post> posts) {
    this.totalHits = totalHits;
    this.posts = posts;
  }

  public long getTotalHits() {
    return totalHits;
  }

  public void setTotalHits(long totalHits) {
    this.totalHits = totalHits;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }
}
