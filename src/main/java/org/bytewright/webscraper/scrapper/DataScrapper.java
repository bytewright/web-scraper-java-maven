package org.bytewright.webscraper.scrapper;

import java.io.IOException;
import java.net.URL;

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

  public void scrape() {
    for (URL url : urlDataSource.getUrls()) {
      LOGGER.info("trying to scrape url: {}", url);
      try (WebClient client = webClientFactory.createClient()) {
        HtmlPage page = client.getPage(url);
        LOGGER.info("Got page: {}", page);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
