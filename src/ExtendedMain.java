import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Optional;

import controller.ImageProcessorController;
import controller.extensions.ExtendedGuiController;
import controller.extensions.ExtendedInteractiveController;
import controller.extensions.ExtendedScriptedController;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;
import view.ImageProcessorView;
import view.SwingGuiView;

/**
 * The entry point to run our interactive or scripted image program, utilizing the controller and
 * corresponding model (and processor) with supplemental classes to create simple images and
 * manage PPM files. Uses controllers extended for the extra credit assignment that support masking,
 * and downscaling.
 */
public class ExtendedMain {

  /**
   * Either displays the system functionality, runs a scripted image controller
   * (with no error handling) from a file, or runs an interactive image controller
   * (with error handling) utilizing the System.
   * @param args the help menu, a scripted file, or an interactive generation
   */
  public static void main(String[] args) {

    ImageProcessor processor = new SimpleImageProcessor();
    Readable inputSource;
    ImageProcessorController controller;

    // If no args are provided, just run in GUI mode
    if (args.length == 0) {
      ImageProcessorView view = new SwingGuiView();
      controller = new ExtendedGuiController(processor, view);
    } else {
      // Switch on the command line arg to dictate the args based on what mode to run in
      // or let the user know how the program should be run.
      switch (args[0]) {
        case "-h":
          System.out.println(usage);
          return;
        case "-file":
          if (args.length < 2) {
            System.out.println("Must provide a file name.\n" + usage);
            return;
          }
          try {
            inputSource = new FileReader(args[1]);
            controller = new ExtendedScriptedController(processor, inputSource,
                                                   Optional.empty());
            break;
          } catch (FileNotFoundException e) {
            System.out.println("Could not find the file to run: " + e.getMessage());
            return;
          }
        case "-text":
          inputSource = new InputStreamReader(System.in);
          controller = new ExtendedInteractiveController(
                  processor,
                  inputSource,
                  Optional.empty(),
                  System.out
          );
          break;
        default:
          System.out.println("Invalid usage.\n" + usage);
          return;
      }
    }

    try {
      // Start the controller
      controller.run();
    } catch (IllegalStateException e) {
      // Gently end the program upon controller error, informing the user of the error message.
      System.out.println(e.getMessage());
    }

  }

  private static final String usage = ""
          + "Usage: [option] ...\n"
          + "-h         : show this help message\n"
          + "-file path : run the provided script file\n"
          + "-text      : run in interactive mode\n"
          + "           : no args to run in GUI mode";

}
