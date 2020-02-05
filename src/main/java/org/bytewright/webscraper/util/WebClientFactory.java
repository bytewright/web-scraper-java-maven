package org.bytewright.webscraper.util;

import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.WebClient;

@Component
public class WebClientFactory {
  public WebClient createClient() {
    WebClient client = new WebClient();
    client.getOptions().setCssEnabled(false);
    client.getOptions().setJavaScriptEnabled(false);
    return client;
  }

  public WebClient createZipDownloader() {
    WebClient client = new WebClient();
    client.getOptions().setCssEnabled(false);
    client.getOptions().setJavaScriptEnabled(false);
    client.getOptions().setTimeout(60 * 1000);
    return client;
  }
}
