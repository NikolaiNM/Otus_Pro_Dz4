package OtusPro;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import extensions.UIExtensions;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.CourseCatalogPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;


@ExtendWith(UIExtensions.class)
public class CourseCatalogTest {

  @Inject
  private CourseCatalogPage courseCatalogPage;

  @ParameterizedTest
  @ValueSource(strings = {"Нагрузочное тестирование", "Разработчик на Spring Framework", "Java Developer. Advanced"})
  public void testSearchAndOpenCourseByName(String courseName) {

    courseCatalogPage.open()
        .searchForCourse(courseName)
        .findAndClickCourseByName(courseName);

    String actualHeader = courseCatalogPage.getPageHeader();
    System.out.println("Открыта страница курса: " + actualHeader);

    assertThat(actualHeader)
        .as("Ошибка: открыта страница неверного курса. Ожидалось: '%s', но найдено: '%s'", courseName, actualHeader)
        .isEqualTo(courseName);

    System.out.println("Название курса соответствует ожидаемому: "+ courseName);
  }

  @Test
  public void testFindAndCheckEarliestAndLatestCourses() throws IOException {

    courseCatalogPage.open()
        .clickShowMoreButtonUntilAllLoaded()
        .findCoursesWithEarliestAndLatestDates()
        .verifyCoursesOnLinks();
  }
}
