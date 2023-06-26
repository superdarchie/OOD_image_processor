package controller.commands;

import model.color.Color;
import model.color.IColor;
import model.image.ImageModel;
import util.Util;

/**
 * Represents a command that downsizes a given image to an instantiated size.
 */
public class Downscale implements ImageProcessingCommand {
  private final int cols;
  private final int rows;

  /**
   * Initializes a downscale object with the supplied dimensions for resizing images.
   * @param width the downscaled image width
   * @param height the downscaled image height
   * @throws IllegalArgumentException if width or height is not positive
   */
  public Downscale(int width, int height) throws IllegalArgumentException {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException("Image dimensions must be positive");
    }
    this.cols = width;
    this.rows = height;
  }

  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);
    if (cols > m.getWidth() || rows > m.getHeight()) {
      throw new IllegalArgumentException("The supplied image must be larger than this downscaled "
              + "dimensions of: " + this.cols + " x " + this.rows);
    }
    if (this.rows == m.getHeight() && this.cols == m.getWidth()) { // saves processing time
      return m;
    }

    Color[][] res = new Color[this.rows][this.cols];

    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        float cCounterpart = m.getWidth() * ((float) col / this.cols);
        float rCounterpart = m.getHeight() * ((float) row / this.rows);

          res[row][col] = new Color(
                  this.downsizeChannel(rCounterpart, cCounterpart, m, IColor.Channel.Red),
                  this.downsizeChannel(rCounterpart, cCounterpart, m, IColor.Channel.Green),
                  this.downsizeChannel(rCounterpart, cCounterpart, m, IColor.Channel.Blue));
      }
    }

    return m.createNew(res);
  }

  // Private helper method to get the average value for a single channel based on the four
  // surrounding pixels in the original image.
  private int downsizeChannel(float r, float c, ImageModel img, IColor.Channel channel) {
    int floorC = (int) Math.floor(c);
    int floorR = (int) Math.floor(r);
    int ceilC = (int) Math.ceil(c);
    int ceilR = (int) Math.ceil(r);

    IColor colorA = img.colorAt(floorR, floorC);
    IColor colorB = img.colorAt(floorR, ceilC);
    IColor colorC = img.colorAt(ceilR, floorC);
    IColor colorD = img.colorAt(ceilR, ceilC);

    float m = colorB.getChannel(channel) * (c - floorC)
            + colorA.getChannel(channel) * (floorC + 1 - c);
    float n = colorD.getChannel(channel) * (c - floorC)
            + colorC.getChannel(channel) * (floorC + 1 - c);
    return (int) (m * (r - floorR) + n * (floorR + 1 - r));
  }

}
