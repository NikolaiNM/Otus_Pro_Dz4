package factory.settings;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;

public class ChromeSettings implements IBrowserSettings {
  @Override
  public AbstractDriverOptions settings() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    return options;
  }
}