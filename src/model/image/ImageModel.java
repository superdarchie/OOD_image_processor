package model.image;

import model.color.Color;

/**
 * Represents the methods that can be done to observe the state of a 2d image.
 *
 * <p>Images have width and height, and some color at each pixel of the image.
 */
public interface ImageModel {

  /**
   * Retrieve the width of the image.
   * @return the width
   */
  int getWidth();

  /**
   * Retrieve the height of the image.
   * @return the height
   */
  int getHeight();

  /**
   * Get the color at the given location in the image.
   * @param r row location
   * @param c column location
   * @return the specified color
   * @throws IllegalArgumentException if the given location is beyond the image boundaries
   */
  Color colorAt(int r, int c) throws IllegalArgumentException;

  /**
   * Creates a new image model of the same type as the object that the method is called on, taking
   * in the array of pixel colors to make the image from.
   * @param pixels the colors of each pixel in the image
   * @return the new image
   * @throws IllegalArgumentException if the provided pixel array is null
   */
  ImageModel createNew(Color[][] pixels) throws IllegalArgumentException;
}
