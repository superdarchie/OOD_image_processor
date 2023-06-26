package controller.commands;

import model.color.Color;
import model.image.ImageModel;
import util.Util;

/**
 * General function object for color transformation where the new pixel Color is a linear
 * combinations of its initial rgb values. Distinct from filtering which is applied separately on
 * every channel based on its neighbors.
 */
public class ColorTransformation implements ImageProcessingCommand {
  private final float[][] transformMatrix;

  /**
   * Initializes the linear transformation 3x3 matrix.
   * @param linearTransformation a 2D array of numbers
   * @throws IllegalArgumentException if the supplied matrix is null or not 3x3 dimensions
   */
  public ColorTransformation(float[][] linearTransformation) throws IllegalArgumentException {
    Util.requireNonNullArg(linearTransformation);
    if (linearTransformation.length != 3 || linearTransformation[0].length != 3) {
      throw new IllegalArgumentException("Color transformation must take in a 3x3 matrix.");
    }
    this.transformMatrix = linearTransformation;
  }

  /**
   * Modify the given image by linearly transforming every pixel.
   * @param m the image to modify
   * @return the color transformation image
   */
  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);

    int width = m.getWidth();
    int height = m.getHeight();

    Color[][] res = new Color[height][width];

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        res[r][c] = applyTransformation(m.colorAt(r, c));
      }
    }
    return m.createNew(res);
  }

  /**
   * Transforms a pixel Color by multiplying each channel according to the 3x3 linear matrix
   * (i.e. r' = a00r + a01g + a02b, g' = a10r + a11g + a12b, b' = a20r + a21g + a22b,
   * where rgb represents in the initial values, r'g'b' represents the new  values, and
   * a__ represents row/column positions in the linear transformation matrix)
   * @param color the color to be transformed
   * @return a new Color comprised of linear transformations of its own rgb values
   */
  private Color applyTransformation(Color color) {
    float[] newChannelVals = new float[3];

    for (int i = 0; i < 3; i++) {
      newChannelVals[i] = this.transformMatrix[i][0] * color.red()
          + this.transformMatrix[i][1] * color.green()
          + this.transformMatrix[i][2] * color.blue();
    }

    return new Color(
      Math.round(newChannelVals[0]),
      Math.round(newChannelVals[1]),
      Math.round(newChannelVals[2])
    );
  }
}
