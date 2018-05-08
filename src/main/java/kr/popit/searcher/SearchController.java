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
  public List<Long> search(@PathVariable("keyword") String keyword) {
    try {
      return popitSearcher.searchPost(keyword);
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
