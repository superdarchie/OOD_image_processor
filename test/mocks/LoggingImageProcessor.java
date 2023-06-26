package mocks;

import controller.commands.ImageProcessingCommand;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.color.Color;
import util.Util;

/**
 * An implementation of an image processor that does not process images
 * but rather logs whenever a method is called on it.
 */
public class LoggingImageProcessor implements ImageProcessor {

  private final StringBuilder log;

  /**
   * Create a new logging processor mock with the given log to write to.
   * @param log the log to write to
   */
  public LoggingImageProcessor(StringBuilder log) {
    this.log = Util.requireNonNullArg(log);
  }

  @Override
  public ImageModel getImageState(String name) throws IllegalArgumentException {
    this.log.append(String.format("getImageState with name %s\n", name));
    return new SimpleImage(new Color[][]{{new Color(0,0,0)}});
  }

  @Override
  public void loadImage(String name, ImageModel img) {
    this.log.append(String.format("loadImage with name %s\n", name));
  }

  @Override
  public void processImage(String name, String saveName, ImageProcessingCommand cmd)
          throws IllegalArgumentException {
    this.log.append(String.format("processImage with name %s, saveName %s\n", name, saveName));
  }
}
