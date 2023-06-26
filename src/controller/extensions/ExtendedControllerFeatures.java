package controller.extensions;

import java.util.Scanner;

import controller.ControllerFeatures;
import controller.commands.Downscale;
import controller.commands.ImageProcessingCommand;
import controller.commands.MaskedCommand;
import model.image.ImageModel;
import model.processor.ImageProcessor;

/**
 * An extended features object that adds the downscale command and capability for command masking.
 */
public class ExtendedControllerFeatures extends ControllerFeatures {

  /**
   * Create a new instance of controller features with a processor delegate to load images to,
   * save images from, and run commands via.
   *
   * @param processor the processor delegate
   * @throws IllegalArgumentException if the processor is null
   */
  public ExtendedControllerFeatures(ImageProcessor processor) throws IllegalArgumentException {
    super(processor);
    this.knownCommands.put("downscale", (Scanner sc) -> new Downscale(sc.nextInt(), sc.nextInt()));
  }

  @Override
  protected ImageProcessingCommand createProcessingCommand(String cmd, Scanner sc)
          throws IllegalStateException {
    ImageProcessingCommand command = super.createProcessingCommand(cmd, sc);
    ImageModel mask;
    if (sc != null && sc.hasNext()) { // Check for a mask.
      try {
        // Get mask, override command with masked version of it.
        mask = this.processor.getImageState(sc.next());
        command = new MaskedCommand(command, mask);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("Specified mask image name does not exist.");
      }
    }
    return command;
  }
}
