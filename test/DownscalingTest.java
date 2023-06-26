import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Optional;

import controller.ImageProcessorController;
import controller.commands.Downscale;
import controller.commands.ImageProcessingCommand;
import controller.extensions.ExtendedGuiController;
import controller.extensions.ExtendedScriptedController;
import mocks.MockDownscaleView;
import model.color.Color;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests all the new implementation necessary to add image downscaling into
 * the program, and that it works harmoniously with the controllers and GUI view.
 */
public class DownscalingTest {
  private ImageModel tenByTen;
  private ImageModel downscaledFourByFour;

  @Before
  public void setUp() {
    Color incrementing = new Color(0, 0, 0);
    Color[][] tenByTenPixels = new Color[10][10];
    for (int row = 0; row < tenByTenPixels.length; row++) {
      for (int col = 0; col < tenByTenPixels[0].length; col++) {
        tenByTenPixels[row][col] = incrementing;
        incrementing = incrementing.add(2, 2, 2);
      }
    }
    this.tenByTen = new SimpleImage(tenByTenPixels);

    Color[][] fourByFourPixels = new Color[4][4];
    fourByFourPixels[0][0] = new Color(0, 0, 0);
    fourByFourPixels[0][1] = new Color(5, 5, 5);
    fourByFourPixels[0][2] = new Color(10, 10, 10);
    fourByFourPixels[0][3] = new Color(15, 15, 15);
    fourByFourPixels[1][0] = new Color(50, 50, 50);
    fourByFourPixels[1][1] = new Color(55, 55, 55);
    fourByFourPixels[1][2] = new Color(60, 60, 60);
    fourByFourPixels[1][3] = new Color(65, 65, 65);
    fourByFourPixels[2][0] = new Color(100, 100, 100);
    fourByFourPixels[2][1] = new Color(105, 105, 105);
    fourByFourPixels[2][2] = new Color(110, 110, 110);
    fourByFourPixels[2][3] = new Color(115, 115, 115);
    fourByFourPixels[3][0] = new Color(150, 150, 150);
    fourByFourPixels[3][1] = new Color(155, 155, 155);
    fourByFourPixels[3][2] = new Color(160, 160, 160);
    fourByFourPixels[3][3] = new Color(165, 165, 165);
    this.downscaledFourByFour = new SimpleImage(fourByFourPixels);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDownscaleException1() {
    ImageProcessingCommand badWidth = new Downscale(0, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDownscaleException2() {
    ImageProcessingCommand badHeight = new Downscale(3, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDownscaleException3() {
    ImageProcessingCommand badWidthAndHeight = new Downscale(-5, -13);
  }

  @Test
  public void testDownscaleException4() {
    try {
      ImageProcessingCommand downscale = new Downscale(10, 10);
      downscale.process(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Supplied object is null.", e.getMessage());
    }
  }

  @Test
  public void testDownscaleException5() {
    try {
      ImageProcessingCommand downscale = new Downscale(1, 2);
      downscale.process(new SimpleImage(new Color[][]{
              {new Color(50, 50, 50), new Color(42, 17, 100)}}));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("The supplied image must be larger than this downscaled dimensions of: "
              + "1 x 2", e.getMessage());
    }
  }

  @Test
  public void testDownscaleException6() {
    try {
      ImageProcessingCommand downscale = new Downscale(2, 1);
      downscale.process(new SimpleImage(new Color[][]{
              {new Color(50, 50, 50)}, {new Color(42, 17, 100)}}));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("The supplied image must be larger than this downscaled dimensions of: "
              + "2 x 1", e.getMessage());
    }
  }

  @Test
  public void testDownscale() {
    // downscaling an image of the same dimensions returns the original image
    ImageProcessingCommand downscale10x10Cmd = new Downscale(10, 10);
    ImageModel tenByTenDownscaled = downscale10x10Cmd.process(this.tenByTen);
    assertEquals(this.tenByTen, tenByTenDownscaled);
    // downscaling a downscaled image of the same dimensions returns the original image
    ImageModel tenByTenDownscaledTwice = downscale10x10Cmd.process(tenByTenDownscaled);
    assertEquals(this.tenByTen, tenByTenDownscaledTwice);

    // testing the same aspect ratio
    ImageProcessingCommand downscale4x4Cmd = new Downscale(4, 4);
    ImageModel downscaled4x4 = downscale4x4Cmd.process(this.tenByTen);
    assertTrue(downscaled4x4.getWidth() == 4 && downscaled4x4.getHeight() == 4);
    assertEquals(this.downscaledFourByFour, downscaled4x4);

    // testing a different aspect ratio
    ImageProcessingCommand downscale5x3Cmd = new Downscale(5, 3);
    ImageModel downscaled5x3 = downscale5x3Cmd.process(this.tenByTen);
    assertTrue(downscaled5x3.getWidth() == 5 && downscaled5x3.getHeight() == 3);
    assertEquals(new Color(0, 0, 0), downscaled5x3.colorAt(0, 0));
    assertEquals(new Color(4, 4, 4), downscaled5x3.colorAt(0, 1));
    assertEquals(new Color(8, 8, 8), downscaled5x3.colorAt(0, 2));
    assertEquals(new Color(12, 12, 12), downscaled5x3.colorAt(0, 3));
    assertEquals(new Color(16, 16, 16), downscaled5x3.colorAt(0, 4));
    assertEquals(new Color(73, 73, 73), downscaled5x3.colorAt(1, 0));
    assertEquals(new Color(77, 77, 77), downscaled5x3.colorAt(1, 1));
    assertEquals(new Color(81, 81, 81), downscaled5x3.colorAt(1, 2));
    assertEquals(new Color(85, 85, 85), downscaled5x3.colorAt(1, 3));
    assertEquals(new Color(89, 89, 89), downscaled5x3.colorAt(1, 4));
    assertEquals(new Color(126, 126, 126), downscaled5x3.colorAt(2, 0));
    assertEquals(new Color(130, 130, 130), downscaled5x3.colorAt(2, 1));
    assertEquals(new Color(134, 134, 134), downscaled5x3.colorAt(2, 2));
    assertEquals(new Color(138, 138, 138), downscaled5x3.colorAt(2, 3));
    assertEquals(new Color(142, 142, 142), downscaled5x3.colorAt(2, 4));

    // testing downscale calculation with different rgb values
    Color[][] threeByThreePixels = new Color[3][3];
    threeByThreePixels[0][0] = new Color(0, 50, 100);
    threeByThreePixels[0][1] = new Color(30, 60, 90);
    threeByThreePixels[0][2] = new Color(70, 70, 70);
    threeByThreePixels[1][0] = new Color(130, 50, 7);
    threeByThreePixels[1][1] = new Color(5, 8, 6);
    threeByThreePixels[1][2] = new Color(40, 50, 60);
    threeByThreePixels[2][0] = new Color(100, 30, 0);
    threeByThreePixels[2][1] = new Color(0, 10, 200);
    threeByThreePixels[2][2] = new Color(210, 160, 30);
    ImageProcessingCommand downscale1x2Cmd = new Downscale(1, 2);
    ImageModel downscaled1x2 = downscale1x2Cmd.process(new SimpleImage(threeByThreePixels));
    assertEquals(downscaled1x2, new SimpleImage(new Color[][]{
            { new Color(0, 50, 100) },
            { new Color(115, 40, 3) }}));
  }

  @Test
  public void testDownscaleFromView() {
    String imgName = "test-img";
    MockDownscaleView view = new MockDownscaleView(imgName);
    ImageProcessor model = new SimpleImageProcessor();
    ImageProcessorController guiController = new ExtendedGuiController(model, view);
    guiController.run();
    view.downscaleWidth.setText("4");
    view.downscaleHeight.setText("4");
    model.loadImage(imgName, this.tenByTen);
    view.downscaleButton.doClick();
    assertEquals(this.downscaledFourByFour, model.getImageState(imgName));
  }

  @Test
  public void testDownscaleFromTextCommands() {
    String imgName = "test-img";
    ImageProcessor model = new SimpleImageProcessor();
    model.loadImage(imgName, this.tenByTen);

    Readable input = new StringReader("downscale " + imgName + " " + imgName + " 4 4");

    ImageProcessorController controller =
            new ExtendedScriptedController(model, input, Optional.of(out));
    controller.run();

    assertEquals(this.downscaledFourByFour, model.getImageState(imgName));
  }
}
