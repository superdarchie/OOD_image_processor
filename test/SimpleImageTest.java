import org.junit.Before;
import org.junit.Test;

import model.image.ImageModel;
import model.image.SimpleImage;
import model.color.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests all the functionality between the ImageModelState and ImageModel interface using the
 * SimpleImage class implementation, separated to distinguish viewing methods from mutating
 * methods for images.
 */
public class SimpleImageTest {
  // width x height
  private ImageModel threeByTwoImage;
  private ImageModel threeByFourImage;
  private ImageModel fourBySixImage;

  @Before
  public void setUp() {
    Color[][] threeByTwo = new Color[2][3];
    Color incrementing = new Color(0, 0, 0);
    for (int row = 0; row < threeByTwo.length; row++) {
      for (int col = 0; col < threeByTwo[0].length; col++) {
        threeByTwo[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.threeByTwoImage = new SimpleImage(threeByTwo);

    Color[][] threeByFour = new Color[4][3];
    incrementing = new Color(0, 0, 0);
    for (int row = 0; row < threeByFour.length; row++) {
      for (int col = 0; col < threeByFour[0].length; col++) {
        threeByFour[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.threeByFourImage = new SimpleImage(threeByFour);

    Color[][] fourBySix = new Color[6][4];
    incrementing = new Color(0, 10, 20);
    for (int row = 0; row < fourBySix.length; row++) {
      for (int col = 0; col < fourBySix[0].length; col++) {
        fourBySix[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    this.fourBySixImage = new SimpleImage(fourBySix);
  }

  @Test
  public void testNullPixels() {
    assertThrows(IllegalArgumentException.class, () -> new SimpleImage(null));
  }

  @Test
  public void testInvalidHeight() {
    try {
      ImageModel invalidImage = new SimpleImage(new Color[0][3]);
      fail("Didn't catch an error.");
    } catch (IllegalArgumentException e) {
      assertEquals("Pixel array must contain at least one pixel.", e.getMessage());
    }
  }

  @Test
  public void testInvalidWidth() {
    try {
      ImageModel invalidImage = new SimpleImage(new Color[4][0]);
      fail("Didn't catch an error.");
    } catch (IllegalArgumentException e) {
      assertEquals("Pixel array must contain at least one pixel.", e.getMessage());
    }
  }

  @Test
  public void testGetHeight() {
    assertEquals(2, this.threeByTwoImage.getHeight());
    assertEquals(4, this.threeByFourImage.getHeight());
    assertEquals(6, this.fourBySixImage.getHeight());
  }

  @Test
  public void testGetWidth() {
    assertEquals(3, this.threeByTwoImage.getWidth());
    assertEquals(3, this.threeByFourImage.getWidth());
    assertEquals(4, this.fourBySixImage.getWidth());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testColorAtException1() {
    this.threeByTwoImage.colorAt(-1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testColorAtException2() {
    this.threeByFourImage.colorAt(0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testColorAtException3() {
    this.threeByTwoImage.colorAt(this.threeByTwoImage.getHeight(), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testColorAtException4() {
    this.threeByFourImage.colorAt(0, this.threeByFourImage.getWidth());
  }

  @Test
  public void testColorAt() {
    assertEquals(new Color(10, 10, 10), this.threeByTwoImage.colorAt(0, 1));
    assertEquals(new Color(50, 50, 50), this.threeByTwoImage.colorAt(1, 2));
    assertEquals(new Color(110, 110, 110), this.threeByFourImage.colorAt(3, 2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNewException() {
    this.threeByFourImage.createNew(null);
  }

  @Test
  public void testCreateNew() {
    Color[][] pixels = new Color[4][3];
    for (int row = 0; row < pixels.length; row++) {
      for (int col = 0; col < pixels[0].length; col++) {
        pixels[row][col] = new Color(4, 2, 0);
      }
    }
    assertEquals(new SimpleImage(pixels), this.threeByFourImage.createNew(pixels));
  }

  @Test
  public void testEquals() {
    assertTrue(this.threeByFourImage.equals(this.threeByFourImage));
    assertFalse(this.threeByTwoImage.equals(new Color[2][3]));
    assertFalse(this.threeByFourImage.equals(this.threeByTwoImage));
    assertFalse(this.threeByTwoImage.equals(this.threeByFourImage));

    Color[][] threeByTwo2 = new Color[2][3];
    Color incrementing = new Color(0, 0, 0);
    for (int row = 0; row < threeByTwo2.length; row++) {
      for (int col = 0; col < threeByTwo2[0].length; col++) {
        threeByTwo2[row][col] = incrementing;
        incrementing = incrementing.add(10, 10, 10);
      }
    }
    ImageModel threeByTwoImage2 = new SimpleImage(threeByTwo2);
    assertTrue(this.threeByTwoImage.equals(threeByTwoImage2));
  }
}
