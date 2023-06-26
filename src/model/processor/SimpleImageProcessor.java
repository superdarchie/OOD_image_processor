package model.processor;

import java.util.HashMap;
import java.util.Map;

import controller.commands.ImageProcessingCommand;
import model.image.ImageModel;
import util.Util;

/**
 * Represents the implementation for a simple image processor, which works between the controller
 * and model to load simple images and process commands, storing all the edited models.
 * This enables an image to not know its name or interaction with the greater program, and instead
 * have its raw functionality support pixels only.
 */
public class SimpleImageProcessor implements ImageProcessor {

  private final Map<String, ImageModel> images;

  /**
   * Initializes a new simple image processor, having no images to start.
   */
  public SimpleImageProcessor() {
    this.images = new HashMap<>();
  }

  @Override
  public ImageModel getImageState(String name) throws IllegalArgumentException {
    return Util.requireNonNullArg(this.images.get(name));
  }

  @Override
  public void loadImage(String name, ImageModel img) throws IllegalArgumentException {
    images.put(name, Util.requireNonNullArg(img));
  }

  @Override
  public void processImage(String name, String saveName, ImageProcessingCommand cmd)
          throws IllegalArgumentException {
    Util.requireNonNullArg(cmd);
    ImageModel imgToProcess = Util.requireNonNullArg(this.images.get(name));
    ImageModel processedImg = cmd.process(imgToProcess);
    this.images.put(saveName, processedImg);
  }

}
