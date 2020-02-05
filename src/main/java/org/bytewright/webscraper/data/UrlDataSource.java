package org.bytewright.webscraper.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UrlDataSource {
  public List<URL> getUrls() {
    URL url = null;
    try {
      url = new URL("https://www.novomind.com/de/");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return List.of(url);
  }
}
