package org.bytewright.webscraper.scrapper;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface DataExtractor {
  Optional<ScrapingResult> extractResult(HtmlPage page);

  void persistResult(Collection<ScrapingResult> resultList);

  boolean isAlreadyDownloaded(URL url);

  boolean isValidUrl(URL url);
}
