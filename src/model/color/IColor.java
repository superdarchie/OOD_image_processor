package model.color;

/**
 * Represents an RGB pixel color, where a color is a combination of red, green, and blue channels.
 */
public interface IColor {

  /**
   * Represents the channels that exist within a color.
   * Red, green, or blue.
   */
  enum Channel { Red, Green, Blue }

  /**
   * Get the red channel for this color.
   * @return the red value
   */
  int red();

  /**
   * Get the green channel for this color.
   * @return the green value
   */
  int green();

  /**
   * Get the blue channel for this color.
   * @return the blue value
   */
  int blue();

  /**
   * Get the provided channel for this color.
   * @param channel the channel to get
   * @return the value for that channel
   * @throws IllegalArgumentException if the provided channel is null or unknown
   */
  int getChannel(Channel channel) throws IllegalArgumentException;


  /**
   * Add two colors together and yield the resultant color.
   * All channels clamped to the range 0-255.
   * @param that the color to add to this
   * @return the resulting color
   */
  Color add(Color that);

  /**
   * Add red, green, and blue values to this color.
   * @param r the red value
   * @param g the blue value
   * @param b the green value
   * @return the resultant color
   */
  Color add(int r, int g, int b);

}
