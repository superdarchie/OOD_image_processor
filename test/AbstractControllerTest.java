import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Optional;

import controller.ImageProcessorController;
import mocks.LoggingImageProcessor;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;
import model.color.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests all the general functionality for the controller package, which modifies images (including
 * load and save) in response to input.
 * All image processing controllers should have similar constraints on null arguments,
 * and similar responses to valid input.
 */
public abstract class AbstractControllerTest {
  protected static ImageModel testImg;
  protected static String testImgString;
  protected static ImageModel testImgModified;
  protected static String testImgModifiedString;
  protected static String assetDir;

  /**
   * Initializes image testing assets.
   */
  @BeforeClass
  public static void setUp() {
    testImg = new SimpleImage(new Color[][]{
      {new Color(0,0,0), new Color(127,127,127), new Color(255,255,255)},
      {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255)}
    });
    testImgModified = new SimpleImage(new Color[][]{
        {new Color(245,0,0), new Color(0,245,0), new Color(0,0,245)},
        {new Color(0,0,0), new Color(117,117,117), new Color(245,245,245)}
    });
    String sep = System.lineSeparator();
    testImgString = "P3" + sep +
            "3 2" + sep +
            "255" + sep +
            "0" + sep +
            "0" + sep +
            "0" + sep +
            "127" + sep +
            "127" + sep +
            "127" + sep +
            "255" + sep +
            "255" + sep +
            "255" + sep +
            "255" + sep +
            "0" + sep +
            "0" + sep +
            "0" + sep +
            "255" + sep +
            "0" + sep +
            "0" + sep +
            "0" + sep +
            "255";
    testImgModifiedString = "P3" + sep +
            "3 2" + sep +
            "255" + sep +
            "245" + sep +
            "0" + sep +
            "0" + sep +
            "0" + sep +
            "245" + sep +
            "0" + sep +
            "0" + sep +
            "0" + sep +
            "245" + sep +
            "0" + sep +
            "0" + sep +
            "0" + sep +
            "117" + sep +
            "117" + sep +
            "117" + sep +
            "245" + sep +
            "245" + sep +
            "245";
    assetDir = System.getProperty("user.dir") + File.separator + "test" + File.separator +
            "assets" + File.separator;
  }

  /**
   * Create a new implementation specific controller to be tested.
   * @param processor the processor to use
   * @param input the input to use
   * @param dest the override destination to use
   * @return the constructed controller
   */
  protected abstract ImageProcessorController makeController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> dest
  );

  @Test
  public void testNoNullProcessor() {
    assertThrows(IllegalArgumentException.class,
        () -> this.makeController(
              null,
              new StringReader(""),
              Optional.empty()));
  }

  @Test
  public void testNoNullInput() {
    assertThrows(IllegalArgumentException.class,
        () -> this.makeController(
              new SimpleImageProcessor(),
              null,
              Optional.empty()));
  }

  @Test
  public void testNoNullDestOverride() {
    assertThrows(IllegalArgumentException.class,
        () -> this.makeController(
              new SimpleImageProcessor(),
              new StringReader(""),
              null));
  }

  @Test
  public void testImageLoad() {
    ImageProcessor processor = new SimpleImageProcessor();
    String command = "load " + assetDir + "test_img.ppm test-img";
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageProcessorController controller = this.makeController(
        processor,
        new StringReader(command),
        Optional.of(out)
    );
    controller.run();
    assertEquals(testImg, processor.getImageState("test-img"));
  }

  @Test
  public void testImageSave() {
    ImageProcessor processor = new SimpleImageProcessor();
    processor.loadImage("test-img", testImg);
    String command = "save test-img " + assetDir + "test_img.ppm";
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageProcessorController controller = this.makeController(
        processor,
        new StringReader(command),
        Optional.of(out)
    );
    controller.run();
    assertEquals(testImgString, out.toString());
  }

  @Test
  public void testImageModification() {
    ImageProcessor processor = new SimpleImageProcessor();
    String command = "load " + assetDir + "test_img.ppm test-img\n" +
        "brighten test-img bright-test -10\n" +
        "vertical-flip bright-test bright-flip\n" +
        "save bright-flip " + assetDir + "test_img_bright_flip.ppm";
    ByteArrayOutputStream dest = new ByteArrayOutputStream();
    ImageProcessorController controller = this.makeController(
        processor,
        new StringReader(command),
        Optional.of(dest)
    );
    controller.run();
    assertEquals(testImgModified, processor.getImageState("bright-flip"));
    assertEquals(testImgModifiedString, dest.toString());
  }

  @Test
  public void testImageModificationFileSave() {
    ImageProcessor processor = new SimpleImageProcessor();
    String command = "load " + assetDir + "test_img.ppm test-img\n" +
        "brighten test-img bright-test -10\n" +
        "vertical-flip bright-test bright-flip\n" +
        "save bright-flip " + assetDir + "test_img_bright_flip.ppm\n" +
        "load " + assetDir + "test_img_bright_flip.ppm result";
    ImageProcessorController controller = this.makeController(
        processor,
        new StringReader(command),
        Optional.empty()
    );
    controller.run();
    assertEquals(testImgModified, processor.getImageState("bright-flip"));
    assertEquals(testImgModified, processor.getImageState("result"));
  }

  @Test
  public void testControllingProcessor() {
    StringBuilder log = new StringBuilder();
    ImageProcessor processor = new LoggingImageProcessor(log);
    String command = "load " + assetDir + "test_img.ppm test-img\n" +
        "brighten test-img bright-test -10\n" +
        "vertical-flip bright-test bright-flip\n" +
        "save bright-flip " + assetDir + "test_img_bright_flip.ppm\n" +
        "load " + assetDir + "test_img_bright_flip.ppm result";
    ImageProcessorController controller = this.makeController(
        processor,
        new StringReader(command),
        Optional.empty()
    );
    controller.run();
    assertEquals("loadImage with name test-img\n" +
        "processImage with name test-img, saveName bright-test\n" +
        "processImage with name bright-test, saveName bright-flip\n" +
        "getImageState with name bright-flip\n" +
        "loadImage with name result\n",
        log.toString());
  }

}
