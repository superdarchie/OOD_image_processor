package model.image;

import java.util.Objects;

import model.color.Color;
import util.Util;

/**
 * Represents the implementation for a simple image, which is a 2d display of pixels.
 *
 * <p>CLASS INVARIANTS:
 * - The colors in the 2d pixels array for SimpleImage never change
 *   - pixels instantiated in constructor
 *   - no methods modify the pixels array
 * - Width and height correspond to the number of columns and number of rows in the pixel array
 *   - constructor initializes height to be the number of rows and width to be the number of columns
 *   - width and height are final
 */
public class SimpleImage implements ImageModel {

  private final int width;
  private final int height;
  private final Color[][] pixels;

  /**
   * Create a new simple image
   * with the provided 2d color array representing the pixels of the image.
   * @param pixels the color array that represents the image
   * @throws IllegalArgumentException if the color array is null
   */
  public SimpleImage(Color[][] pixels) throws IllegalArgumentException {
    Util.requireNonNullArg(pixels);
    if (pixels.length == 0 || pixels[0].length == 0) {
      throw new IllegalArgumentException("Pixel array must contain at least one pixel.");
    }
    this.height = pixels.length;
    this.width = pixels[0].length;
    this.pixels = new Color[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        this.pixels[r][c] = pixels[r][c];
      }
    }
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public Color colorAt(int r, int c) throws IllegalArgumentException {
    if (r < 0 || c < 0 || r > this.height - 1 || c > this.width - 1) {
      throw new IllegalArgumentException("Cannot get color outside of image bounds.");
    }
    return pixels[r][c];
  }

  @Override
  public ImageModel createNew(Color[][] pixels) throws IllegalArgumentException {
    return new SimpleImage(pixels);
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof SimpleImage)) {
      return false;
    }
    ImageModel thatImg = (SimpleImage) that;
    if (this.getHeight() != thatImg.getHeight() || this.getWidth() != thatImg.getWidth()) {
      return false;
    } else {
      for (int row = 0; row < this.getHeight(); row++) {
        for (int col = 0; col < this.getWidth(); col++) {
          if (!this.colorAt(row, col).equals(thatImg.colorAt(row, col))) {
            return false;
          }
        }
      }
      return true;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getHeight(), this.getWidth());
  }
}
