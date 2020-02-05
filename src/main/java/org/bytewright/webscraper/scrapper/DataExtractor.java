package org.bytewright.webscraper.scrapper;

import java.util.List;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface DataExtractor {
  Optional<ScrapingResult> extractResult(HtmlPage page);

  void persistResult(List<ScrapingResult> resultList);
}
