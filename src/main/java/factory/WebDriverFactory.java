package factory;

import exceptions.BrowserNotSupportedException;
import factory.remote.RemoteDriverManager;
import factory.settings.ChromeSettings;
import factory.settings.IBrowserSettings;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import java.util.Locale;

public class WebDriverFactory {
  private final String browserName;
  private final String remoteUrl;

  public WebDriverFactory() {
    this.browserName = System.getProperty("browser.name", "chrome").trim().toLowerCase(Locale.ROOT);
    this.remoteUrl = System.getProperty("remote.url", "");
  }

  public WebDriver create() {
    IBrowserSettings settings = getBrowserSettings();
    AbstractDriverOptions options = settings.settings();

    if (!remoteUrl.isEmpty()) {
      return RemoteDriverManager.createDriver(remoteUrl, options);
    }

    return createLocalDriver(options);
  }

  private IBrowserSettings getBrowserSettings() {
    switch (browserName) {
      case "chrome":
        return new ChromeSettings();
      // Добавьте другие браузеры при необходимости
      default:
        throw new BrowserNotSupportedException(browserName);
    }
  }

  private WebDriver createLocalDriver(AbstractDriverOptions options) {
    WebDriverManager.chromedriver().setup();
    return new ChromeDriver((ChromeOptions) options);
  }
}