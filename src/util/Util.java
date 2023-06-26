package util;

/**
 * Represents general functionality to be used and shared throughout the entire program. Can be
 * called using Util.methodName(...) through static typing.
 */
public class Util {

  /**
   * Corrects Objects.requireNonNull(...) to return an IllegalArgumentException instead of
   * a NullPointerException.
   * @param obj any generic object
   * @param <T> generalized Type T
   * @return a non-null object
   * @throws IllegalArgumentException if the supplied object is null
   */
  public static <T> T requireNonNullArg(T obj) throws IllegalArgumentException {
    if (obj == null) {
      throw new IllegalArgumentException("Supplied object is null.");
    } else {
      return obj;
    }
  }
}
