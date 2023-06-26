package controller.commands;

import java.util.function.Function;
import model.image.ImageModel;
import model.color.Color;
import util.Util;

/**
 * A generalized command to transform all the colors in a given image into new colors.
 */
public class MapChannels implements ImageProcessingCommand {
  private final Function<Color,Color> mapping;

  /**
   * Initializes this with the Color to Color command to be mapped.
   * @param mapping a function object to convert a Color into a new Color
   */
  public MapChannels(Function<Color, Color> mapping) {
    Util.requireNonNullArg(mapping);
    this.mapping = mapping;
  }

  /**
   * Map all the colors in the provided image model using the mapping for this command.
   *
   * @param m the image to modify
   * @return the processed image
   * @throws IllegalArgumentException if the provided model is null
   */
  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);

    int height = m.getHeight();
    int width = m.getWidth();

    Color[][] res = new Color[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        res[r][c] = this.mapping.apply(m.colorAt(r, c));
      }
    }

    return m.createNew(res);
  }

}
