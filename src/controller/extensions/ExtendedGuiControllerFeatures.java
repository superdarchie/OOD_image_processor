package controller.extensions;

import java.util.Scanner;

import model.processor.ImageProcessor;
import util.Util;
import view.ImageProcessorView;

/**
 * An extension of the extended controller features class that also manages a view so that the
 * view can be updated every time an image is changed.
 */
public class ExtendedGuiControllerFeatures extends ExtendedControllerFeatures {

  private final ImageProcessorView view;

  /**
   * Create a new instance of controller features with a processor delegate to load images to,
   * save images from, and run commands via.
   *
   * @param processor the processor delegate
   * @param view the view to use
   * @throws IllegalArgumentException if the processor is null
   */
  public ExtendedGuiControllerFeatures(ImageProcessor processor, ImageProcessorView view)
          throws IllegalArgumentException {
    super(processor);
    this.view = Util.requireNonNullArg(view);
  }

  @Override
  public void loadFile(String path, String saveName) {
    super.loadFile(path, saveName);
    this.view.displayImage(this.processor.getImageState(saveName));
  }

  @Override
  public void runProcessingCommand(String cmd, String name, String saveName, Scanner sc) {
    super.runProcessingCommand(cmd, name, saveName, sc);
    this.view.displayImage(this.processor.getImageState(saveName));
  }
}
