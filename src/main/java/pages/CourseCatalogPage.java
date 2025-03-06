package pages;

import annotations.Path;
import com.google.inject.Inject;
import commons.waiters.Waiters;
import org.assertj.core.api.SoftAssertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import services.CourseService;
import services.CategoryService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Path("/catalog/courses")
public class CourseCatalogPage extends AbsBasePage<CourseCatalogPage> {

  private static final By SHOW_MORE_BUTTON = By.cssSelector("button.sc-mrx253-0.enxKCy.sc-prqxfo-0.cXVWAS");
  private static final By SEARCH_INPUT = By.cssSelector("input[type='search']");
  private static final By COURSE_DATES = By.cssSelector("#__next section.sc-o4bnil-0 div.sc-18q05a6-0 > div > a > div.sc-1x9oq14-0 > div > div");
  private static final By COURSE_NAME = By.cssSelector("#__next section.sc-o4bnil-0 div.sc-18q05a6-0 > div > a > h6 > div");
  private static final By COURSE_TITLES = By.cssSelector("a.sc-zzdkm7-0 h6.sc-1x9oq14-0");
  private static final By LINKS = By.cssSelector("#__next > div.sc-1j17uuq-0.klmZDZ.sc-1u2d5lq-0.oYOFo > main > div > section.sc-o4bnil-0.riKpM > div.sc-18q05a6-0.incGfX > div a[href^='/']");
  private static final By CHECKBOXES_LOCATOR = By.cssSelector("input.sc-1fry39v-3.iDiEdJ[type='checkbox']");
  private static final By CATEGORY_NAMES_LOCATOR = By.cssSelector("label.sc-1x9oq14-0-label");

  private static final String TITLE_NAME_COURSE = ".sc-1ddwpfq-1 h1";
  private static final String START_DATE_COURSE = "div.sc-x072mc-0.hOtCic.galmep div.sc-3cb1l3-4.kGoYMV:first-child > p.sc-1x9oq14-0.sc-3cb1l3-0.dgWykw";

  private final CourseService courseService;
  private final CategoryService categoryService;
  private final Waiters waiters;

  private List<Integer> courseIndexes;

  @Inject
  public CourseCatalogPage(WebDriver driver, Waiters waiters) {
    super(driver);
    this.waiters = waiters;
    this.courseService = new CourseService(driver, waiters);
    this.categoryService = new CategoryService(driver);
  }

  public String getPageHeader() {
    return driver.findElement(By.tagName("h1")).getText();
  }

  public CourseCatalogPage searchForCourse(String courseName) {
    driver.findElement(SEARCH_INPUT).sendKeys(courseName);
    waiters.waitForElementVisible(COURSE_TITLES);
    return this;
  }

  public CourseCatalogPage findAndClickCourseByName(String courseName) {
    List<WebElement> courses = driver.findElements(COURSE_NAME);
    WebElement targetCourse = courses.stream()
        .filter(course -> course.getText().equals(courseName))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Курс с именем " + courseName + " не найден"));

    highlightElement(targetCourse, "3px solid #0000ff");
    addFocusListener(targetCourse);

    targetCourse.click();
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

  public CourseCatalogPage clickShowMoreButtonUntilAllLoaded() {
    boolean buttonFound = true;

    while (buttonFound) {
      try {
        WebElement button = driver.findElement(SHOW_MORE_BUTTON);
        if (button.isDisplayed()) {
          ((JavascriptExecutor) driver).executeScript(
              "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
              button
          );

          waiters.waitForElementClickable(button);
          button.click();

          waiters.waitForCondition(webDriver -> {
            try {
              WebElement newButton = driver.findElement(SHOW_MORE_BUTTON);
              return newButton.isDisplayed();
            } catch (StaleElementReferenceException e) {
              return false;
            }
          });
        }
      } catch (NoSuchElementException e) {
        buttonFound = false;
      } catch (Exception e) {
        buttonFound = false;
      }
    }

    return this;
  }

  public CourseCatalogPage findCoursesWithEarliestAndLatestDates() {
    List<String> courseDates = courseService.getCourseDates(COURSE_DATES);
    List<LocalDate> parsedDates = parseCourseDates(courseDates);

    this.courseIndexes = new ArrayList<>();
    this.courseIndexes.addAll(findCourseIndexesWithEarliestDate(parsedDates));
    this.courseIndexes.addAll(findCourseIndexesWithLatestDate(parsedDates));

    return this;
  }

  private List<Integer> findCourseIndexesWithEarliestDate(List<LocalDate> dates) {
    return findCourseIndexesByCondition(dates, LocalDate::compareTo, true);
  }

  private List<Integer> findCourseIndexesWithLatestDate(List<LocalDate> dates) {
    return findCourseIndexesByCondition(dates, LocalDate::compareTo, false);
  }

  private List<Integer> findCourseIndexesByCondition(List<LocalDate> dates, Comparator<LocalDate> comparator, boolean isEarliest) {
    if (dates == null || dates.isEmpty()) return Collections.emptyList();

    LocalDate targetDate = dates.stream()
        .filter(Objects::nonNull)
        .reduce((date1, date2) -> isEarliest
            ? comparator.compare(date1, date2) < 0 ? date1 : date2
            : comparator.compare(date1, date2) > 0 ? date1 : date2)
        .orElse(null);

    if (targetDate == null) return Collections.emptyList();

    return IntStream.range(0, dates.size())
        .filter(i -> targetDate.equals(dates.get(i)))
        .boxed()
        .collect(Collectors.toList());
  }

  private List<LocalDate> parseCourseDates(List<String> courseDates) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy");
    return courseDates.stream()
        .map(date -> {
          if (date.contains("О дате старта будет объявлено позже")) {
            return null;
          }
          try {
            String dateString = date.split(" · ")[0];
            return LocalDate.parse(dateString, formatter);
          } catch (Exception e) {
            return null;
          }
        })
        .collect(Collectors.toList());
  }

  public String getCourseLinkByIndex(int index) {
    List<WebElement> courseElements = driver.findElements(LINKS);
    if (index < 0 || index >= courseElements.size()) {
      System.out.println("Не удалось найти курс с индексом " + index + ".");
      return null;
    }

    WebElement courseElement = courseElements.get(index);
    String href = courseElement.getAttribute("href");

    if (href == null || href.isEmpty()) {
      System.out.println("Ссылка для индекса " + index + " не найдена.");
      return null;
    }

    System.out.println("Ссылка на курс с индексом " + index + ": " + href);
    return href;
  }

  public CourseCatalogPage verifyCoursesOnLinks() throws IOException {
    if (this.courseIndexes == null || this.courseIndexes.isEmpty()) {
      throw new IllegalStateException("Список индексов курсов пуст или не инициализирован.");
    }

    List<String> courseNames = courseService.getCourseNames(COURSE_NAME);
    List<String> courseDates = courseService.getCourseDates(COURSE_DATES);
    SoftAssertions softAssertions = new SoftAssertions();

    for (int index : courseIndexes) {
      String courseLink = getCourseLinkByIndex(index);
      Document coursePage = Jsoup.connect(courseLink).get();

      String actualCourseTitle = coursePage.select(TITLE_NAME_COURSE).text();
      String expectedCourseTitle = courseNames.get(index);
      softAssertions.assertThat(actualCourseTitle)
          .as("Не прошла проверка названия курса для индекса " + index)
          .isEqualTo(expectedCourseTitle);

      if (actualCourseTitle.equals(expectedCourseTitle)) {
        System.out.println("Название курса совпадает с ожидаемым: " + expectedCourseTitle);
      } else {
        System.err.println("Ошибка: название курса не совпадает. Ожидалось: '" + expectedCourseTitle + "', но найдено: '" + actualCourseTitle + "'");
      }

      String actualCourseDate = coursePage.select(START_DATE_COURSE).text();
      String expectedCourseDate = courseDates.get(index)
          .replaceAll(" · .*", "")
          .replaceAll(",\\s*\\d{4}", "");
      softAssertions.assertThat(actualCourseDate)
          .as("Не прошла проверка даты курса для индекса " + index)
          .isEqualTo(expectedCourseDate);

      if (actualCourseDate.equals(expectedCourseDate)) {
        System.out.println("Дата начала курса совпадает с ожидаемой: " + expectedCourseDate);
      } else {
        System.err.println("Ошибка: дата курса не совпадает. Ожидалось: '" + expectedCourseDate + "', но найдено: '" + actualCourseDate + "'");
      }
    }

    softAssertions.assertAll();
    return this;
  }
  public int getCategoryIndex(String categoryName) {
    return categoryService.getCategoryIndex(CATEGORY_NAMES_LOCATOR, categoryName);
  }

  public boolean isCheckboxSelectedByIndex(int index) {
    List<WebElement> checkboxes = driver.findElements(CHECKBOXES_LOCATOR);
    return checkboxes.get(index).isSelected();
  }
}