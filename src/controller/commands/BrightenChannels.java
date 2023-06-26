package controller.commands;

import model.color.Color;

/**
 * Function object command to brighten or darken the given image by a supplied amount.
 */
public class BrightenChannels extends MapChannels {
  /**
   * Modify the image by brightening it
   * (add the supplied increment value to all clamped channels of the color pixel).
   * @param increment the amount to brighten (positive) / darken (negative)
   */
  public BrightenChannels(int increment) {
    super((Color c) -> c.add(increment, increment, increment));
  }
}
