package kr.popit.searcher;

import java.sql.Timestamp;

public class Post {
  private long id;
  private String loginName;
  private String displayName;
  private String content;
  private String title;
  private String postName;
  private Timestamp postDate;
  private String[] categories;
  private String[] tags;
  private String highlightedText;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Timestamp getPostDate() {
    return postDate;
  }

  public void setPostDate(Timestamp postDate) {
    this.postDate = postDate;
  }

  public String[] getCategories() {
    return categories;
  }

  public void setCategories(String[] categories) {
    this.categories = categories;
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
  }

  public String getPostName() {
    return postName;
  }

  public void setPostName(String postName) {
    this.postName = postName;
  }

  public String getHighlightedText() {
    return highlightedText;
  }

  public void setHighlightedText(String highlightedText) {
    this.highlightedText = highlightedText;
  }
}
