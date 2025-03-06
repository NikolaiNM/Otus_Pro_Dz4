package factory.remote;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteDriverManager {
  public static WebDriver createDriver(String remoteUrl, AbstractDriverOptions options) {
    try {
      RemoteCapabilitiesConfigurator.configure(options);
      return new RemoteWebDriver(new URL(remoteUrl), options);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid remote URL: " + remoteUrl, e);
    }
  }
}