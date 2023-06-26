package controller;

import model.processor.ImageProcessor;
import util.Util;
import view.ImageProcessorView;

/**
 * GUI controller which processes input and updates images accordingly through user interaction.
 */
public class GuiImageProcessorController implements ImageProcessorController {

  protected final ImageProcessor processor;
  private final ImageProcessorView view;
  protected final ControllerFeatureSet controls;

  /**
   * Create a new image processor controller that runs based on user input through a GUI
   * in order to process images.
   *
   * @param processor    a processor to use to process images
   * @param view         a view to show the program and allow for user input
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public GuiImageProcessorController(ImageProcessor processor, ImageProcessorView view)
          throws IllegalArgumentException {
    this(processor, view, new GuiControllerFeatures(processor, view));
  }

  /**
   * Create a new image processor controller that runs based on user input through a GUI
   * in order to process images.
   *
   * @param processor    a processor to manipulate the images with
   * @param view         a view to show the program and allow for user input
   * @param controls     a delegate for all the controller features
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public GuiImageProcessorController(ImageProcessor processor, ImageProcessorView view, ControllerFeatureSet controls)
          throws IllegalArgumentException {
    this.processor = Util.requireNonNullArg(processor);
    this.view = Util.requireNonNullArg(view);
    this.controls = Util.requireNonNullArg(controls);
  }

  @Override
  public void run() throws IllegalStateException {
    view.setActionObject(this.controls);
  }
}
