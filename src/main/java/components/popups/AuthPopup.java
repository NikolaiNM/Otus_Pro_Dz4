package components.popups;

import commons.AbsCommons;
import org.openqa.selenium.WebDriver;

public class AuthPopup extends AbsCommons implements IPopup<AuthPopup> {

  public AuthPopup(WebDriver driver) {
    super(driver);
  }

  @Override
  public AuthPopup popupShouldNotBeVisible() {
    return null;
  }

  @Override
  public AuthPopup popupShouldBeVisible() {
    return null;
  }
}
