package controller;

import java.io.OutputStream;
import java.util.Optional;
import java.util.Scanner;
import model.processor.ImageProcessor;

/**
 * Represents the controller implementation for a scripted image processor reading a file.
 * It does not handle user input, instead throwing an exception if given an invalid command.
 */
public class ScriptedImageProcessorController extends SimpleImageProcessorController {

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * If an exception is encountered, control is stopped.
   *
   * @param processor a processor to use to process images
   * @param input     a script from which to read the commands
   * @param overrideDest optional override on where to write images when called to save them
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public ScriptedImageProcessorController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> overrideDest
  ) throws IllegalArgumentException {
    super(processor, input, overrideDest);
  }

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * If an exception is encountered, control is stopped.
   *
   * @param processor a processor to use to process images
   * @param input     a script from which to read the commands
   * @param overrideDest optional override on where to write images when called to save them
   * @param controls the controller feature set to use
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public ScriptedImageProcessorController(
          ImageProcessor processor,
          Readable input,
          Optional<OutputStream> overrideDest,
          ControllerFeatureSet controls
  ) throws IllegalArgumentException {
    super(processor, input, overrideDest, controls);
  }

  @Override
  public void run() throws IllegalStateException {

    Scanner sc = new Scanner(this.input);

    // Run through lines of provided script.
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      // Only care about non-empty, non-comment lines.
      if (!line.startsWith("#") && line.length() > 0) {
        this.runLine(line);
      }
    }
  }

}
