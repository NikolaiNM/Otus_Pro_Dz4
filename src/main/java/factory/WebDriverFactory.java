package factory;

import exceptions.BrowserNotSupportedException;
import factory.settings.ChromeSettings;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.Locale;

public class WebDriverFactory {

  private final String browserName = System.getProperty("browser.name").trim().toLowerCase(Locale.ROOT);
  private final String remoteUrl = System.getProperty("remote.url", "");

  public WebDriver create() {
    if (!remoteUrl.isEmpty()) {
      return createRemoteDriver();
    }

    return createLocalDriver();
  }

  private WebDriver createLocalDriver() {
    switch (browserName) {
      case "chrome": {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver((ChromeOptions) new ChromeSettings().settings());
      }
      default:
        throw new BrowserNotSupportedException(browserName);
    }
  }

  private WebDriver createRemoteDriver() {
    RemoteWebDriverFactory remoteFactory = new RemoteWebDriverFactory(remoteUrl);

    switch (browserName) {
      case "chrome": {
        ChromeOptions options = (ChromeOptions) new ChromeSettings().settings();
        return remoteFactory.createRemoteDriver(options);
      }
      default:
        throw new BrowserNotSupportedException(browserName);
    }
  }
}