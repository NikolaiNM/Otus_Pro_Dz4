package exceptions;

public class UrlDataExceptions extends RuntimeException {

  public UrlDataExceptions() {
    super("Url data annotation doesn't exist on page class");
  }

}
