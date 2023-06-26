package controller.commands;

import model.color.Color;
import model.image.ImageModel;
import util.Util;

/**
 * Function object command to flip the given image vertically.
 */
public class FlipVertically implements ImageProcessingCommand {

  /**
   * Modify the given image model by flipping it vertically
   * (i.e. the top row swaps with the bottom and so on).
   * @param m the base image model
   * @return the vertically flipped image
   */
  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);

    int height = m.getHeight();
    int width = m.getWidth();

    Color[][] res = new Color[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        res[r][c] = m.colorAt(height - r - 1, c);
      }
    }

    return m.createNew(res);
  }

}
