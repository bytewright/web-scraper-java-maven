package org.bytewright.webscraper.scrapper.sites;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bytewright.webscraper.scrapper.ScrapingResult;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Streams;

public class ThingiverseScrapingResult implements ScrapingResult {
  private final String thingiverseId;
  private final String summary;
  private final List<String> tags;
  private final List<String> images;
  private final String zipPath;

  public ThingiverseScrapingResult(String thingiverseId,
      String summary,
      List<String> tags,
      List<String> images,
      String zipPath) {
    this.thingiverseId = thingiverseId;
    this.summary = summary;
    this.tags = tags;
    this.images = images;
    this.zipPath = zipPath;
  }

  public String getThingiverseId() {
    return thingiverseId;
  }

  public String getSummary() {
    return summary;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<String> getImages() {
    return images;
  }

  public String getZipPath() {
    return zipPath;
  }

  @Override
  public boolean isValid() {
    return thingiverseId != null;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("thingiverseId", thingiverseId)
        .add("tags", tags)
        .add("images", images)
        .add("zipPath", zipPath)
        .add("summary", summary)
        .toString();
  }

  public Iterable<? extends CharSequence> getInfoCsvLines() {
    LinkedList<String> lines = new LinkedList<>();
    lines.add("id;tags;summay");
    String dataLine = Streams.concat(
        Stream.of(thingiverseId),
        tags.stream(),
        Stream.of(summary))
        .collect(Collectors.joining(";"));
    lines.add(dataLine);
    return lines;
  }
}
