package controller;

import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import model.image.ImageModel;
import model.processor.ImageProcessor;
import util.ImageUtil;
import util.Util;

/**
 * Generalized controller to support several ways to enter commands.
 * Supports a basic functionality to interact with images that can be extended to support new
 * design in the future.
 */
public abstract class SimpleImageProcessorController implements ImageProcessorController {

  protected final ImageProcessor processor;
  protected final Readable input;
  protected final Optional<OutputStream> overrideDest;
  protected final ControllerFeatureSet controls;

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * @param processor a processor to use to process images
   * @param input a readable input source from which to read the commands/script
   * @param overrideDest optional destination to send all output to instead of to files by name
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public SimpleImageProcessorController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> overrideDest
  ) throws IllegalArgumentException {
    this(processor, input, overrideDest, new ControllerFeatures(processor));
  }

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * @param processor a processor to use to process images
   * @param input a readable input source from which to read the commands/script
   * @param overrideDest optional destination to send all output to instead of to files by name
   * @param controls the controller feature set object to use
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public SimpleImageProcessorController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> overrideDest,
      ControllerFeatureSet controls
  ) throws IllegalArgumentException {
    this.processor = Util.requireNonNullArg(processor);
    this.input = Util.requireNonNullArg(input);
    this.overrideDest = Util.requireNonNullArg(overrideDest);
    this.controls = Util.requireNonNullArg(controls);
  }

  /**
   * Parse a line to figure out what sort of command it is (image processing vs. input/output)
   * and then run the command.
   * @param line the line to parse as a command and run
   * @throws IllegalStateException if something goes wrong in building or running the command
   * @throws IllegalArgumentException if some arguments are null or otherwise unusable
   */
  protected void runLine(String line) throws IllegalStateException, IllegalArgumentException {
    Scanner tokenScanner = new Scanner(line);

    String cmd;
    String name;
    String saveName;
    try {
      cmd = tokenScanner.next();
      name = tokenScanner.next();
      saveName = tokenScanner.next();
    } catch (NoSuchElementException e) {
      throw new IllegalStateException(String.format("Command '%s' has insufficient arguments.",
              line));
    }

    // Handle load differently from processing commands - the controller is responsible for IO.
    if (cmd.equals("load")) {
      this.controls.loadFile(name, saveName);

    // Handle save differently from processing commands - the controller is responsible for IO.
    } else if (cmd.equals("save")) {

      if (this.overrideDest.isPresent()) {
        ImageModel img = this.processor.getImageState(name);
        ImageUtil.writeImage(img, saveName, this.overrideDest.get());
      } else {
        this.controls.saveImage(name, saveName);
      }

    // If not load or save, try to make a processing command out of the input and run it.
    } else {
      try {
        this.controls.runProcessingCommand(cmd, name, saveName, tokenScanner);
      } catch (NoSuchElementException e) {
        throw new IllegalStateException(String.format("Command '%s' has insufficient arguments.",
                line));
      }
    }
  }

}
