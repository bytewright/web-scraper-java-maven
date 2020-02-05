package org.bytewright.webscraper.scrapper.sites;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bytewright.webscraper.scrapper.DataExtractor;
import org.bytewright.webscraper.scrapper.ScrapingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Component
public class ThingiverseDataExtractor implements DataExtractor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThingiverseDataExtractor.class);
  private static final String regex = "https://www\\.thingiverse\\.com/thing:([0-9]+)";
  private static final Pattern pattern = Pattern.compile(regex);

  @Override
  public Optional<ScrapingResult> extractResult(HtmlPage page) {
    DomElement descriptionElem = page.getElementById("description");
    String description = descriptionElem.asText();
    List<String> tags = parseTags(page.getAnchors());
    List<String> imgs = parseGallery(page);
    String thingId = parseThingId(page);
    String zipDownloadUrl = parseZipUrl(thingId);
    return createResult(thingId, description, tags, imgs, zipDownloadUrl);
  }

  private Optional<ScrapingResult> createResult(String thingId, String description, List<String> tags,
      List<String> imgs, String zipDownloadUrl) {
    ThingiverseScrapingResult result = new ThingiverseScrapingResult(thingId, description, tags, imgs, zipDownloadUrl);
    return result.isValid() ? Optional.of(result) : Optional.empty();
  }

  private String parseZipUrl(String thingId) {
    return "https://www.thingiverse.com/thing:" + thingId + "/zip";
  }

  private String parseThingId(HtmlPage page) {
    return parseThingId(page.getUrl());
  }

  private String parseThingId(URL url) {
    Matcher matcher = pattern.matcher(url.toString());
    if (!matcher.matches()) {
      throw new IllegalArgumentException("not thingiverse url: " + url.toString());
    }
    return matcher.group(1);
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
  public void persistResult(Collection<ScrapingResult> resultList) {
    List<ThingiverseScrapingResult> scrapingResultList = resultList.stream()
        .filter(scrapingResult -> scrapingResult instanceof ThingiverseScrapingResult)
        .map(scrapingResult -> (ThingiverseScrapingResult) scrapingResult)
        .sorted(Comparator.comparing(ThingiverseScrapingResult::getThingiverseId))
        .collect(Collectors.toList());

    for (ThingiverseScrapingResult result : scrapingResultList) {
      try {
        Path path = Paths.get("data/output", result.getThingiverseId());
        Path outDir = Files.createDirectories(path);
        Path infoPath = outDir.resolve("info.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(infoPath.toFile(), result);
        Files.write(outDir.resolve("info.csv"), result.getInfoCsvLines());
        for (String image : result.getImages()) {
          LOGGER.info("downloading img urls: {}", image);
        }
        downloadArchive(result, outDir);
      } catch (IOException e) {
        LOGGER.error("Failed to scrape id {}", result.getThingiverseId());
        e.printStackTrace();
      }
    }

  }

  @Override
  public boolean isAlreadyDownloaded(URL url) {
    String thingId = parseThingId(url);
    return Files.isDirectory(Paths.get("data/output", thingId));
  }

  @Override
  public boolean isValidUrl(URL url) {
    return pattern.matcher(url.toString()).matches();
  }

  private void downloadArchive(ThingiverseScrapingResult result, Path outDir) throws IOException {
    File file = outDir.resolve("archive.zip").toFile();
    LOGGER.info("Downloading zip from: {} to {}", result.getZipPath(), file.getAbsoluteFile());
    URL url = new URL(result.getZipPath());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    try (InputStream in = connection.getInputStream()) {
      try (FileOutputStream out = new FileOutputStream(file)) {
        copy(in, out, 2048);
      }
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
    byte[] buf = new byte[bufferSize];
    int n = input.read(buf);
    while (n >= 0) {
      output.write(buf, 0, n);
      n = input.read(buf);
    }
    output.flush();
  }

}
