package org.bytewright.webscraper.scrapper.sites;

import java.util.List;

import org.bytewright.webscraper.scrapper.ScrapingResult;

import com.google.common.base.MoreObjects;

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
}
