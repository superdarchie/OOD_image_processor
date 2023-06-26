package controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Scanner;

import model.processor.ImageProcessor;
import util.Util;

/**
 * Represents the controller implementation for an interactive image processor. It handles
 * user input by prompting the user with messages if given invalid commands and gathering new input.
 */
public class InteractiveImageProcessorController extends SimpleImageProcessorController {

  private final Appendable responder;

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * If a recoverable exception is encountered, the user is asked to just re-enter the last command.
   *
   * @param processor a processor to use to process images
   * @param input     a readable input source from which to read the commands/script
   * @param overrideDest optional override on where to write images when called to save them
   * @param responder an output location to which messages may be rendered to respond to the user
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public InteractiveImageProcessorController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> overrideDest,
      Appendable responder
  ) throws IllegalArgumentException {
    super(processor, input, overrideDest);
    this.responder = Util.requireNonNullArg(responder);
  }

  /**
   * Create a new image processor controller that runs input commands in order to process images.
   * If a recoverable exception is encountered, the user is asked to just re-enter the last command.
   *
   * @param processor a processor to use to process images
   * @param input     a readable input source from which to read the commands/script
   * @param overrideDest optional override on where to write images when called to save them
   * @param responder an output location to which messages may be rendered to respond to the user
   * @param controls the controller features to use
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public InteractiveImageProcessorController(
          ImageProcessor processor,
          Readable input,
          Optional<OutputStream> overrideDest,
          Appendable responder,
          ControllerFeatureSet controls
  ) throws IllegalArgumentException {
    super(processor, input, overrideDest, controls);
    this.responder = Util.requireNonNullArg(responder);
  }

  @Override
  public void run() throws IllegalStateException {

    Scanner sc = new Scanner(this.input);

    // Continue running while there is user input.
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      // Allow the user to quit by typing quit or q.
      if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) {
        this.respondWithMessage("Done.");
        return;
      }
      // Ignore empty lines and comment lines, otherwise run the line.
      if (!line.startsWith("#") && line.length() > 0) {
        // Try-catch to allow for graceful recovery and re-attempted command entry.
        try {
          this.runLine(line);
        } catch (IllegalArgumentException e) {
          this.respondWithMessage("Illegal argument. Try re-entering the command?");
        } catch (IllegalStateException e) {
          this.respondWithMessage(String.format(
              "Encountered exception: %s\nTry re-entering the command?%n",
              e.getMessage()
          ));
        }
      }
    }
  }

  /**
   * Send a message to the responder. Used to prompt the user.
   * @param message message to send
   * @throws IllegalStateException if for some reason the responder cannot be appended
   */
  private void respondWithMessage(String message) throws IllegalStateException {
    try {
      this.responder.append(message);
    } catch (IOException e) {
      throw new IllegalStateException("Responding to the user failed.");
    }
  }

}
