package model.processor;

import controller.commands.ImageProcessingCommand;
import model.image.ImageModel;

/**
 * Represents an image processor capable of managing multiple images in memory at once.
 * Stores a mapping between names and images that allows images in memory to be referenced
 * and manipulated.
 */
public interface ImageProcessor {

  /**
   * Retrieve an observable version of the image with the given name.
   * @param name the internal name of the image to get
   * @return the image state
   * @throws IllegalArgumentException if the name does not correspond to an image
   */
  ImageModel getImageState(String name) throws IllegalArgumentException;

  /**
   * Add an image to the memory of this processor and associate it with the given name.
   * @param name the given name
   * @param img the image to store
   * @throws IllegalArgumentException if the image to store is null
   */
  void loadImage(String name, ImageModel img) throws IllegalArgumentException;

  /**
   * Process a stored image with an image processing command
   * and store the result back into the processor's memory with a new name.
   * @param name the name of the image to process
   * @param saveName the name to associate with the processed image
   * @param cmd the command to run to process the image
   * @throws IllegalArgumentException if the name does not correspond to an image
   *                                  or the command is null
   */
  void processImage(String name, String saveName, ImageProcessingCommand cmd)
          throws IllegalArgumentException;

}
