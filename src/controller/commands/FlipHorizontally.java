package controller.commands;

import model.color.Color;
import model.image.ImageModel;
import util.Util;

/**
 * Function object command to flip the given image horizontally.
 */
public class FlipHorizontally implements ImageProcessingCommand {

  /**
   * Modify the given image model by flipping it horizontally
   * (i.e. the left column swaps with the right and so on).
   * @param m the base image model
   * @return the horizontally flipped image
   */
  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);

    int height = m.getHeight();
    int width = m.getWidth();

    Color[][] res = new Color[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        res[r][c] = m.colorAt(r, width - c - 1);
      }
    }

    return m.createNew(res);
  }
}
