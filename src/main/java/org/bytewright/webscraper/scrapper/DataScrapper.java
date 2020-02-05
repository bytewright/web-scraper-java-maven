package org.bytewright.webscraper.scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
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
  int counter = 0;

  public void scrape() {
    Map<URL, ScrapingResult> resultList = new LinkedHashMap<>();
    for (URL url : urlDataSource.getUrls((url -> dataExtractor.isValidUrl(url)))) {
      if (dataExtractor.isAlreadyDownloaded(url) || resultList.containsKey(url)) {
        continue;
      }

      LOGGER.info("Trying to scrape url: {}", url);
      try (WebClient client = webClientFactory.createClient()) {
        HtmlPage page = client.getPage(url);
        Optional<ScrapingResult> result = dataExtractor.extractResult(page);
        result.ifPresent(scrapingResult -> resultList.put(url, scrapingResult));
        counter++;
        if (counter > 5)
          break;
      } catch (IOException e) {
        LOGGER.error("Failed to scrape url {}", url, e);
      } catch (Exception e) {
        LOGGER.error("Something went wrong while scraping url {}", url, e);
      }
    }
    LOGGER.info("Scrapping finished, got {} ScrapingResult(s)", resultList.size());
    dataExtractor.persistResult(resultList.values());
  }
}
