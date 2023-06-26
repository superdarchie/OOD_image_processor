package controller.commands;

import model.color.Color;
import model.color.IColor.Channel;
import model.image.ImageModel;
import util.Util;

/**
 * Function object command to represent filtering, a common image processing algorithm which acts
 * on a pixel in an image and a channel. An entire image can be filtered by overlaying the kernel
 * atop relevant pixels for every channel.
 */
public class Filter implements ImageProcessingCommand {

  private final float[][] kernel;
  private final int kWidth;
  private final int kHeight;

  /**
   * Initializes the kernel to be used for filtering.
   * @param kernel a 2D array of numbers
   * @throws IllegalArgumentException if the supplied kernel is null or the kernel dimensions are
   *                                  even
   */
  public Filter(float[][] kernel) throws IllegalArgumentException {
    Util.requireNonNullArg(kernel);
    if (kernel.length % 2 != 1 || kernel[0].length % 2 != 1) {
      throw new IllegalArgumentException("Kernel for a filter operation must have odd dimensions.");
    }
    this.kernel = kernel;
    this.kHeight = kernel.length;
    this.kWidth = kernel[0].length;
  }

  /**
   * Modify the given image by filtering it with the kernel.
   * (i.e. for every pixel in the supplied image, apply the kernel on each channel).
   * @param m the image to modify
   * @return the filtered image
   */
  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);

    int width = m.getWidth();
    int height = m.getHeight();

    Color[][] res = new Color[height][width];

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        res[r][c] = new Color(
          applyKernel(r, c, m, Channel.Red),
          applyKernel(r, c, m, Channel.Green),
          applyKernel(r, c, m, Channel.Blue)
        );
      }
    }

    return m.createNew(res);
  }

  /**
   * Filters a single channel by overlaying the center of the kernel with the particular pixel and
   * only includes those pixels that are overlapped.
   * @param row the pixel row in the image
   * @param col the pixel column in the image
   * @param m the image to filter
   * @param channel in an RGB pixel, either red, green, or blue
   * @return the clamped sum of the product of overlapped kernel and pixel positions
   * @throws IllegalArgumentException if the supplied channel or image is null
   */
  private int applyKernel(int row, int col, ImageModel m, Channel channel)
          throws IllegalArgumentException {
    Util.requireNonNullArg(channel);
    Util.requireNonNullArg(m);

    float value = 0;

    // Figure out bounds for pixels of the image to use.
    int rowStart = row - (kHeight / 2);
    int rowEnd = row + (kHeight / 2) + 1;
    int colStart = col - (kWidth / 2);
    int colEnd = col + (kWidth / 2) + 1;
    // Loop over the bounds, making sure the edges of the image are respected.
    for (int r = Math.max(0, rowStart); r < Math.min(m.getHeight(), rowEnd); r++) {
      for (int c = Math.max(0, colStart); c < Math.min(m.getWidth(), colEnd); c++) {
        Color color = m.colorAt(r, c);
        // Add kernel val times color channel val to the total.
        value += kernel[r - rowStart][c - colStart] * color.getChannel(channel);
      }
    }
    return Math.round(value);
  }
}
