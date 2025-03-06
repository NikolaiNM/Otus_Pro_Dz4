package components.popups;

public interface IPopup<T> {

  T popupShouldNotBeVisible();

  T popupShouldBeVisible();

}
