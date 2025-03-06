package commons.waiters;

import com.google.inject.Inject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Waiters {

  private WebDriverWait webDriverWait;

  @Inject
  public Waiters(WebDriver driver) {
    this.webDriverWait = new WebDriverWait(
        driver,
        Duration.ofSeconds(Integer.parseInt(System.getProperty("webdriver.waiter.timeout")))
    );
  }

  public boolean waitForCondition(ExpectedCondition<?> condition) {
    try {
      webDriverWait.until(condition);
      return true;
    } catch (TimeoutException ignored) {
      return false;
    }
  }

  public boolean waitForElementByLocator(By locator) {
    return this.waitForCondition(ExpectedConditions.presenceOfElementLocated(locator));
  }

  public boolean waitForElementVisible(WebElement element) {
    return this.waitForCondition(ExpectedConditions.visibilityOf(element));
  }

  public boolean waitForElementVisible(By locator) {
    return this.waitForCondition(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  public boolean waitForElementClickable(By locator) {
    return this.waitForCondition(ExpectedConditions.elementToBeClickable(locator));
  }

  public boolean waitForElementClickable(WebElement element) {
    return this.waitForCondition(ExpectedConditions.elementToBeClickable(element));
  }
}