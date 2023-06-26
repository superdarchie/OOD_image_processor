package controller.extensions;

import java.io.OutputStream;
import java.util.Optional;

import controller.InteractiveImageProcessorController;
import model.processor.ImageProcessor;

/**
 * Extension of the interactive controller that adds support for downscaling and command masking.
 */
public class ExtendedInteractiveController extends InteractiveImageProcessorController {

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * If a recoverable exception is encountered, the user is asked to just re-enter the last command.
   *
   * @param processor    a processor to use to process images
   * @param input        a readable input source from which to read the commands/script
   * @param overrideDest optional override on where to write images when called to save them
   * @param responder    an output location to which messages may be rendered to respond to the user
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public ExtendedInteractiveController(ImageProcessor processor, Readable input,
                                       Optional<OutputStream> overrideDest, Appendable responder)
          throws IllegalArgumentException {
    super(processor, input, overrideDest, responder, new ExtendedControllerFeatures(processor));
  }
}
