package model.color;

import java.util.Objects;

import util.Util;

/**
 * Represents an 8-bit pixel color (as values from 0-255) per channel, creating a 24-bit image.
 * Two colors are equivalent if they have the same rgb values.
 */
public class Color implements IColor {

  // The max value used for an 8-bit representation of a color value.
  public static final int MAX_VALUE = 255;

  private final int g;
  private final int r;
  private final int b;

  /**
   * Clamp a color value to the range 0 to 255.
   * @param x the value to clamp
   * @return the clamped result
   */
  private int colorClamp(int x) {
    return Math.max(0, Math.min(x, MAX_VALUE));
  }

  /**
   * Create a new color with the given red, green, and blue channel values.
   * @param r the red value
   * @param g the green value
   * @param b the blue value
   */
  public Color(int r, int g, int b) {
    this.r = colorClamp(r);
    this.g = colorClamp(g);
    this.b = colorClamp(b);
  }

  @Override
  public int red() {
    return this.r;
  }

  @Override
  public int green() {
    return this.g;
  }

  @Override
  public int blue() {
    return this.b;
  }

  @Override
  public int getChannel(Channel channel) throws IllegalArgumentException {
    Util.requireNonNullArg(channel);
    switch (channel) {
      case Red:
        return this.r;
      case Green:
        return this.g;
      case Blue:
        return this.b;
      default:
        throw new IllegalArgumentException("Unknown channel requested.");
    }
  }

  @Override
  public Color add(Color that) {
    return this.add(that.red(), that.green(), that.blue());
  }

  @Override
  public Color add(int r, int g, int b) {
    return new Color(
        colorClamp(this.r + r),
        colorClamp(this.g + g),
        colorClamp(this.b + b)
    );
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Color)) {
      return false;
    } else {
      IColor thatColor = (Color) that;
      return thatColor.red() == this.r && thatColor.green() == this.g && thatColor.blue() == this.b;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.r, this.g, this.b);
  }

}
