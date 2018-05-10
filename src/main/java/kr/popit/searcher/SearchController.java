package kr.popit.searcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {
  @Autowired
  private PopitIndexRunner popitIndexRunner;

  @Autowired
  private PopitSearcher popitSearcher;

  @RequestMapping(value = "/api/search/{keyword}", method = RequestMethod.GET)
  @ResponseBody
  public SearchResult search(
      @PathVariable("keyword") String keyword,
      @RequestParam("page") Integer page) {
    if (page == null) {
      page = 1;
    }
    try {
      return popitSearcher.searchPost(keyword, page);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @RequestMapping(value = "/api/indexer", method = RequestMethod.GET)
  @ResponseBody
  public String runIndexer() {
    popitIndexRunner.startIndex();
    return "SUCCESS";
  }

}
