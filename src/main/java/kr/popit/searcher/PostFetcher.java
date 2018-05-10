package kr.popit.searcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostFetcher {
  private final JdbcTemplate jdbcTemplate;

  private String postSql =
          "  select \n" +
          "    a.ID, a.post_author, a.post_content, a.post_title, a.post_date, a.post_name, \n" +
          "    b.display_name, b.user_login \n" +
          "  from  \n" +
          "    wprdh0703_posts as a \n" +
          "  inner join  \n" +
          "    wprdh0703_users as b on a.post_author = b.ID \n" +
          "  where  \n" +
          "    a.post_status = 'publish' \n" +
          "  and  \n" +
          "    a.post_type = 'post' \n";

  private String tagSql =
      "select wprdh0703_terms.name, wprdh0703_term_taxonomy.taxonomy \n" +
      "  from wprdh0703_terms \n" +
      "inner join  \n" +
      "  wprdh0703_term_taxonomy on wprdh0703_terms.term_id = wprdh0703_term_taxonomy.term_id \n" +
      "inner join  \n" +
      "  wprdh0703_term_relationships on wprdh0703_term_taxonomy.term_taxonomy_id = wprdh0703_term_relationships.term_taxonomy_id \n" +
      "where  \n" +
      "  wprdh0703_term_relationships.object_id = ? \n";

  public PostFetcher(@Value("${spring.datasource.url}") String datasourceUrl,
                     @Value("${spring.datasource.username}") String datasourceUsername,
                     @Value("${spring.datasource.password}") String datasourcePassword) throws SQLException {
    String password = (datasourcePassword == null || datasourcePassword.trim().isEmpty()) ? null : datasourcePassword;

    SimpleDriverDataSource dataSource = new SimpleDriverDataSource(
        new com.mysql.jdbc.Driver(),
        datasourceUrl,
        datasourceUsername,
        password);

    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<Post> fetchAllPosts() throws IOException, SQLException {
    List<Map<String, Object>> results = this.jdbcTemplate.queryForList(postSql);

    List<Post> posts = new ArrayList<>();
    if (results.size() == 0) {
      return posts;
    }

    for (Map<String, Object> map : results) {
      BigInteger postId = (BigInteger) map.get("id");

      String loginName = (String) map.get("user_login");
      String displayName = (String) map.get("display_name");
      String content =(String) map.get("post_content");
      String title = (String) map.get("post_title");
      String postName = (String) map.get("post_name");
      Timestamp postDate = (Timestamp) map.get("post_date");

      Post post = new Post();
      post.setId(postId.longValue());
      post.setLoginName(loginName);
      post.setDisplayName(displayName);
      post.setContent(content);
      post.setTitle(title);
      post.setPostName(postName);
      post.setPostDate(postDate);

      List<List<String>> terms = getTerms(postId.longValue());
      List<String> categories = terms.get(0);
      List<String> tags = terms.get(1);

      post.setCategories(categories.toArray(new String[]{}));
      post.setTags(tags.toArray(new String[]{}));

      posts.add(post);
    }

    return posts;
  }

  private List<List<String>> getTerms(long postId) throws IOException, SQLException{
    List<Map<String, Object>> results = this.jdbcTemplate.queryForList(tagSql, postId);

    List<List<String>> terms = new ArrayList<List<String>>();

    List<String> categories = new ArrayList<>();
    List<String> tags = new ArrayList<>();

    for (Map<String, Object> map: results) {
      String taxonomy = (String)map.get("taxonomy");
      String name = (String)map.get("name");
      if (taxonomy.equals("category")) {
        categories.add(name);
      } else if (taxonomy.equals("post_tag")) {
        tags.add(name);
      }
    }

    terms.add(categories);
    terms.add(tags);

    return terms;
  }
}
