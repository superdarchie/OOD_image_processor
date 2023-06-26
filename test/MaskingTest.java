import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.StringReader;
import java.util.Optional;

import controller.ImageProcessorController;
import controller.ScriptedImageProcessorController;
import controller.commands.BrightenChannels;
import controller.commands.CommandImpls;
import controller.commands.Downscale;
import controller.commands.FlipHorizontally;
import controller.commands.FlipVertically;
import controller.commands.ImageProcessingCommand;
import controller.commands.MaskedCommand;
import controller.extensions.ExtendedScriptedController;
import model.color.Color;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests all the new implementation necessary to add partial image manipulation via masking into
 * the program, and that it works harmoniously with the controllers.
 */
public class MaskingTest {
  private ImageModel threeByThree;
  private ImageModel threeByThreeMask1;
  private ImageModel threeByThreeMask2;

  @Before
  public void setUp() {
    Color incrementing = new Color(0, 20, 40);
    Color[][] threeByThreePixels = new Color[3][3];
    for (int row = 0; row < threeByThreePixels.length; row++) {
      for (int col = 0; col < threeByThreePixels[0].length; col++) {
        threeByThreePixels[row][col] = incrementing;
        incrementing = incrementing.add(15, 10, 5);
      }
    }
    threeByThree = new SimpleImage(threeByThreePixels);

    Color maskColor = new Color(0, 0, 0);
    threeByThreePixels[1][0] = maskColor;
    threeByThreePixels[1][1] = maskColor;
    threeByThreePixels[2][0] = maskColor;
    threeByThreePixels[2][1] = maskColor;
    // mask of the 4 pixels making bottom left square
    threeByThreeMask1 = new SimpleImage(threeByThreePixels);

    threeByThreePixels[1][0] = maskColor.add(1, 1, 1);
    threeByThreePixels[1][1] = maskColor.add(1, 1, 1);
    threeByThreePixels[2][0] = maskColor.add(1, 1, 1);
    threeByThreePixels[2][1] = maskColor.add(1, 1, 1);
    threeByThreePixels[0][2] = maskColor;
    threeByThreePixels[1][2] = maskColor;
    threeByThreePixels[2][2] = maskColor;
    // mask of the 3 pixels on the right side
    threeByThreeMask2 = new SimpleImage(threeByThreePixels);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMaskingException1() {
    ImageProcessingCommand nullCmd = new MaskedCommand(null,
            new SimpleImage(new Color[][]{{ new Color(0, 0, 0)}}));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMaskingException2() {
    ImageProcessingCommand nullMask = new MaskedCommand(new FlipHorizontally(), null);
  }

  @Test
  public void testMaskingException3() {
    try {
      Color[][] twoByOnePixels = new Color[][]{
              { new Color(50, 42, 69), new Color(4, 2, 0) }};
      Color[][] oneByOneMask = new Color[][]{{ new Color(0, 0, 0) }};
      ImageProcessingCommand badMask = new MaskedCommand(new BrightenChannels(15),
              new SimpleImage(oneByOneMask));
      badMask.process(new SimpleImage(twoByOnePixels));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Mask dimensions do not match image dimensions.", e.getMessage());
    }
  }

  @Test
  public void testMaskingException4() {
    try {
      Color[][] oneByOnePixels = new Color[][]{{ new Color(50, 42, 69) }};
      Color[][] oneByTwoMask = new Color[][]{
              { new Color(0, 0, 0) }, { new Color(255, 255, 255)}};
      ImageProcessingCommand badMask = new MaskedCommand(new BrightenChannels(15),
              new SimpleImage(oneByTwoMask));
      badMask.process(new SimpleImage(oneByOnePixels));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Mask dimensions do not match image dimensions.", e.getMessage());
    }
  }

  @Test
  public void testMaskingException5() {
    try {
      ImageProcessingCommand goodMask = new MaskedCommand(CommandImpls.SHARPEN,
              new SimpleImage(new Color[][]{{new Color(0, 0, 0), new Color(255, 255, 255)}}));
      goodMask.process(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Supplied object is null.", e.getMessage());
    }
  }

  @Test
  public void testMaskingException6() {
    try {
      Color[][] twoByOnePixels = new Color[][]{
              { new Color(50, 42, 69), new Color(4, 2, 0) }};
      Color[][] twoByOneMask = new Color[][]{
              { new Color(0, 0, 0), new Color(255, 255, 255)}};
      ImageProcessingCommand badMask = new MaskedCommand(new Downscale(1,1),
              new SimpleImage(twoByOneMask));
      badMask.process(new SimpleImage(twoByOnePixels));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Mask dimensions do not match image dimensions.", e.getMessage());
    }
  }

  @Test
  public void testMasking() {
    // brighten
    ImageProcessingCommand brightenMaskCmd = new MaskedCommand(new BrightenChannels(16),
            threeByThreeMask1);
    ImageModel brightenMasked = brightenMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), brightenMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), brightenMasked.colorAt(0, 1));
    assertEquals(new Color(30, 40, 50), brightenMasked.colorAt(0, 2));
    assertEquals(new Color(61, 66, 71), brightenMasked.colorAt(1, 0));
    assertEquals(new Color(76, 76, 76), brightenMasked.colorAt(1, 1));
    assertEquals(new Color(75, 70, 65), brightenMasked.colorAt(1, 2));
    assertEquals(new Color(106, 96, 86), brightenMasked.colorAt(2, 0));
    assertEquals(new Color(121, 106, 91), brightenMasked.colorAt(2, 1));
    assertEquals(new Color(120, 100, 80), brightenMasked.colorAt(2, 2));

    // vertical flip
    ImageProcessingCommand vertFlipMaskCmd = new MaskedCommand(new FlipVertically(),
            threeByThreeMask1);
    ImageModel vertFlipMasked = vertFlipMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), vertFlipMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), vertFlipMasked.colorAt(0, 1));
    assertEquals(new Color(30, 40, 50), vertFlipMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), vertFlipMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), vertFlipMasked.colorAt(1, 1));
    assertEquals(new Color(75, 70, 65), vertFlipMasked.colorAt(1, 2));
    assertEquals(new Color(0, 20, 40), vertFlipMasked.colorAt(2, 0));
    assertEquals(new Color(15, 30, 45), vertFlipMasked.colorAt(2, 1));
    assertEquals(new Color(120, 100, 80), vertFlipMasked.colorAt(2, 2));

    // horizontal flip
    ImageProcessingCommand horizFlipMaskCmd = new MaskedCommand(new FlipHorizontally(),
            threeByThreeMask2);
    ImageModel horizFlipMasked = horizFlipMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), horizFlipMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), horizFlipMasked.colorAt(0, 1));
    assertEquals(new Color(0, 20, 40), horizFlipMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), horizFlipMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), horizFlipMasked.colorAt(1, 1));
    assertEquals(new Color(45, 50, 55), horizFlipMasked.colorAt(1, 2));
    assertEquals(new Color(90, 80, 70), horizFlipMasked.colorAt(2, 0));
    assertEquals(new Color(105, 90, 75), horizFlipMasked.colorAt(2, 1));
    assertEquals(new Color(90, 80, 70), horizFlipMasked.colorAt(2, 2));

    // blur
    ImageProcessingCommand blurMaskCmd = new MaskedCommand(CommandImpls.GAUSSIAN_BLUR,
            threeByThreeMask1);
    ImageModel blurMasked = blurMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), blurMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), blurMasked.colorAt(0, 1));
    assertEquals(new Color(30, 40, 50), blurMasked.colorAt(0, 2));
    assertEquals(new Color(38, 40, 43), blurMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), blurMasked.colorAt(1, 1));
    assertEquals(new Color(75, 70, 65), blurMasked.colorAt(1, 2));
    assertEquals(new Color(45, 41, 38), blurMasked.colorAt(2, 0));
    assertEquals(new Color(68, 60, 53), blurMasked.colorAt(2, 1));
    assertEquals(new Color(120, 100, 80), blurMasked.colorAt(2, 2));


    // sharpen
    ImageProcessingCommand sharpenMaskCmd = new MaskedCommand(CommandImpls.SHARPEN,
            threeByThreeMask2);
    ImageModel sharpenMasked = sharpenMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), sharpenMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), sharpenMasked.colorAt(0, 1));
    assertEquals(new Color(23, 38, 53), sharpenMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), sharpenMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), sharpenMasked.colorAt(1, 1));
    assertEquals(new Color(141, 131, 122), sharpenMasked.colorAt(1, 2));
    assertEquals(new Color(90, 80, 70), sharpenMasked.colorAt(2, 0));
    assertEquals(new Color(105, 90, 75), sharpenMasked.colorAt(2, 1));
    assertEquals(new Color(158, 128, 98), sharpenMasked.colorAt(2, 2));


    // sepia
    ImageProcessingCommand sepiaMaskCmd = new MaskedCommand(CommandImpls.SEPIA_TONE,
            threeByThreeMask2);
    ImageModel sepiaMasked = sepiaMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), sepiaMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), sepiaMasked.colorAt(0, 1));
    assertEquals(new Color(52, 46, 36), sepiaMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), sepiaMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), sepiaMasked.colorAt(1, 1));
    assertEquals(new Color(96, 85, 66), sepiaMasked.colorAt(1, 2));
    assertEquals(new Color(90, 80, 70), sepiaMasked.colorAt(2, 0));
    assertEquals(new Color(105, 90, 75), sepiaMasked.colorAt(2, 1));
    assertEquals(new Color(139, 124, 97), sepiaMasked.colorAt(2, 2));


    // greyscale
    ImageProcessingCommand lumaMaskCmd = new MaskedCommand(CommandImpls.LUMA, threeByThreeMask2);
    ImageModel lumaMasked = lumaMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), lumaMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), lumaMasked.colorAt(0, 1));
    assertEquals(new Color(39, 39, 39), lumaMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), lumaMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), lumaMasked.colorAt(1, 1));
    assertEquals(new Color(71, 71, 71), lumaMasked.colorAt(1, 2));
    assertEquals(new Color(90, 80, 70), lumaMasked.colorAt(2, 0));
    assertEquals(new Color(105, 90, 75), lumaMasked.colorAt(2, 1));
    assertEquals(new Color(103, 103, 103), lumaMasked.colorAt(2, 2));

    // components
    ImageProcessingCommand redComponentMaskCmd = new MaskedCommand(CommandImpls.RED_CHANNEL,
            threeByThreeMask1);
    ImageModel redComponentMasked = redComponentMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), redComponentMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), redComponentMasked.colorAt(0, 1));
    assertEquals(new Color(30, 40, 50), redComponentMasked.colorAt(0, 2));
    assertEquals(new Color(45, 45, 45), redComponentMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), redComponentMasked.colorAt(1, 1));
    assertEquals(new Color(75, 70, 65), redComponentMasked.colorAt(1, 2));
    assertEquals(new Color(90, 90, 90), redComponentMasked.colorAt(2, 0));
    assertEquals(new Color(105, 105, 105), redComponentMasked.colorAt(2, 1));
    assertEquals(new Color(120, 100, 80), redComponentMasked.colorAt(2, 2));

    ImageProcessingCommand greenComponentMaskCmd = new MaskedCommand(CommandImpls.GREEN_CHANNEL,
            threeByThreeMask2);
    ImageModel greenComponentMasked = greenComponentMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), greenComponentMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), greenComponentMasked.colorAt(0, 1));
    assertEquals(new Color(40, 40, 40), greenComponentMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), greenComponentMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), greenComponentMasked.colorAt(1, 1));
    assertEquals(new Color(70, 70, 70), greenComponentMasked.colorAt(1, 2));
    assertEquals(new Color(90, 80, 70), greenComponentMasked.colorAt(2, 0));
    assertEquals(new Color(105, 90, 75), greenComponentMasked.colorAt(2, 1));
    assertEquals(new Color(100, 100, 100), greenComponentMasked.colorAt(2, 2));

    ImageProcessingCommand blueComponentMaskCmd = new MaskedCommand(CommandImpls.BLUE_CHANNEL,
            threeByThreeMask1);
    ImageModel blueComponentMasked = blueComponentMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), blueComponentMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), blueComponentMasked.colorAt(0, 1));
    assertEquals(new Color(30, 40, 50), blueComponentMasked.colorAt(0, 2));
    assertEquals(new Color(55, 55, 55), blueComponentMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), blueComponentMasked.colorAt(1, 1));
    assertEquals(new Color(75, 70, 65), blueComponentMasked.colorAt(1, 2));
    assertEquals(new Color(70, 70, 70), blueComponentMasked.colorAt(2, 0));
    assertEquals(new Color(75, 75, 75), blueComponentMasked.colorAt(2, 1));
    assertEquals(new Color(120, 100, 80), blueComponentMasked.colorAt(2, 2));

    ImageProcessingCommand valueComponentMaskCmd = new MaskedCommand(CommandImpls.VALUE,
            threeByThreeMask2);
    ImageModel valueComponentMasked = valueComponentMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), valueComponentMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), valueComponentMasked.colorAt(0, 1));
    assertEquals(new Color(50, 50, 50), valueComponentMasked.colorAt(0, 2));
    assertEquals(new Color(45, 50, 55), valueComponentMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), valueComponentMasked.colorAt(1, 1));
    assertEquals(new Color(75, 75, 75), valueComponentMasked.colorAt(1, 2));
    assertEquals(new Color(90, 80, 70), valueComponentMasked.colorAt(2, 0));
    assertEquals(new Color(105, 90, 75), valueComponentMasked.colorAt(2, 1));
    assertEquals(new Color(120, 120, 120), valueComponentMasked.colorAt(2, 2));

    ImageProcessingCommand intensityComponentMaskCmd = new MaskedCommand(CommandImpls.INTENSITY,
            threeByThreeMask1);
    ImageModel intensityComponentMasked = intensityComponentMaskCmd.process(threeByThree);
    assertEquals(new Color(0, 20, 40), intensityComponentMasked.colorAt(0, 0));
    assertEquals(new Color(15, 30, 45), intensityComponentMasked.colorAt(0, 1));
    assertEquals(new Color(30, 40, 50), intensityComponentMasked.colorAt(0, 2));
    assertEquals(new Color(50, 50, 50), intensityComponentMasked.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), intensityComponentMasked.colorAt(1, 1));
    assertEquals(new Color(75, 70, 65), intensityComponentMasked.colorAt(1, 2));
    assertEquals(new Color(80, 80, 80), intensityComponentMasked.colorAt(2, 0));
    assertEquals(new Color(90, 90, 90), intensityComponentMasked.colorAt(2, 1));
    assertEquals(new Color(120, 100, 80), intensityComponentMasked.colorAt(2, 2));
  }

  @Test
  public void testMaskingFromCommands() {
    String imgName = "test-img";
    ImageProcessor model = new SimpleImageProcessor();
    model.loadImage(imgName, this.threeByThree);
    model.loadImage("test-mask", this.threeByThreeMask1);
    Readable input = new StringReader("brighten " + imgName + " " + imgName + " 16 test-mask");

    ImageProcessorController controller =
            new ExtendedScriptedController(model, input, Optional.of(out));
    controller.run();

    ImageModel expected = new SimpleImage(new Color[][]{
      {new Color(0,20, 40), new Color(15, 30, 45), new Color(30, 40, 50)},
      {new Color(61, 66, 71), new Color(76, 76, 76), new Color(75, 70, 65)},
      {new Color(106, 96, 86), new Color(121, 106, 91), new Color(120, 100, 80)}
    });

    assertEquals(expected, model.getImageState(imgName));
  }
}
