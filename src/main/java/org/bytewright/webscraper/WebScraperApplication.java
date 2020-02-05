package org.bytewright.webscraper;

import org.bytewright.webscraper.scrapper.DataScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebScraperApplication implements ApplicationRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebScraperApplication.class);

  @Autowired
  private DataScrapper dataScrapper;

  public static void main(String[] args) {
    SpringApplication.run(WebScraperApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    LOGGER.info("Application context is initialised, starting app. Arguments: {}", args.getOptionNames());
    dataScrapper.scrape();
  }
}
