package OtusPro;
import static org.junit.jupiter.api.Assertions.assertTrue;

import extensions.UIExtensions;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.CourseCatalogPage;
import pages.MainPage;

@ExtendWith(UIExtensions.class)
public class MainPageTest {

  @Inject
  private MainPage mainPage;
  @Inject
  private CourseCatalogPage courseCatalogPage;

  @Test
  public void checkingCourseCategory() {
    String selectedCategory = mainPage
        .open()
        .openTeachingMenu()
        .selectRandomCourseCategory()
        .getSelectedCategoryName();

    int categoryIndex = courseCatalogPage.getCategoryIndex(selectedCategory);

    boolean isChecked = courseCatalogPage.isCheckboxSelectedByIndex(categoryIndex);
    assertTrue(isChecked, "Чекбокс напротив выбранной категории не активен.");

    System.out.println("Чекбокс напротив '" + selectedCategory + "' выбран.");
  }

}
