package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import commons.waiters.Waiters;
import components.AdsBlock;
import factory.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import pages.CourseCatalogPage;
import pages.MainPage;

public class GuiceModule extends AbstractModule {

  private WebDriver driver;

  public GuiceModule() {
    driver = new WebDriverFactory().create();
  }

  @Provides
  private WebDriver getDriver() {
    return this.driver;
  }

  @Singleton
  @Provides
  public MainPage getMainPage(WebDriver driver, Waiters waiters) {
    return new MainPage(driver, waiters);
  }

  @Singleton
  @Provides
  public CourseCatalogPage getCourseCatalogPage(WebDriver driver, Waiters waiters) {
    return new CourseCatalogPage(driver, waiters);
  }

  @Singleton
  @Provides
  public AdsBlock getAdbBlock() {
    return new AdsBlock(driver);
  }

}
