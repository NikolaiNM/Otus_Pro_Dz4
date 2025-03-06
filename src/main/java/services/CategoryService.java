package services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class CategoryService {
  private final WebDriver driver;

  public CategoryService(WebDriver driver) {
    this.driver = driver;
  }

  public int getCategoryIndex(By locator, String name) {
    List<WebElement> categories = driver.findElements(locator);
    return IntStream.range(0, categories.size())
        .filter(i -> categories.get(i).getText().equals(name))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("Категория не найдена: " + name));
  }
}