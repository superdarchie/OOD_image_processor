package controller.extensions;

import java.io.OutputStream;
import java.util.Optional;

import controller.ScriptedImageProcessorController;
import model.processor.ImageProcessor;

/**
 * Extension of the scripted controller that allows for downscaling and masking.
 */
public class ExtendedScriptedController extends ScriptedImageProcessorController {

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * If an exception is encountered, control is stopped.
   *
   * @param processor    a processor to use to process images
   * @param input        a script from which to read the commands
   * @param overrideDest optional override on where to write images when called to save them
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public ExtendedScriptedController(ImageProcessor processor, Readable input,
                                    Optional<OutputStream> overrideDest)
          throws IllegalArgumentException {
    super(processor, input, overrideDest, new ExtendedControllerFeatures(processor));
  }
}
