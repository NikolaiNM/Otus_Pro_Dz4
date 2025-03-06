package components;

import annotations.Component;
import org.openqa.selenium.WebDriver;

@Component("css:.ads")
public class AdsBlock extends AbsBaseComponent {

  public AdsBlock(WebDriver driver) {
    super(driver);
  }
}