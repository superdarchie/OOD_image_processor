package mocks;

import controller.ControllerFeatureSet;
import model.image.ImageModel;
import util.Util;
import view.SwingGuiView;

/**
 * A mock view that allows the same functionality as the swing gui view and additionally
 * logs the methods called on it.
 */
public class LoggingSwingGuiView extends SwingGuiView {
  private final StringBuilder log;

  /**
   * Create a new logging GUI view mock with the given log to write to.
   * @param log the log to write to
   */
  public LoggingSwingGuiView(StringBuilder log) {
    super();
    this.log = Util.requireNonNullArg(log);
  }

  @Override
  public void displayImage(ImageModel img) {
    this.log.append("displayImage\n");
    super.displayImage(img);
  }

  @Override
  public void setActionObject(ControllerFeatureSet actionObject) {
    this.log.append("setActionObject\n");
    super.setActionObject(actionObject);
  }

}
