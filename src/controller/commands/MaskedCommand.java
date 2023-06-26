package controller.commands;

import model.color.Color;
import model.image.ImageModel;
import util.Util;

/**
 * Represents a command that applies to only part of an image as specified by a mask.
 */
public class MaskedCommand implements ImageProcessingCommand {

  private final ImageModel mask;
  private final ImageProcessingCommand command;

  /**
   * Create a new masked command with the actual processing command to run, and a mask specifying
   * the parts of the image to run the command on.
   * @param command the command to run
   * @param mask the mask to use
   * @throws IllegalArgumentException if either argument is null
   */
  public MaskedCommand(ImageProcessingCommand command, ImageModel mask)
          throws IllegalArgumentException {
    this.mask = Util.requireNonNullArg(mask);
    this.command = Util.requireNonNullArg(command);
  }

  @Override
  public ImageModel process(ImageModel m) throws IllegalArgumentException {
    Util.requireNonNullArg(m);
    int maskHeight = this.mask.getHeight();
    int maskWidth = this.mask.getWidth();
    if (m.getWidth() != maskWidth || m.getHeight() != maskHeight) {
      throw new IllegalArgumentException("Mask dimensions do not match image dimensions.");
    }

    // Process image normally using command delegate.
    ImageModel processedImage = this.command.process(m);

    if (processedImage.getWidth() != maskWidth || processedImage.getHeight() != maskHeight) {
      throw new IllegalArgumentException("Mask dimensions do not match image dimensions.");
    }

    Color maskColor = new Color(0,0,0); // Use black as the mask color.
    Color[][] maskResultPixels = new Color[maskHeight][maskWidth];
    for (int r = 0; r < maskHeight; r++) {
      for (int c = 0; c < maskWidth; c++) {
        if (this.mask.colorAt(r,c).equals(maskColor)) {
          maskResultPixels[r][c] = processedImage.colorAt(r,c);
        } else {
          maskResultPixels[r][c] = m.colorAt(r,c);
        }
      }
    }

    return m.createNew(maskResultPixels);
  }
}
