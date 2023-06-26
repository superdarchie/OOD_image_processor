package util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import javax.imageio.ImageIO;

import model.color.Color;
import model.image.ImageModel;

/**
 * This class contains utility methods to read images from files, convert the images to arrays of
 * colors, and make a generic histogram data representation.
 */
public class ImageUtil {

  /**
   * Read an image into memory as a 2d array of colors from the given filename.
   * Works at least for the png/jpg/bmp/ppm image formats.
   *
   * @param fileName the path to the file to read from
   * @return a 2d color array representing the pixels of the image that was read
   * @throws IllegalStateException if the file is not found, or any other exception occurs
   *                               when reading from the file
   */
  public static Color[][] readImage(String fileName, InputStream input)
          throws IllegalStateException {
    if (fileName.endsWith(".ppm") || fileName.endsWith(".PPM")) {
      return ImageUtil.readPPM(input);
    } else {
      return ImageUtil.readNonPPM(input);
    }
  }

  /**
   * Read an image in the PPM format and store as an array of colors.
   *
   * @param input the input that should be in the PPM format.
   * @return a 2d color array representing the pixels of the image that was read
   * @throws IllegalStateException if the readable input is not a ppm file
   */
  public static Color[][] readPPM(InputStream input) throws IllegalStateException {
    Util.requireNonNullArg(input);
    Scanner sc = new Scanner(input);

    StringBuilder builder = new StringBuilder();
    // Read the file line by line, and populate a string. This will throw away any comment lines.
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    // Now set up the scanner to read from the string we just built.
    sc = new Scanner(builder.toString());

    String token;

    token = sc.next();
    if (!token.equals("P3")) {
      throw new IllegalStateException("Given file is not a PPM file.");
    }
    int width = sc.nextInt();
    int height = sc.nextInt();
    float maxVal = sc.nextInt();

    Color[][] pixels = new Color[height][width];

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        // All values must be corrected in case the max val of the file is less or greater than 255.
        // Conversion here finds the nearest equivalent val in the 8-bit representation that is used
        // throughout this program.
        int r = Math.round(sc.nextInt() * Color.MAX_VALUE / maxVal);
        int g = Math.round(sc.nextInt() * Color.MAX_VALUE / maxVal);
        int b = Math.round(sc.nextInt() * Color.MAX_VALUE / maxVal);
        pixels[i][j] = new Color(r, g, b);
      }
    }

    return pixels;
  }

  /**
   * Read a (non-ppm) image in from the provided input stream and return it as a 2d array of colors.
   * Relies on ImageIO to parse the file and support reading from multiple formats.
   *
   * @param input the stream to read image data from
   * @return a 2d color array representing the pixels of the image that was read
   * @throws IllegalStateException if the file read fails for whatever reason
   */
  public static Color[][] readNonPPM(InputStream input) throws IllegalStateException {
    Util.requireNonNullArg(input);

    try {
      // ImageIO intelligently reads in the image based on the format.
      BufferedImage img = ImageIO.read(input);

      // Make sure ImageIO was able to read the file. Unsupported formats get read in as null.
      if (img == null) {
        throw new IllegalStateException("Unable to read the provided file.");
      }

      // Info about the image that was just loaded into the buffered image.
      int minCol = img.getMinX();
      int minRow = img.getMinY();
      int height = img.getHeight();
      int width = img.getWidth();

      // Construct the array of pixels for an image model representation of the image.
      // Do this by looping over rows and columns in the image and retrieving each color.
      Color[][] res = new Color[height][width];
      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {
          int argb = img.getRGB(minCol + c, minRow + r); // Get color at r,c as an ARGB int.
          // Do nothing with alpha.
          int red = (argb >> 16) & 255;  // Bits 23-16 contain the red channel value.
          int green = (argb >> 8) & 255; // Bits 15-8 contain the green channel value.
          int blue = argb & 255;         // Bits 7-0 contain the blue channel value.
          res[r][c] = new Color(red, green, blue);
        }
      }
      // Return the completed array of pixels.
      return res;
    } catch (IOException e) {
      throw new IllegalStateException("IO exception encountered when reading from specified file.");
    }
  }

  /**
   * Write an image to the given destination with correct formatting based on the file extension.
   * Usually used to write to a file output stream to save images to the filesystem.
   *
   * @param img the internal image representation to send to the given destination
   * @param fileName the name of the file to store to - extension important for formatting info
   * @param dest the destination to write the formatted file data to
   * @throws IllegalStateException if anything goes wrong with the image write
   */
  public static void writeImage(ImageModel img, String fileName, OutputStream dest)
          throws IllegalStateException {
    if (fileName.endsWith(".ppm") || fileName.endsWith(".PPM")) {
      writePPM(img, dest);
    } else {
      writeNonPPM(img, fileName, dest);
    }
  }

  /**
   * Write an image in PPM format to a destination based on the file name.
   *
   * @param img the image to write
   * @param dest the destination to write to
   * @throws IllegalStateException if writing to the writer fails
   */
  public static void writePPM(ImageModel img, OutputStream dest) throws IllegalStateException {

    try {
      String sep = System.lineSeparator();
      int h = img.getHeight();
      int w = img.getWidth();

      // Write out
      dest.write(("P3" + sep).getBytes());
      dest.write((w + " " + h + sep).getBytes());
      dest.write((Color.MAX_VALUE + sep).getBytes());

      for (int r = 0; r < h; r++) {
        for (int c = 0; c < w; c++) {
          Color color = img.colorAt(r,c);
          dest.write((color.red() + sep + color.green() + sep + color.blue()).getBytes());
          // Add newline unless last row and col.
          dest.write(((r < h - 1 || c < w - 1) ? sep : "").getBytes());
          dest.flush();
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Could not write image to destination.");
    }

  }

  /**
   * Write a image in a format other than PPM to the given destination.
   * Uses ImageIO for the ability to write to various image formats.
   *
   * @param img the image to write
   * @param fileName the name of the file - gives the file extension to inform proper formatting
   * @param dest the destination to write to
   * @throws IllegalStateException if writing the image data fails for some reason
   */
  public static void writeNonPPM(ImageModel img, String fileName, OutputStream dest)
          throws IllegalStateException {
    // Everything after the last "." in the filename should be the file extension / format.
    String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
    if (!Arrays.asList(ImageIO.getWriterFormatNames()).contains(formatName)) {
      throw new IllegalStateException("Cannot write out to the format indicated by the file name.");
    }

    // Set up buffered image.
    BufferedImage writeImg = imageToBufferedImage(img);
    // Try to write the buffered image out to the target file in the specified format.
    // ImageIO provides this functionality with a single method.
    try {
      ImageIO.write(writeImg, formatName, dest);
    } catch (IOException e) {
      throw new IllegalStateException("Could not write out to file.");
    }
  }

  /**
   * Returns a BufferedImage representation from our supplied ImageModel, enabling improved
   * efficiency and applications for different image types (ex. "png" or "jpg").
   * @param img the image model or 2D array of pixel Colors
   * @return the buffered image
   * @throws IllegalArgumentException if the supplied image is null
   */
  public static BufferedImage imageToBufferedImage(ImageModel img) throws IllegalArgumentException {
    Util.requireNonNullArg(img);
    int width = img.getWidth();
    int height = img.getHeight();
    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        Color color = img.colorAt(r,c);
        // Bit shift and combine the different channels to calculate the single int representation
        // of the RGB color for this pixel. Channel values guaranteed to be in the range 0-255, so
        // no risk of the bitwise or causing overlap.
        int rgb = color.red() << 16 | color.green() << 8 | color.blue();
        resultImage.setRGB(c, r, rgb);
      }
    }
    return resultImage;
  }

  /**
   * Calculates a data representation for a histogram using a hashmap where the key is the
   * respective 0-255 value and the value is the frequency.
   * @param img the image to calculate a histogram data representation for
   * @param mapping converts a pixel Color into a value for the key
   * @return the data representation histogram
   * @throws IllegalArgumentException if the supplied image or mapping function is null
   */
  public static Map<Integer, Integer> imageToHistogram(
          ImageModel img, Function<Color, Integer> mapping) throws IllegalArgumentException {
    Util.requireNonNullArg(img);
    Util.requireNonNullArg(mapping);

    Map<Integer, Integer> histogram = new HashMap<>();
    for (int row = 0; row < img.getHeight(); row++) {
      for (int col = 0; col < img.getWidth(); col++) {
        int result = mapping.apply(img.colorAt(row, col));
        histogram.put(result, histogram.getOrDefault(result, 0) + 1);
      }
    }
    return histogram;
  }

}

