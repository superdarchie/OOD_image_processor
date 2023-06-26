import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import controller.ControllerFeatureSet;
import controller.ControllerFeatures;
import model.color.Color;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;
import util.ImageUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

/**
 * Tests all the functionality in the ControllerFeatures class, which represents the shared
 * functionality among controllers - loading/saving images and supported image processing commands.
 */
public class ControllerFeaturesTest {

  private static ControllerFeatureSet features;
  private static ImageProcessor processor;
  private static ImageModel testImg;
  protected static String assetDir;


  @Before
  public void setup() {
    processor = new SimpleImageProcessor();
    features = new ControllerFeatures(processor);
    testImg = new SimpleImage(new Color[][]{
        {new Color(0,0,0), new Color(127,127,127), new Color(255,255,255)},
        {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255)}
    });
    assetDir = System.getProperty("user.dir") + File.separator + "test" + File.separator +
            "assets" + File.separator;
  }

  @Test
  public void testLoadFile() {
    assertThrows(IllegalArgumentException.class, () -> processor.getImageState("test-img"));
    features.loadFile(assetDir + "test_img.ppm", "test-img");
    assertEquals(testImg, processor.getImageState("test-img"));

    try {
      features.loadFile(assetDir + "non-existent", "test-img");
    } catch (IllegalStateException e) {
      assertEquals("Could not find specified file to read from.", e.getMessage());
    }
  }

  @Test
  public void testSaveImage() {
    processor.loadImage("test-img", testImg);
    features.saveImage("test-img", assetDir + "test_save.png");
    try {
      ImageModel savedImage = new SimpleImage(
          ImageUtil.readImage(
              assetDir + "test_save.png", new FileInputStream(assetDir + "test_save.png")
          )
      );
      assertEquals(testImg, savedImage);
    } catch (FileNotFoundException e) {
      fail();
    }
  }

  @Test
  public void testProcessingCommandThrows() {
    processor.loadImage("test-img", testImg);

    assertThrows(IllegalStateException.class,
        () -> features.runProcessingCommand("X", "test-img", "2", null));

    assertThrows(IllegalArgumentException.class,
        () -> features.runProcessingCommand("brighten", "test-img", "test-img", null));

    assertThrows(IllegalStateException.class,
        () -> features.runProcessingCommand("brighten", "test-img", "test-img", new Scanner("")));
  }

  @Test
  public void testRunProcessingCommand() {
    processor.loadImage("test-img", testImg);

    features.runProcessingCommand("horizontal-flip", "test-img", "test-img", null);
    features.runProcessingCommand("brighten", "test-img", "test-img", new Scanner("-10"));
    features.runProcessingCommand("gaussian-blur", "test-img", "test-img", null);

    ImageModel resultImg = processor.getImageState("test-img");
    ImageModel expectedImg = new SimpleImage(new Color[][]{
        { new Color(76,91,107), new Color(75,91,75), new Color(45,30,15) },
        { new Color(38,69,99), new Color(61,91,61), new Color(69,38,7) },
    });
    assertEquals(expectedImg, resultImg);
  }

}
