package controller;

/**
 * Represents a controller for an image processing program, which handles user-input to produce new,
 * edited photos based off a series of known commands.
 */
public interface ImageProcessorController {

  /**
   * Run the image processor controller.
   * @throws IllegalStateException if illegal state is encountered
   */
  void run() throws IllegalStateException;

}
