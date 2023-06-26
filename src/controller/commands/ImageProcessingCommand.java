package controller.commands;

import model.image.ImageModel;

/**
 * Represents the user command options for altering an image.
 * Has the option to be extended for supporting new user functionality.
 */
public interface ImageProcessingCommand {
  /**
   * Operate on the image model.
   * @param m the image to modify
   * @return the altered image
   * @throws IllegalArgumentException if the provided image is null
   */
  ImageModel process(ImageModel m) throws IllegalArgumentException;

}
