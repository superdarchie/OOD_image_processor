package controller.extensions;

import controller.GuiImageProcessorController;
import model.processor.ImageProcessor;
import view.ImageProcessorView;

/**
 * Extended gui controller that uses the extended gui controllers features to grant access
 * to downscaling.
 */
public class ExtendedGuiController extends GuiImageProcessorController {

  /**
   * Create a new image processor controller that runs based on user input through a GUI
   * in order to process images.
   *
   * @param processor a processor to use to process images
   * @param view      a view to show the program and allow for user input
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public ExtendedGuiController(ImageProcessor processor, ImageProcessorView view)
          throws IllegalArgumentException {
    super(processor, view, new ExtendedGuiControllerFeatures(processor, view));
  }
}
