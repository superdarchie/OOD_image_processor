import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import model.image.ImageModel;
import model.image.SimpleImage;
import model.color.Color;
import util.ImageUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests all the functionality in the ImageUtil class which reads and translates a PPM file.
 */
public class ImageUtilTest {
  private ImageModel testImg;
  private String testPPM;
  private Color[][] testPixels;
  private String testPixelsPPM;

  @Before
  public void setUp() {
    testImg = new SimpleImage(new Color[][]{
            {new Color(0,0,0), new Color(127,127,127),
                new Color(255,255,255)},
            {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255)}
    });
    String sep = System.lineSeparator();
    testPPM = "P3" + sep +
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

    // 1 row by 3 columns test pixels
    this.testPixels = new Color[][]{{
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(0, 0, 255)
      }};

    // Expected corresponding PPM
    this.testPixelsPPM = "P3" + sep +
            "3 1" + sep +
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

  }

  @Test
  public void testWritePPM() {
    ByteArrayOutputStream dest = new ByteArrayOutputStream();

    ImageModel img = new SimpleImage(this.testPixels);
    try {
      ImageUtil.writePPM(img, dest);
    } catch (IllegalStateException e) {
      fail();
    }
    assertEquals(this.testPixelsPPM, dest.toString());
  }

  @Test
  public void testWriteNonPPM() {
    // Since pngs are not stored in a human-readable format, for this test we write the png, then
    // read it back in to ensure that at least the program is internally consistent.
    ByteArrayOutputStream dest = new ByteArrayOutputStream();

    ImageModel img = new SimpleImage(this.testPixels);
    try {
      ImageUtil.writeNonPPM(img, "test.png", dest);
    } catch (IllegalStateException e) {
      fail();
    }

    ImageModel resultImg;
    try {
      Color[][] pixels = ImageUtil.readNonPPM(new ByteArrayInputStream(dest.toByteArray()));
      resultImg = new SimpleImage(pixels);
      assertEquals(img, resultImg);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test
  public void testLosslessIO() {
    for (String format : new String[]{"ppm", "png", "bmp"}) {
      ByteArrayOutputStream dest = new ByteArrayOutputStream();

      String mockName = "test." + format;
      ImageModel img = new SimpleImage(this.testPixels);
      try {
        ImageUtil.writeImage(img, mockName, dest);
        Color[][] pixels
                = ImageUtil.readImage(mockName, new ByteArrayInputStream(dest.toByteArray()));
        ImageModel resultImg = new SimpleImage(pixels);
        assertEquals(img, resultImg);
      } catch (IllegalStateException e) {
        fail(e.getMessage());
      }
    }
  }

  @Test public void testLossyIO() {
    for (String format : new String[]{"jpg"}) {
      ByteArrayOutputStream dest = new ByteArrayOutputStream();

      String mockName = "test." + format;
      ImageModel img = new SimpleImage(this.testPixels);
      try {
        ImageUtil.writeImage(img, mockName, dest);
        Color[][] pixels
                = ImageUtil.readImage(mockName, new ByteArrayInputStream(dest.toByteArray()));
        ImageModel resultImg = new SimpleImage(pixels);

        // Assert the images are at least the same size.
        assertEquals(resultImg.getHeight(), img.getHeight());
        assertEquals(resultImg.getWidth(), img.getWidth());
        // Go through and make sure all the colors have not been changed too much even with lossy
        // storage.
        int diffTolerance = 200;
        for (int r = 0; r < resultImg.getHeight(); r++) {
          for (int c = 0; c < resultImg.getWidth(); c++) {
            Color og = img.colorAt(r,c);
            Color mod = resultImg.colorAt(r,c);
            int diff = (int) Math.sqrt(Math.pow(og.red() - mod.red(), 2)
                    + Math.pow(og.green() - mod.green(), 2)
                    + Math.pow(og.blue() - mod.blue(), 2));
            assertTrue(diff < diffTolerance);
          }
        }
      } catch (IllegalStateException e) {
        fail(e.getMessage());
      }
    }
  }

  @Test
  public void testWritePpmToFile() {
    ImageModel img = new SimpleImage(this.testPixels);
    try {
      ImageUtil.writePPM(img, new FileOutputStream("test/assets/test_output.ppm"));
    } catch (FileNotFoundException | IllegalStateException e) {
      fail();
    }
  }

  @Test
  public void testWriteToFile() {
    ImageModel img = new SimpleImage(this.testPixels);
    for (String format : new String[]{"ppm", "png", "bmp", "jpg"}) {
      try {
        String fileName = "test/assets/test_output." + format;
        ImageUtil.writeImage(img, fileName, new FileOutputStream(fileName));
      } catch (FileNotFoundException | IllegalStateException e) {
        fail();
      }
    }
  }

  @Test
  public void testReadPPM() {
    Color[][] pixels = ImageUtil.readPPM(new ByteArrayInputStream(this.testPPM.getBytes()));
    for (int r = 0; r < this.testImg.getHeight(); r++) {
      for (int c = 0; c < this.testImg.getWidth(); c++) {
        assertEquals(this.testImg.colorAt(r,c), pixels[r][c]);
      }
    }
  }

  @Test
  public void testReadNonPPM() {
    try {
      Color[][] pixels = ImageUtil.readNonPPM(
              new FileInputStream("test/assets/test_img.png"));
      for (int r = 0; r < this.testImg.getHeight(); r++) {
        for (int c = 0; c < this.testImg.getWidth(); c++) {
          assertEquals(this.testImg.colorAt(r, c), pixels[r][c]);
        }
      }
    } catch (FileNotFoundException e) {
      fail();
    }
  }

  @Test
  public void testPPMMaxValueCorrection() {
    String twoByOneMax15PPM = "P3\n" +
            "2 1\n" +
            "15\n" +
            "15\n" +
            "4\n" +
            "1\n" +
            "0\n" +
            "9\n" +
            "10";
    Color[][] pixelsMax15
            = ImageUtil.readPPM(new ByteArrayInputStream(twoByOneMax15PPM.getBytes()));
    Color[][] expectedPixelsMax15 = new Color[][]{
            {new Color(255, 68, 17), new Color(0, 153, 170)}};
    assertEquals(expectedPixelsMax15[0][0], pixelsMax15[0][0]);
    assertEquals(expectedPixelsMax15[0][1], pixelsMax15[0][1]);

    String oneByTwoMax511PPM = "P3\n" +
            "1 2\n" +
            "511\n" +
            "509\n" +
            "300\n" +
            "0\n" +
            "9\n" +
            "142\n" +
            "409";
    Color[][] pixelsMax511
            = ImageUtil.readPPM(new ByteArrayInputStream(oneByTwoMax511PPM.getBytes()));
    Color[][] expectedPixelsMax511 = new Color[][]{
            {new Color(254, 150, 0)}, {new Color(4, 71, 204)}};
    assertEquals(expectedPixelsMax511[0][0], pixelsMax511[0][0]);
    assertEquals(expectedPixelsMax511[1][0], pixelsMax511[1][0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testImageToBufferedImageException() {
    ImageUtil.imageToBufferedImage(null);
  }

  @Test
  public void testImageToBufferedImage() {
    // Test by reading in a known image on disk as a buffered image, creating the same image with
    // our internal image repr. and then convert the internal repr. and compare pixels on the two
    // buffered images.
    try {
      BufferedImage expectedBuffImg = ImageIO.read(new FileInputStream("test/assets/test_img.png"));
      BufferedImage actualBuffImg = ImageUtil.imageToBufferedImage(this.testImg);
      for (int row = 0; row < actualBuffImg.getHeight(); row++) {
        for (int col = 0; col < actualBuffImg.getWidth(); col++) {
          assertEquals(expectedBuffImg.getRGB(col, row), actualBuffImg.getRGB(col, row));
        }
      }
    } catch (IOException e) {
      fail();
    }
  }

  @Test
  public void testImageToHistogram() {
    Map<Integer, Integer> expectedRGBHistogram = new HashMap<>();
    expectedRGBHistogram.put(0, 3);
    expectedRGBHistogram.put(127, 1);
    expectedRGBHistogram.put(255, 2);
    assertEquals(expectedRGBHistogram, ImageUtil.imageToHistogram(this.testImg, Color::red));
    assertEquals(expectedRGBHistogram, ImageUtil.imageToHistogram(this.testImg, Color::green));
    assertEquals(expectedRGBHistogram, ImageUtil.imageToHistogram(this.testImg, Color::blue));

    Map<Integer, Integer> expectedIntensityHistogram = new HashMap<>();
    expectedIntensityHistogram.put(0, 1);
    expectedIntensityHistogram.put(127, 1);
    expectedIntensityHistogram.put(255, 1);
    expectedIntensityHistogram.put(85, 3);
    assertEquals(expectedIntensityHistogram, ImageUtil.imageToHistogram(this.testImg,
        (Color c) -> (c.red() + c.green() + c.blue()) / 3));

    Color incrementing = new Color(0, 0, 0);
    Color[][] allPixels = new Color[256][256];
    for (int row = 0; row < allPixels.length; row++) {
      for (int col = 0; col < allPixels[0].length; col++) {
        allPixels[row][col] = incrementing;
        incrementing = incrementing.add(1, 1, 1);
      }
      incrementing = new Color(0, 0, 0);
    }
    ImageModel allPixelsImg = new SimpleImage(allPixels);

    Map<Integer, Integer> expectedHistogramAllPixels = new HashMap<>();
    for (int i = 0; i < 256; i++) {
      expectedHistogramAllPixels.put(i, 256);
    }
    assertEquals(expectedHistogramAllPixels, ImageUtil.imageToHistogram(allPixelsImg, Color::red));
    assertEquals(expectedHistogramAllPixels,
                 ImageUtil.imageToHistogram(allPixelsImg, Color::green));
    assertEquals(expectedHistogramAllPixels, ImageUtil.imageToHistogram(allPixelsImg, Color::blue));
    assertEquals(expectedHistogramAllPixels, ImageUtil.imageToHistogram(allPixelsImg,
        (Color c) -> (c.red() + c.green() + c.blue()) / 3));

    ImageModel uniqueRGB = new SimpleImage(new Color[][]{{
            new Color(43, 117, 3),
            new Color(43, 5, 206),
            new Color(0, 117, 206)
      }});

    Map<Integer, Integer> expectedRedHistogram = new HashMap<>();
    expectedRedHistogram.put(43, 2);
    expectedRedHistogram.put(0, 1);
    assertEquals(expectedRedHistogram, ImageUtil.imageToHistogram(uniqueRGB, Color::red));

    Map<Integer, Integer> expectedGreenHistogram = new HashMap<>();
    expectedGreenHistogram.put(117, 2);
    expectedGreenHistogram.put(5, 1);
    assertEquals(expectedGreenHistogram, ImageUtil.imageToHistogram(uniqueRGB, Color::green));

    Map<Integer, Integer> expectedBlueHistogram = new HashMap<>();
    expectedBlueHistogram.put(206, 2);
    expectedBlueHistogram.put(3, 1);
    assertEquals(expectedBlueHistogram, ImageUtil.imageToHistogram(uniqueRGB, Color::blue));

    Map<Integer, Integer> expectedIntensityHistogram2 = new HashMap<>();
    expectedIntensityHistogram2.put(54, 1);
    expectedIntensityHistogram2.put(84, 1);
    expectedIntensityHistogram2.put(107, 1);
    assertEquals(expectedIntensityHistogram2, ImageUtil.imageToHistogram(uniqueRGB,
        (Color c) -> (c.red() + c.green() + c.blue()) / 3));
  }
}
