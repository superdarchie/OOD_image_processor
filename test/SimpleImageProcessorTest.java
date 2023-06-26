import org.junit.Before;
import org.junit.Test;

import controller.commands.BrightenChannels;
import controller.commands.FlipHorizontally;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;
import model.color.Color;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the simple image processor.
 * Ensures that the processor can be set up correctly,
 * handles incorrect method calls correctly,
 * and is actually able to process images.
 */
public class SimpleImageProcessorTest {
  Color[][] threeByThreePixels;
  ImageModel threeByThreeImage;
  Color[][] threeByThreePixelsBrightened;
  ImageModel threeByThreeImageBrightened;
  Color[][] threeByThreePixelsStrongBrightened;
  ImageModel threeByThreeImageStrongBrightened;
  Color[][] threeByThreePixelsHorizontal;
  ImageModel threeByThreeImageHorizontal;
  ImageModel oneByOnePixel;
  ImageProcessor processor;

  @Before
  public void setUp() {
    this.threeByThreePixels = new Color[3][3];
    Color incrementing = new Color(0, 0, 0);
    for (int row = 0; row < this.threeByThreePixels.length; row++) {
      for (int col = 0; col < this.threeByThreePixels[0].length; col++) {
        this.threeByThreePixels[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.threeByThreeImage = new SimpleImage(this.threeByThreePixels);

    this.threeByThreePixelsBrightened = new Color[3][3];
    incrementing = new Color(10, 10, 10);
    for (int row = 0; row < this.threeByThreePixelsBrightened.length; row++) {
      for (int col = 0; col < this.threeByThreePixelsBrightened[0].length; col++) {
        this.threeByThreePixelsBrightened[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.threeByThreeImageBrightened = new SimpleImage(this.threeByThreePixelsBrightened);

    this.threeByThreePixelsStrongBrightened = new Color[3][3];
    incrementing = new Color(32, 32, 32);
    for (int row = 0; row < this.threeByThreePixelsStrongBrightened.length; row++) {
      for (int col = 0; col < this.threeByThreePixelsStrongBrightened[0].length; col++) {
        this.threeByThreePixelsStrongBrightened[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.threeByThreeImageStrongBrightened
            = new SimpleImage(this.threeByThreePixelsStrongBrightened);

    this.threeByThreePixelsHorizontal = new Color[3][3];
    incrementing = new Color(0, 0, 0);
    for (int row = 0; row < this.threeByThreePixelsHorizontal.length; row++) {
      for (int col = this.threeByThreePixelsHorizontal[0].length - 1; col >= 0; col--) {
        this.threeByThreePixelsHorizontal[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.threeByThreeImageHorizontal = new SimpleImage(this.threeByThreePixelsHorizontal);

    this.oneByOnePixel = new SimpleImage(new Color[][]{{new Color(40, 100, 42)}});
    this.processor = new SimpleImageProcessor();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetImageStateException1() {
    this.processor.getImageState("3x3 Pixels");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetImageStateException2() {
    this.processor.getImageState("");
  }

  @Test
  public void testGetImageStateAndLoadImage() {
    this.processor.loadImage("3x3 Pixels", this.threeByThreeImage);
    assertEquals(this.threeByThreeImage, this.processor.getImageState("3x3 Pixels"));
    this.processor.loadImage("1x1 Pixels", this.oneByOnePixel);
    assertEquals(this.oneByOnePixel, this.processor.getImageState("1x1 Pixels"));
    ImageModel newOneByOnePixel = new SimpleImage(new Color[][]{{ new Color(255, 255, 0)}});
    this.processor.loadImage("1x1 Pixels", newOneByOnePixel);
    assertEquals(newOneByOnePixel, this.processor.getImageState("1x1 Pixels"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoStoringNullImages() {
    this.processor.loadImage("test", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testProcessImageException1() {
    this.processor.processImage("3x3 Pixels", "3x3 Pixels Brightened",
            new BrightenChannels(10));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testProcessImageException2() {
    this.processor.loadImage("3x3 Pixels", this.threeByThreeImage);
    this.processor.processImage("3x3 Pixels", "3x3 Pixels Brightened",null);
  }

  @Test
  public void testGetImageStateAndProcessImage() {
    this.processor.loadImage("3x3 Pixels", this.threeByThreeImage);
    this.processor.processImage("3x3 Pixels", "3x3 Pixels Brightened",
            new BrightenChannels(10));
    assertEquals(this.threeByThreeImageBrightened,
            this.processor.getImageState("3x3 Pixels Brightened"));
    this.processor.processImage("3x3 Pixels", "3x3 Pixels Brightened",
            new BrightenChannels(32));
    assertEquals(this.threeByThreeImageStrongBrightened,
            this.processor.getImageState("3x3 Pixels Brightened"));
    this.processor.processImage("3x3 Pixels", "3x3 Pixels Flipped Horizontally",
            new FlipHorizontally());
    assertEquals(this.threeByThreeImageHorizontal,
            this.processor.getImageState("3x3 Pixels Flipped Horizontally"));
    this.processor.processImage("3x3 Pixels Flipped Horizontally",
            "3x3 Pixels Twice Flipped Horizontally", new FlipHorizontally());
    assertEquals(this.threeByThreeImage,
            this.processor.getImageState("3x3 Pixels Twice Flipped Horizontally"));
  }
}
