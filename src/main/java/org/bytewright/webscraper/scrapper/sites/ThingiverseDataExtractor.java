package org.bytewright.webscraper.scrapper.sites;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bytewright.webscraper.scrapper.DataExtractor;
import org.bytewright.webscraper.scrapper.ScraperSettings;
import org.bytewright.webscraper.scrapper.ScrapingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Component
public class ThingiverseDataExtractor implements DataExtractor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThingiverseDataExtractor.class);

  @Autowired
  private ScraperSettings scraperSettings;

  @Override
  public Optional<ScrapingResult> extractResult(HtmlPage page) {
    LOGGER.debug("Scraping page: {}", page.getUrl());
    DomElement descriptionElem = page.getElementById("description");
    String description = descriptionElem.asText();
    LOGGER.debug("Found desc: {}", description);
    List<String> tags = parseTags(page.getAnchors());
    LOGGER.debug("Found Tags: {}", tags);
    List<String> imgs = parseGallery(page);
    LOGGER.debug("Found imgs: {}", imgs);
    LOGGER.debug("found anchors: {}", page.getAnchors());
    return Optional.empty();
  }

  private List<String> parseGallery(HtmlPage htmlPage) {
    HtmlElement abc = htmlPage.getFirstByXPath("/html/body/div[1]/div[2]/div/div/div[2]/div/div[2]/div[1]/div[1]/div[2]");
    return abc != null ? List.of(abc.asText()) : List.of();
  }

  private List<String> parseTags(List<HtmlAnchor> anchors) {
    return anchors.stream()
        .filter(htmlAnchor -> htmlAnchor.getHrefAttribute().contains("tag:"))
        .map(HtmlAnchor::getFirstChild)
        .map(DomNode::asText)
        .collect(Collectors.toList());
  }

  @Override
  public void persistResult(List<ScrapingResult> resultList) {
    List<ThingiverseScrapingResult> scrapingResultList = resultList.stream()
        .filter(scrapingResult -> scrapingResult instanceof ThingiverseScrapingResult)
        .map(scrapingResult -> (ThingiverseScrapingResult) scrapingResult)
        .sorted(Comparator.comparing(ThingiverseScrapingResult::getThingiverseId))
        .collect(Collectors.toList());
    scrapingResultList.forEach(this::log);

  }

  private void log(ThingiverseScrapingResult thingiverseScrapingResult) {
    LOGGER.info("Scraped id {}: {}", thingiverseScrapingResult.getThingiverseId(), thingiverseScrapingResult);
  }
}
