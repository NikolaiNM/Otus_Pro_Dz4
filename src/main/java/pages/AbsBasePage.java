package pages;
import static org.assertj.core.api.Assertions.assertThat;

import annotations.Path;
import annotations.UrlData;
import annotations.UrlTemplate;
import commons.AbsCommons;
import exceptions.PathPageExceptions;
import exceptions.UrlDataExceptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public abstract class AbsBasePage<T> extends AbsCommons {

  private String baseUrl = System.getProperty("base.url");

  public AbsBasePage(WebDriver driver) {
    super(driver);

    baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }

  @FindBy(tagName = "h1")
  private WebElement header;

  public T pageHeaderShouldBeSameAs(String expectedHeader) {
    String actualHeader = header.getText();

    assertThat(actualHeader)
        .as("Expected header: '%s', but found: '%s'", expectedHeader, actualHeader)
        .isNotBlank()
        .isEqualTo(expectedHeader);

    System.out.println("Заголовки совпали: '" + expectedHeader + "'");

    return (T)this;
  }

  private String getUrlTemplate(String name) {
    Class clazz = getClass();
    if(clazz.isAnnotationPresent(UrlData.class)) {
      UrlData urlData = (UrlData)clazz.getDeclaredAnnotation(UrlData.class);

      UrlTemplate[] urlTemplates = urlData.value();

      assertThat(Arrays.stream(urlTemplates).distinct().toList().size())
          .as("Page should not contains url templates with same names")
          .isEqualTo(urlTemplates.length);

      return Arrays.stream(urlTemplates)
          .filter((UrlTemplate urlTemplate) -> urlTemplate.name().equals(name))
          .findFirst().get()
          .template();
    }

    throw new UrlDataExceptions();
  }

  private String getPath() {
    Class<? extends AbsBasePage> clazz = this.getClass();

    if(clazz.isAnnotationPresent(Path.class)) {
      Path path = clazz.getDeclaredAnnotation(Path.class);

      return path.value().startsWith("/") ? path.value() : "/" + path.value();
    }

    return "";
  }

  public Object page(Class<? extends AbsBasePage> clazz) {
    Constructor<? extends AbsBasePage> constructor = null;
    try {
      constructor = clazz.getConstructor(WebDriver.class);
      return constructor.newInstance(driver);
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public T open() {
    String path = getPath();
    if(path.isEmpty()) {
      throw new PathPageExceptions();
    }

    driver.get(baseUrl + path);

    return (T)this;
  }

  public T open(String nameTemaplate, String... params) {
    String urlTemplate = getUrlTemplate(nameTemaplate);
    String path = getPath();
    for(int i = 0; i < params.length; i++) {
      urlTemplate = urlTemplate.replace("$" + (i + 1), params[i]);
    }
    if(!path.isEmpty()) {
      urlTemplate = path + urlTemplate;
    }

    driver.get(baseUrl + urlTemplate);

    return (T)this;
  }

}
