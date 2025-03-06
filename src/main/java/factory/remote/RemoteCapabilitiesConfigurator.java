package factory.remote;

import org.openqa.selenium.remote.AbstractDriverOptions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemoteCapabilitiesConfigurator {
  public static void configure(AbstractDriverOptions options) {
    String browserVersion = System.getProperty("browser.version", "128.0");
    options.setCapability("browserVersion", browserVersion);

    Map<String, Object> selenoidOptions = new HashMap<>();
    selenoidOptions.put("name", "Test badge...");
    selenoidOptions.put("sessionTimeout", "15m");
    selenoidOptions.put("env", Collections.singletonList("TZ=UTC"));
    selenoidOptions.put("labels", Collections.singletonMap("manual", "true"));
    selenoidOptions.put("enableVideo", true);

    options.setCapability("selenoid:options", selenoidOptions);
  }
}