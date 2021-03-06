package org.bytewright.webscraper.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UrlDataSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(UrlDataSource.class);
  private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
  private static final String INPUT_DATA_DIR = "data/input";
  private Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

  public List<URL> getUrls(Predicate<URL> urlFilterFunction) {
    List<URL> urls = new LinkedList<>(getUrlsFromDataDir(urlFilterFunction));
    LOGGER.info("Found {} urls in datadir, first: {}", urls.size(), urls.isEmpty() ? null : urls.get(0));
    return urls;
  }

  private Collection<? extends URL> getUrlsFromDataDir(Predicate<URL> urlFilterFunction) {
    try {
      Path path = Paths.get(INPUT_DATA_DIR);
      if (!Files.isDirectory(path)) {
        LOGGER.error("Expecting links in txt files in dir: {}", path.toAbsolutePath());
        throw new IllegalStateException();
      }
      return Files.walk(path)
          .filter(Files::isRegularFile)
          .peek(path1 -> LOGGER.debug("parsing file {}", path1.toAbsolutePath()))
          .map(this::getUrlsFromFile)
          .peek(urls -> LOGGER.debug("Got {} urls from last file", urls.size()))
          .flatMap(Collection::stream)
          .filter(urlFilterFunction)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      LOGGER.error("Failed to read urls from data dir", e);
      return List.of();
    }
  }

  private Collection<? extends URL> getUrlsFromFile(Path path) {
    List<URL> urlList = new LinkedList<>();

    try {
      return Files.lines(path)
          .map(StringUtils::strip)
          .filter(s -> URL_PATTERN.matcher(s).matches())
          .map(this::toUrl)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return urlList;
  }

  private URL toUrl(String line) {
    try {
      return new URL(line);
    } catch (MalformedURLException e) {
      LOGGER.error("Failed to parse url from {}", line, e);
    }
    return null;
  }
}
