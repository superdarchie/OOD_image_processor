package controller.commands;

import java.util.function.Function;

import model.color.Color;
import util.Util;

/**
 * A generalized command to transform all the colors in a given image into new colors
 * based on an int mapping component.
 */
public class SetAllChannels extends MapChannels {
  /**
   * Produces new Colors made up of the produced Integer for all colors in an image.
   * @param mapping a function object to convert a Color into a new Integer
   */
  public SetAllChannels(Function<Color,Integer> mapping) {
    super((Color c) -> {
      int value = Util.requireNonNullArg(mapping).apply(c);
      return new Color(value, value, value);
    });
  }
}
