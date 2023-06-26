package view;

import controller.ControllerFeatureSet;
import model.image.ImageModel;

/**
 * This interface represents a view for an image processing program.
 */
public interface ImageProcessorView {

  /**
   * Displays the provided image on the view.
   * Also updates the views histograms representing the currently displayed image.
   * @param img the image to display
   * @throws IllegalArgumentException if the provided image is null
   */
  void displayImage(ImageModel img) throws IllegalArgumentException;

  /**
   * Sets the object to use to request actions from the controller.
   * @param actionObject the controller features object for command system functionality and
   *                     load/save
   * @throws IllegalArgumentException if the provided feature set is null
   */
  void setActionObject(ControllerFeatureSet actionObject) throws IllegalArgumentException;

}
