import org.junit.Before;
import org.junit.Test;

import controller.commands.BrightenChannels;
import controller.commands.CommandImpls;
import controller.commands.Filter;
import controller.commands.FlipHorizontally;
import controller.commands.FlipVertically;
import controller.commands.ImageProcessingCommand;
import controller.commands.MapChannels;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.color.Color;

import static org.junit.Assert.assertEquals;

/**
 * Tests all the implementation and uses of the image commands supported in the controller via
 * the command design pattern.
 */
public class CommandsTest {
  private ImageProcessingCommand brighten;
  private ImageProcessingCommand darken;
  private ImageProcessingCommand horizontalFlip;
  private ImageProcessingCommand verticalFlip;
  private ImageProcessingCommand makeWhite;
  private ImageModel threeByTwoImage;
  private Color[][] sevenByFive;
  private ImageModel sevenByFiveImage;
  private ImageModel twoByTwoImage;

  @Before
  public void setUp() {
    this.brighten = new BrightenChannels(10);
    this.darken = new BrightenChannels(-20);
    this.horizontalFlip = new FlipHorizontally();
    this.verticalFlip = new FlipVertically();
    this.makeWhite = new MapChannels(
        (Color c) -> {
          int value = 255;
          return new Color(value, value, value); });

    Color[][] threeByTwo = new Color[2][3];
    Color incrementing = new Color(0, 20, 30);
    for (int row = 0; row < threeByTwo.length; row++) {
      for (int col = 0; col < threeByTwo[0].length; col++) {
        threeByTwo[row][col] = incrementing;
        incrementing = incrementing.add(15, 10, 5);
      }
    }
    this.threeByTwoImage = new SimpleImage(threeByTwo);

    this.sevenByFive = new Color[5][7];
    incrementing = new Color(0, 0, 0);
    for (int row = 0; row < this.sevenByFive.length; row++) {
      for (int col = 0; col < this.sevenByFive[0].length; col++) {
        this.sevenByFive[row][col] = incrementing;
        incrementing = incrementing.add(5, 10, 15);
      }
    }
    this.sevenByFiveImage = new SimpleImage(this.sevenByFive);

    this.twoByTwoImage = new SimpleImage(new Color[][]{
            { new Color(40, 170, 100), new Color(230, 7, 50)},
            { new Color(70, 30, 50), new Color(180, 42, 211)}});
  }

  @Test
  public void testHorizontalFlip() {
    ImageModel twoByTwoFlippedHorizontal = this.horizontalFlip.process(this.twoByTwoImage);
    assertEquals(new Color(230, 7, 50), twoByTwoFlippedHorizontal.colorAt(0, 0));
    assertEquals(new Color(40, 170, 100), twoByTwoFlippedHorizontal.colorAt(0, 1));
    assertEquals(new Color(180, 42, 211), twoByTwoFlippedHorizontal.colorAt(1, 0));
    assertEquals(new Color(70, 30, 50), twoByTwoFlippedHorizontal.colorAt(1, 1));

    ImageModel sevenByFiveFlippedHorizontal = this.horizontalFlip.process(this.sevenByFiveImage);
    Color incrementing = new Color(0, 0, 0);
    for (int row = 0; row < sevenByFiveFlippedHorizontal.getHeight(); row++) {
      for (int col = sevenByFiveFlippedHorizontal.getWidth() - 1; col >= 0; col--) {
        assertEquals(incrementing, sevenByFiveFlippedHorizontal.colorAt(row, col));
        incrementing = incrementing.add(5, 10, 15);
      }
    }
  }

  @Test
  public void testVerticalFlip() {
    ImageModel twoByTwoFlippedVertical = this.verticalFlip.process(this.twoByTwoImage);
    assertEquals(new Color(70, 30, 50), twoByTwoFlippedVertical.colorAt(0, 0));
    assertEquals(new Color(180, 42, 211), twoByTwoFlippedVertical.colorAt(0, 1));
    assertEquals(new Color(40, 170, 100), twoByTwoFlippedVertical.colorAt(1, 0));
    assertEquals(new Color(230, 7, 50), twoByTwoFlippedVertical.colorAt(1, 1));

    ImageModel sevenByFiveFlippedVertical = this.verticalFlip.process(this.sevenByFiveImage);
    Color incrementing = new Color(0, 0, 0);
    for (int row = sevenByFiveFlippedVertical.getHeight() - 1; row >= 0; row--) {
      for (int col = 0; col < sevenByFiveFlippedVertical.getWidth(); col++) {
        assertEquals(incrementing, sevenByFiveFlippedVertical.colorAt(row, col));
        incrementing = incrementing.add(5, 10, 15);
      }
    }
  }

  @Test
  public void testBrightenChannels() {
    ImageModel brightenedImage = this.brighten.process(this.sevenByFiveImage);
    Color incrementing = new Color(10, 10, 10);
    for (int row = 0; row < this.sevenByFive.length; row++) {
      for (int col = 0; col < this.sevenByFive[0].length; col++) {
        assertEquals(incrementing, brightenedImage.colorAt(row, col));
        incrementing = incrementing.add(5, 10, 15);
      }
    }

    ImageModel darkenedImage = this.darken.process(this.threeByTwoImage);
    assertEquals(new Color(0, 0, 10), darkenedImage.colorAt(0, 0));
    assertEquals(new Color(0, 10, 15), darkenedImage.colorAt(0, 1));
    assertEquals(new Color(10, 20, 20), darkenedImage.colorAt(0, 2));
    assertEquals(new Color(25, 30, 25), darkenedImage.colorAt(1, 0));
    assertEquals(new Color(40, 40, 30), darkenedImage.colorAt(1, 1));
    assertEquals(new Color(55, 50, 35), darkenedImage.colorAt(1, 2));
  }

  @Test
  public void testGreyscaleRed() {
    ImageModel threeByTwoGreyscaleRed = CommandImpls.RED_CHANNEL.process(this.threeByTwoImage);
    assertEquals(new Color(0, 0, 0), threeByTwoGreyscaleRed.colorAt(0, 0));
    assertEquals(new Color(15, 15, 15), threeByTwoGreyscaleRed.colorAt(0, 1));
    assertEquals(new Color(30, 30, 30), threeByTwoGreyscaleRed.colorAt(0, 2));
    assertEquals(new Color(45, 45, 45), threeByTwoGreyscaleRed.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), threeByTwoGreyscaleRed.colorAt(1, 1));
    assertEquals(new Color(75, 75, 75), threeByTwoGreyscaleRed.colorAt(1, 2));

    ImageModel twoByTwoGreyscaleRed = CommandImpls.RED_CHANNEL.process(this.twoByTwoImage);
    assertEquals(new Color(40, 40, 40), twoByTwoGreyscaleRed.colorAt(0, 0));
    assertEquals(new Color(230, 230, 230), twoByTwoGreyscaleRed.colorAt(0, 1));
    assertEquals(new Color(70, 70, 70), twoByTwoGreyscaleRed.colorAt(1, 0));
    assertEquals(new Color(180, 180, 180), twoByTwoGreyscaleRed.colorAt(1, 1));
  }

  @Test
  public void testGreyscaleGreen() {
    ImageModel threeByTwoGreyscaleGreen = CommandImpls.GREEN_CHANNEL.process(this.threeByTwoImage);
    assertEquals(new Color(20, 20, 20), threeByTwoGreyscaleGreen.colorAt(0, 0));
    assertEquals(new Color(30, 30, 30), threeByTwoGreyscaleGreen.colorAt(0, 1));
    assertEquals(new Color(40, 40, 40), threeByTwoGreyscaleGreen.colorAt(0, 2));
    assertEquals(new Color(50, 50, 50), threeByTwoGreyscaleGreen.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), threeByTwoGreyscaleGreen.colorAt(1, 1));
    assertEquals(new Color(70, 70, 70), threeByTwoGreyscaleGreen.colorAt(1, 2));

    ImageModel twoByTwoGreyscaleGreen = CommandImpls.GREEN_CHANNEL.process(this.twoByTwoImage);
    assertEquals(new Color(170, 170, 170), twoByTwoGreyscaleGreen.colorAt(0, 0));
    assertEquals(new Color(7, 7, 7), twoByTwoGreyscaleGreen.colorAt(0, 1));
    assertEquals(new Color(30, 30, 30), twoByTwoGreyscaleGreen.colorAt(1, 0));
    assertEquals(new Color(42, 42, 42), twoByTwoGreyscaleGreen.colorAt(1, 1));
  }

  @Test
  public void testGreyscaleBlue() {
    ImageModel threeByTwoGreyscaleBlue = CommandImpls.BLUE_CHANNEL.process(this.threeByTwoImage);
    assertEquals(new Color(30, 30, 30), threeByTwoGreyscaleBlue.colorAt(0, 0));
    assertEquals(new Color(35, 35, 35), threeByTwoGreyscaleBlue.colorAt(0, 1));
    assertEquals(new Color(40, 40, 40), threeByTwoGreyscaleBlue.colorAt(0, 2));
    assertEquals(new Color(45, 45, 45), threeByTwoGreyscaleBlue.colorAt(1, 0));
    assertEquals(new Color(50, 50, 50), threeByTwoGreyscaleBlue.colorAt(1, 1));
    assertEquals(new Color(55, 55, 55), threeByTwoGreyscaleBlue.colorAt(1, 2));

    ImageModel twoByTwoGreyscaleBlue = CommandImpls.BLUE_CHANNEL.process(this.twoByTwoImage);
    assertEquals(new Color(100, 100, 100), twoByTwoGreyscaleBlue.colorAt(0, 0));
    assertEquals(new Color(50, 50, 50), twoByTwoGreyscaleBlue.colorAt(0, 1));
    assertEquals(new Color(50, 50, 50), twoByTwoGreyscaleBlue.colorAt(1, 0));
    assertEquals(new Color(211, 211, 211), twoByTwoGreyscaleBlue.colorAt(1, 1));
  }

  @Test
  public void testGreyscaleValue() {
    ImageModel threeByTwoGreyscaleValue = CommandImpls.VALUE.process(this.threeByTwoImage);
    assertEquals(new Color(30, 30, 30), threeByTwoGreyscaleValue.colorAt(0, 0));
    assertEquals(new Color(35, 35, 35), threeByTwoGreyscaleValue.colorAt(0, 1));
    assertEquals(new Color(40, 40, 40), threeByTwoGreyscaleValue.colorAt(0, 2));
    assertEquals(new Color(50, 50, 50), threeByTwoGreyscaleValue.colorAt(1, 0));
    assertEquals(new Color(60, 60, 60), threeByTwoGreyscaleValue.colorAt(1, 1));
    assertEquals(new Color(75, 75, 75), threeByTwoGreyscaleValue.colorAt(1, 2));

    ImageModel twoByTwoGreyscaleValue = CommandImpls.VALUE.process(this.twoByTwoImage);
    assertEquals(new Color(170, 170, 170), twoByTwoGreyscaleValue.colorAt(0, 0));
    assertEquals(new Color(230, 230, 230), twoByTwoGreyscaleValue.colorAt(0, 1));
    assertEquals(new Color(70, 70, 70), twoByTwoGreyscaleValue.colorAt(1, 0));
    assertEquals(new Color(211, 211, 211), twoByTwoGreyscaleValue.colorAt(1, 1));
  }

  @Test
  public void testGreyscaleIntensity() {
    ImageModel threeByTwoGreyscaleIntensity = CommandImpls.INTENSITY.process(this.threeByTwoImage);
    assertEquals(new Color(16, 16, 16), threeByTwoGreyscaleIntensity.colorAt(0, 0));
    assertEquals(new Color(26, 26, 26), threeByTwoGreyscaleIntensity.colorAt(0, 1));
    assertEquals(new Color(36, 36, 36), threeByTwoGreyscaleIntensity.colorAt(0, 2));
    assertEquals(new Color(46, 46, 46), threeByTwoGreyscaleIntensity.colorAt(1, 0));
    assertEquals(new Color(56, 56, 56), threeByTwoGreyscaleIntensity.colorAt(1, 1));
    assertEquals(new Color(66, 66, 66), threeByTwoGreyscaleIntensity.colorAt(1, 2));

    ImageModel twoByTwoGreyscaleIntensity = CommandImpls.INTENSITY.process(this.twoByTwoImage);
    assertEquals(new Color(103, 103, 103), twoByTwoGreyscaleIntensity.colorAt(0, 0));
    assertEquals(new Color(95, 95, 95), twoByTwoGreyscaleIntensity.colorAt(0, 1));
    assertEquals(new Color(50, 50, 50), twoByTwoGreyscaleIntensity.colorAt(1, 0));
    assertEquals(new Color(144, 144, 144), twoByTwoGreyscaleIntensity.colorAt(1, 1));
  }

  @Test
  public void testGreyscaleLuma() {
    ImageModel threeByTwoGreyscaleLuma = CommandImpls.LUMA.process(this.threeByTwoImage);
    assertEquals(new Color(16, 16, 16), threeByTwoGreyscaleLuma.colorAt(0, 0));
    assertEquals(new Color(27, 27, 27), threeByTwoGreyscaleLuma.colorAt(0, 1));
    assertEquals(new Color(38, 38, 38), threeByTwoGreyscaleLuma.colorAt(0, 2));
    assertEquals(new Color(49, 49, 49), threeByTwoGreyscaleLuma.colorAt(1, 0));
    assertEquals(new Color(59, 59, 59), threeByTwoGreyscaleLuma.colorAt(1, 1));
    assertEquals(new Color(70, 70, 70), threeByTwoGreyscaleLuma.colorAt(1, 2));

    ImageModel twoByTwoGreyscaleLuma = CommandImpls.LUMA.process(this.twoByTwoImage);
    assertEquals(new Color(137, 137, 137), twoByTwoGreyscaleLuma.colorAt(0, 0));
    assertEquals(new Color(58, 58, 58), twoByTwoGreyscaleLuma.colorAt(0, 1));
    assertEquals(new Color(40, 40, 40), twoByTwoGreyscaleLuma.colorAt(1, 0));
    assertEquals(new Color(84, 84, 84), twoByTwoGreyscaleLuma.colorAt(1, 1));
  }

  @Test
  public void testMapChannels() {
    ImageModel threeByTwoAllWhite = this.makeWhite.process(this.threeByTwoImage);
    for (int row = 0; row < threeByTwoAllWhite.getHeight(); row++) {
      for (int col = 0; col < threeByTwoAllWhite.getWidth(); col++) {
        assertEquals(new Color(255, 255, 255), threeByTwoAllWhite.colorAt(row, col));
      }
    }
  }

  @Test
  public void testFilter() {
    ImageProcessingCommand threeByOneFilter = new Filter(new float[][]{{0.2f, 0.4f, -0.3f}});
    ImageModel threeByTwoFiltered = threeByOneFilter.process(this.threeByTwoImage);
    assertEquals(new Color(0,0,2), threeByTwoFiltered.colorAt(0, 0));
    assertEquals(new Color(0,4,8), threeByTwoFiltered.colorAt(0, 1));
    assertEquals(new Color(15,22,23), threeByTwoFiltered.colorAt(0, 2));
    assertEquals(new Color(0,2,3), threeByTwoFiltered.colorAt(1, 0));
    assertEquals(new Color(11,13,13), threeByTwoFiltered.colorAt(1, 1));
    assertEquals(new Color(42,40,32), threeByTwoFiltered.colorAt(1, 2));
  }

  @Test
  public void testGaussianBlurFilter() {
    ImageModel threeByTwoGaussianBlur = CommandImpls.GAUSSIAN_BLUR.process(this.threeByTwoImage);
    assertEquals(new Color(11, 19, 21), threeByTwoGaussianBlur.colorAt(0, 0));
    assertEquals(new Color(23, 30, 30), threeByTwoGaussianBlur.colorAt(0, 1));
    assertEquals(new Color(23, 26, 24), threeByTwoGaussianBlur.colorAt(0, 2));
    assertEquals(new Color(20, 24, 23), threeByTwoGaussianBlur.colorAt(1, 0));
    assertEquals(new Color(34, 38, 34), threeByTwoGaussianBlur.colorAt(1, 1));
    assertEquals(new Color(31, 32, 27), threeByTwoGaussianBlur.colorAt(1, 2));
  }

  @Test
  public void testGaussianBlurLarge() {
    ImageModel sevenByFiveGaussianBlur = CommandImpls.GAUSSIAN_BLUR.process(this.sevenByFiveImage);
    assertEquals(new Color(8, 15, 23), sevenByFiveGaussianBlur.colorAt(0, 0));
    assertEquals(new Color(13, 25, 38), sevenByFiveGaussianBlur.colorAt(0, 1));
    assertEquals(new Color(16, 33, 49), sevenByFiveGaussianBlur.colorAt(0, 2));
    assertEquals(new Color(20, 40, 60), sevenByFiveGaussianBlur.colorAt(0, 3));
    assertEquals(new Color(24, 48, 71), sevenByFiveGaussianBlur.colorAt(0, 4));
    assertEquals(new Color(28, 55, 83), sevenByFiveGaussianBlur.colorAt(0, 5));
    assertEquals(new Color(23, 45, 68), sevenByFiveGaussianBlur.colorAt(0, 6));
    assertEquals(new Color(28, 55, 83), sevenByFiveGaussianBlur.colorAt(1, 0));
    assertEquals(new Color(40, 80, 120), sevenByFiveGaussianBlur.colorAt(1, 1));
    assertEquals(new Color(45, 90, 135), sevenByFiveGaussianBlur.colorAt(1, 2));
    assertEquals(new Color(50, 100, 149), sevenByFiveGaussianBlur.colorAt(1, 3));
    assertEquals(new Color(55, 110, 161), sevenByFiveGaussianBlur.colorAt(1, 4));
    assertEquals(new Color(60, 120, 173), sevenByFiveGaussianBlur.colorAt(1, 5));
    assertEquals(new Color(48, 95, 135), sevenByFiveGaussianBlur.colorAt(1, 6));
    assertEquals(new Color(54, 108, 149), sevenByFiveGaussianBlur.colorAt(2, 0));
    assertEquals(new Color(75, 150, 206), sevenByFiveGaussianBlur.colorAt(2, 1));
    assertEquals(new Color(80, 160, 218), sevenByFiveGaussianBlur.colorAt(2, 2));
    assertEquals(new Color(85, 170, 227), sevenByFiveGaussianBlur.colorAt(2, 3));
    assertEquals(new Color(90, 180, 233), sevenByFiveGaussianBlur.colorAt(2, 4));
    assertEquals(new Color(95, 188, 236), sevenByFiveGaussianBlur.colorAt(2, 5));
    assertEquals(new Color(74, 145, 179), sevenByFiveGaussianBlur.colorAt(2, 6));
    assertEquals(new Color(80, 155, 184), sevenByFiveGaussianBlur.colorAt(3, 0));
    assertEquals(new Color(110, 211, 248), sevenByFiveGaussianBlur.colorAt(3, 1));
    assertEquals(new Color(115, 219, 251), sevenByFiveGaussianBlur.colorAt(3, 2));
    assertEquals(new Color(120, 226, 254), sevenByFiveGaussianBlur.colorAt(3, 3));
    assertEquals(new Color(125, 233, 255), sevenByFiveGaussianBlur.colorAt(3, 4));
    assertEquals(new Color(130, 238, 255), sevenByFiveGaussianBlur.colorAt(3, 5));
    assertEquals(new Color(100, 180, 191), sevenByFiveGaussianBlur.colorAt(3, 6));
    assertEquals(new Color(73, 136, 143), sevenByFiveGaussianBlur.colorAt(4, 0));
    assertEquals(new Color(100, 183, 191), sevenByFiveGaussianBlur.colorAt(4, 1));
    assertEquals(new Color(104, 185, 191), sevenByFiveGaussianBlur.colorAt(4, 2));
    assertEquals(new Color(108, 188, 191), sevenByFiveGaussianBlur.colorAt(4, 3));
    assertEquals(new Color(111, 190, 191), sevenByFiveGaussianBlur.colorAt(4, 4));
    assertEquals(new Color(115, 191, 191), sevenByFiveGaussianBlur.colorAt(4, 5));
    assertEquals(new Color(88, 143, 143), sevenByFiveGaussianBlur.colorAt(4, 6));
  }

  @Test
  public void testSharpenFilter() {
    Color[][] threeByThree = new Color[3][3];
    Color incrementing = new Color(0, 20, 30);
    for (int row = 0; row < threeByThree.length; row++) {
      for (int col = 0; col < threeByThree[0].length; col++) {
        threeByThree[row][col] = incrementing;
        incrementing = incrementing.add(15, 10, 5);
      }
    }
    ImageModel threeByThreeImage = new SimpleImage(threeByThree);

    ImageModel threeByThreeSharpened = CommandImpls.SHARPEN.process(threeByThreeImage);
    assertEquals(new Color(0, 8, 26), threeByThreeSharpened.colorAt(0, 0));
    assertEquals(new Color(28, 56, 66), threeByThreeSharpened.colorAt(0, 1));
    assertEquals(new Color(23, 38, 41), threeByThreeSharpened.colorAt(0, 2));
    assertEquals(new Color(84, 94, 84), threeByThreeSharpened.colorAt(1, 0));
    assertEquals(new Color(180, 180, 150), threeByThreeSharpened.colorAt(1, 1));
    assertEquals(new Color(141, 131, 103), threeByThreeSharpened.colorAt(1, 2));
    assertEquals(new Color(113, 98, 71), threeByThreeSharpened.colorAt(2, 0));
    assertEquals(new Color(197, 169, 122), threeByThreeSharpened.colorAt(2, 1));
    assertEquals(new Color(158, 128, 86), threeByThreeSharpened.colorAt(2, 2));
  }

  @Test
  public void testSharpenLarge() {
    ImageModel sevenByFiveSharp = CommandImpls.SHARPEN.process(this.sevenByFiveImage);
    assertEquals(new Color(0, 0, 0), sevenByFiveSharp.colorAt(0, 0));
    assertEquals(new Color(0, 0, 0), sevenByFiveSharp.colorAt(0, 1));
    assertEquals(new Color(0, 0, 0), sevenByFiveSharp.colorAt(0, 2));
    assertEquals(new Color(0, 0, 0), sevenByFiveSharp.colorAt(0, 3));
    assertEquals(new Color(0, 0, 0), sevenByFiveSharp.colorAt(0, 4));
    assertEquals(new Color(28, 56, 96), sevenByFiveSharp.colorAt(0, 5));
    assertEquals(new Color(23, 45, 79), sevenByFiveSharp.colorAt(0, 6));
    assertEquals(new Color(24, 49, 101), sevenByFiveSharp.colorAt(1, 0));
    assertEquals(new Color(45, 90, 176), sevenByFiveSharp.colorAt(1, 1));
    assertEquals(new Color(29, 59, 146), sevenByFiveSharp.colorAt(1, 2));
    assertEquals(new Color(38, 76, 178), sevenByFiveSharp.colorAt(1, 3));
    assertEquals(new Color(46, 94, 206), sevenByFiveSharp.colorAt(1, 4));
    assertEquals(new Color(98, 198, 255), sevenByFiveSharp.colorAt(1, 5));
    assertEquals(new Color(73, 149, 253), sevenByFiveSharp.colorAt(1, 6));
    assertEquals(new Color(75, 163, 255), sevenByFiveSharp.colorAt(2, 0));
    assertEquals(new Color(116, 251, 255), sevenByFiveSharp.colorAt(2, 1));
    assertEquals(new Color(80, 188, 255), sevenByFiveSharp.colorAt(2, 2));
    assertEquals(new Color(85, 205, 255), sevenByFiveSharp.colorAt(2, 3));
    assertEquals(new Color(90, 221, 255), sevenByFiveSharp.colorAt(2, 4));
    assertEquals(new Color(161, 255, 255), sevenByFiveSharp.colorAt(2, 5));
    assertEquals(new Color(116, 255, 255), sevenByFiveSharp.colorAt(2, 6));
    assertEquals(new Color(182, 255, 255), sevenByFiveSharp.colorAt(3, 0));
    assertEquals(new Color(255, 255, 255), sevenByFiveSharp.colorAt(3, 1));
    assertEquals(new Color(231, 255, 255), sevenByFiveSharp.colorAt(3, 2));
    assertEquals(new Color(239, 255, 255), sevenByFiveSharp.colorAt(3, 3));
    assertEquals(new Color(247, 255, 255), sevenByFiveSharp.colorAt(3, 4));
    assertEquals(new Color(255, 255, 255), sevenByFiveSharp.colorAt(3, 5));
    assertEquals(new Color(231, 255, 255), sevenByFiveSharp.colorAt(3, 6));
    assertEquals(new Color(169, 255, 255), sevenByFiveSharp.colorAt(4, 0));
    assertEquals(new Color(227, 255, 255), sevenByFiveSharp.colorAt(4, 1));
    assertEquals(new Color(195, 255, 255), sevenByFiveSharp.colorAt(4, 2));
    assertEquals(new Color(201, 255, 255), sevenByFiveSharp.colorAt(4, 3));
    assertEquals(new Color(206, 255, 255), sevenByFiveSharp.colorAt(4, 4));
    assertEquals(new Color(255, 255, 255), sevenByFiveSharp.colorAt(4, 5));
    assertEquals(new Color(206, 255, 255), sevenByFiveSharp.colorAt(4, 6));
  }

  @Test
  public void testSepiaTone() {
    ImageModel threeByTwoSepiaTone = CommandImpls.SEPIA_TONE.process(this.threeByTwoImage);
    assertEquals(new Color(21,19,15), threeByTwoSepiaTone.colorAt(0, 0));
    assertEquals(new Color(36,32,25), threeByTwoSepiaTone.colorAt(0, 1));
    assertEquals(new Color(50,45,35), threeByTwoSepiaTone.colorAt(0, 2));
    assertEquals(new Color(65,58,45), threeByTwoSepiaTone.colorAt(1, 0));
    assertEquals(new Color(79,71,55), threeByTwoSepiaTone.colorAt(1, 1));
    assertEquals(new Color(94,83,65), threeByTwoSepiaTone.colorAt(1, 2));

    ImageModel twoByTwoSepiaTone = CommandImpls.SEPIA_TONE.process(this.twoByTwoImage);
    assertEquals(new Color(165,147,115), twoByTwoSepiaTone.colorAt(0, 0));
    assertEquals(new Color(105,93,73), twoByTwoSepiaTone.colorAt(0, 1));
    assertEquals(new Color(60,53,42), twoByTwoSepiaTone.colorAt(1, 0));
    assertEquals(new Color(143,127,99), twoByTwoSepiaTone.colorAt(1, 1));
  }
}
