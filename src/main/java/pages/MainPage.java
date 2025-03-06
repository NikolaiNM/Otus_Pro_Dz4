package pages;

import annotations.Path;
import com.google.inject.Inject;
import commons.waiters.Waiters;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import java.util.Random;

@Path("/")
public class MainPage extends AbsBasePage<MainPage> {

  private static final By MENU_TEACHING_BUTTON = By.cssSelector("span[title='Обучение']");
  private static final By COURSE_CATEGORIES_LOCATOR = By.cssSelector("a[href*='categories'][class*='dNitgt']");
  private String selectedCategoryName;
  private final WebDriver driver;
  private final Waiters waiters;

  @Inject
  public MainPage(WebDriver driver, Waiters waiters) {
    super(driver);
    this.driver = driver;
    this.waiters = waiters;
  }

  public MainPage openTeachingMenu() {
    WebElement teachingButton = $(MENU_TEACHING_BUTTON);
    waiters.waitForCondition(ExpectedConditions.elementToBeClickable(MENU_TEACHING_BUTTON));
    teachingButton.click();
    return this;
  }

  private WebElement getRandomElement(List<WebElement> elements) {
    int randomIndex = new Random().nextInt(elements.size());
    return elements.get(randomIndex);
  }

  public MainPage selectRandomCourseCategory() {
    waiters.waitForCondition(ExpectedConditions.visibilityOfElementLocated(COURSE_CATEGORIES_LOCATOR));

    List<WebElement> categories = $$(COURSE_CATEGORIES_LOCATOR);

    if (!categories.isEmpty()) {
      WebElement randomCategory = getRandomElement(categories);

      highlightElement(randomCategory, "3px solid #ff0000");
      addFocusListener(randomCategory);

      selectedCategoryName = randomCategory.getText().replaceAll("\\(.*\\)", "").trim();
      randomCategory.click();
    } else {
      throw new RuntimeException("Категории курсов не найдены.");
    }

    return this;
  }

  private void highlightElement(WebElement element, String style) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].style.border = arguments[1]", element, style);
  }

  private void addFocusListener(WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "arguments[0].addEventListener('focus', function() { "
            + "  this.style.boxShadow = '0 0 5px 2px rgba(0, 255, 0, 0.5)'; "
            + "});",
        element
    );
  }

  public String getSelectedCategoryName() {
    return selectedCategoryName;
  }
}