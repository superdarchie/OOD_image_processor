import org.junit.Before;
import org.junit.Test;

import java.io.File;

import controller.ControllerFeatureSet;
import controller.GuiControllerFeatures;
import controller.GuiImageProcessorController;
import controller.ImageProcessorController;
import mocks.LoggingSwingGuiView;
import model.color.Color;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;
import view.ImageProcessorView;
import view.SwingGuiView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests all the functionality in the GUIControllerFeatures class which updates the user
 * view every time the image is changed.
 */
public class GuiControllerTest {

  private static ImageModel testImg;
  protected static String assetDir;


  @Before
  public void setup() {
    testImg = new SimpleImage(new Color[][]{
            {new Color(0,0,0), new Color(127,127,127), new Color(255,255,255)},
            {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255)}
    });
    assetDir = System.getProperty("user.dir") + File.separator + "test" + File.separator +
            "assets" + File.separator;
  }

  @Test
  public void testConstructor() {
    assertThrows(IllegalArgumentException.class,
        () -> new GuiImageProcessorController(null, new SwingGuiView()));
    assertThrows(IllegalArgumentException.class,
        () -> new GuiImageProcessorController(new SimpleImageProcessor(), null));
  }

  @Test
  public void testControls() {
    ImageProcessor processor = new SimpleImageProcessor();
    StringBuilder log = new StringBuilder();
    ImageProcessorView loggingView = new LoggingSwingGuiView(log);
    ControllerFeatureSet control = new GuiControllerFeatures(processor, loggingView);
    ImageProcessorController controller = new GuiImageProcessorController(processor, loggingView);
    controller.run();
    assertEquals("setActionObject\n", log.toString());
    // Check that loading a file causes the view to be updated with the new image.
    control.loadFile(assetDir + "test_img.png", "img-name");
    assertEquals("setActionObject\ndisplayImage\n", log.toString());
    // Check that processing an image also updates the view.
    control.runProcessingCommand("luma-component", "img-name", "img-name", null);
    assertEquals("setActionObject\ndisplayImage\ndisplayImage\n", log.toString());
  }

}
