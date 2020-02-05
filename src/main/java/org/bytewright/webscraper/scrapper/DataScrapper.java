package org.bytewright.webscraper.scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bytewright.webscraper.data.UrlDataSource;
import org.bytewright.webscraper.util.WebClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * https://www.scrapingbee.com/blog/introduction-to-web-scraping-with-java/
 */
@Component
public class DataScrapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataScrapper.class);

  @Autowired
  private UrlDataSource urlDataSource;
  @Autowired
  private WebClientFactory webClientFactory;
  @Autowired
  private DataExtractor dataExtractor;

  public void scrape() {
    List<ScrapingResult> resultList = new LinkedList<>();
    for (URL url : urlDataSource.getUrls()) {
      LOGGER.info("trying to scrape url: {}", url);
      try (WebClient client = webClientFactory.createClient()) {
        HtmlPage page = client.getPage(url);
        Optional<ScrapingResult> result = dataExtractor.extractResult(page);
        result.ifPresent(resultList::add);
        break;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    LOGGER.info("Scrapping finished, got {} ScrapingResult(s)", resultList.size());
    dataExtractor.persistResult(resultList);
  }
}
