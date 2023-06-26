package mocks;

import java.util.Scanner;

import javax.swing.*;

import controller.ControllerFeatureSet;
import model.image.ImageModel;
import util.Util;
import view.ImageProcessorView;

/**
 * A mock GUI view for testing with only a public downscale button and corresponding text fields
 * for the downscaled width and height dimensions.
 */
public class MockDownscaleView implements ImageProcessorView {
  public final JButton downscaleButton;
  public final JTextField downscaleWidth;
  public final JTextField downscaleHeight;
  private final String imageName;

  /**
   * Instantiate a new mock view that has the necessary implementation to downscale the given image.
   * @param imageName the image to downscale
   */
  public MockDownscaleView(String imageName) {
    this.downscaleButton = new JButton();
    this.downscaleWidth = new JTextField();
    this.downscaleHeight = new JTextField();
    this.imageName = Util.requireNonNullArg(imageName);
  }

  @Override
  public void displayImage(ImageModel img) throws IllegalArgumentException {
    // do nothing
  }

  @Override
  public void setActionObject(ControllerFeatureSet actionObject) throws IllegalArgumentException {
    Util.requireNonNullArg(actionObject);
    downscaleButton.addActionListener(e -> actionObject.runProcessingCommand("downscale",
            this.imageName,
            this.imageName,
            new Scanner(downscaleWidth.getText() + " " + downscaleHeight.getText())));
  }
}
