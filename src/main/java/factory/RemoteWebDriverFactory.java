package factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.AbstractDriverOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;

public class RemoteWebDriverFactory {

  private final String remoteUrl;

  public RemoteWebDriverFactory(String remoteUrl) {
    this.remoteUrl = remoteUrl;
  }

  public WebDriver createRemoteDriver(AbstractDriverOptions<?> options) {
    options.setCapability("browserVersion", "128.0");
    options.setCapability("selenoid:options", new HashMap<String, Object>() {{
        put("name", "Test badge...");
        put("sessionTimeout", "15m");
        put("env", new ArrayList<String>() {{
            add("TZ=UTC");
            }});
        put("labels", new HashMap<String, Object>() {{
            put("manual", "true");
          }});
        put("enableVideo", true);
      }});

    try {
      return new RemoteWebDriver(new URL(remoteUrl), options);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid remote URL: " + remoteUrl, e);
    }
  }
}