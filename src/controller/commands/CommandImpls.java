package controller.commands;

import model.color.Color;

/**
 * Utility class representing implementations for different function-object commands to save
 * space/prevent cluttering in the controller.
 */
public class CommandImpls {

  /**
   * Gaussian blur is an image filtering operation that blurs the image by re-calculating the color
   * of each pixel by applying a specific kernel to cause a blur effect.
   */
  public static final ImageProcessingCommand GAUSSIAN_BLUR = new Filter(new float[][]{
          {0.0625f, 0.125f, 0.0625f},
          {0.125f, 0.25f, 0.125f},
          {0.0625f, 0.125f, 0.0625f}
  });

  /**
   * Sharpen is an image filtering operation that accentuates the edges/boundaries between regions
   * of high contrast.
   */
  public static final ImageProcessingCommand SHARPEN = new Filter(new float[][]{
          {-0.125f, -0.125f, -0.125f, -0.125f, -0.125f},
          {-0.125f, 0.25f, 0.25f, 0.25f, -0.125f},
          {-0.125f, 0.25f, 1, 0.25f, -0.125f},
          {-0.125f, 0.25f, 0.25f, 0.25f, -0.125f},
          {-0.125f, -0.125f, -0.125f, -0.125f, -0.125f},
  });

  /**
   * The sepia tone command is a color transformation that converts all colors in an image to give
   * it a reddish-brownish tone, like in old photographs.
   */
  public static final ImageProcessingCommand SEPIA_TONE = new ColorTransformation(new float[][]{
          {0.393f, 0.769f, 0.189f},
          {0.349f, 0.686f, 0.168f},
          {0.272f, 0.534f, 0.131f}
  });

  /**
   * The luma command is another name for greyscale. It is a color transformation that applies the
   * same to all channels (resulting in a greyscale image), and keeps the perceived brightness of
   * pixels the same when doing this operation.
   */
  public static final ImageProcessingCommand LUMA = new ColorTransformation(new float[][]{
          {0.2126f, 0.7152f, 0.0722f},
          {0.2126f, 0.7152f, 0.0722f},
          {0.2126f, 0.7152f, 0.0722f}
  });

  /**
   * Command to set all channels the same as the red channel for all pixels in an image.
   */
  public static final ImageProcessingCommand RED_CHANNEL = new ColorTransformation(new float[][]{
          {1, 0, 0},
          {1, 0, 0},
          {1, 0, 0}});

  /**
   * Command to set all channels the same as the green channel for all pixels in an image.
   */
  public static final ImageProcessingCommand GREEN_CHANNEL = new ColorTransformation(new float[][]{
          {0, 1, 0},
          {0, 1, 0},
          {0, 1, 0},
  });

  /**
   * Command to set all channels the same as the blue channel for all pixels in an image.
   */
  public static final ImageProcessingCommand BLUE_CHANNEL = new ColorTransformation(new float[][]{
          {0, 0, 1},
          {0, 0, 1},
          {0, 0, 1},
  });

  /**
   * Command to set all channels the same as the maximum for the pixel, for all pixels in an image.
   */
  public static final ImageProcessingCommand VALUE = new SetAllChannels(
      (Color color) -> Math.max(color.red(), Math.max(color.green(), color.blue()))
  );

  /**
   * Command to set all channels to the intensity of the pixel (calculated as the average value of
   * the three channels), for all pixels in an image.
   */
  public static final ImageProcessingCommand INTENSITY = new SetAllChannels(
      (Color color) -> (color.red() + color.green() + color.blue()) / 3
  );
}
