package services;

import commons.waiters.Waiters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService {
  private final WebDriver driver;
  private final Waiters waiters;

  public CourseService(WebDriver driver, Waiters waiters) {
    this.driver = driver;
    this.waiters = waiters;
  }

  public List<String> getCourseNames(By locator) {
    return driver.findElements(locator).stream()
        .map(WebElement::getText)
        .collect(Collectors.toList());
  }

  public List<String> getCourseDates(By locator) {
    return driver.findElements(locator).stream()
        .map(WebElement::getText)
        .collect(Collectors.toList());
  }
}