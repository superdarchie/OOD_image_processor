package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

import controller.commands.BrightenChannels;
import controller.commands.CommandImpls;
import controller.commands.Downscale;
import controller.commands.FlipHorizontally;
import controller.commands.FlipVertically;
import controller.commands.ImageProcessingCommand;
import controller.commands.MaskedCommand;
import model.color.Color;
import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import util.ImageUtil;
import util.Util;

/**
 * Represents the image saving, loading, and processing functionality common to all controllers.
 * Supports the horizontal flip, vertical flip, red component, green component, blue component,
 * value component, intensity component, luma component, brighten, gaussian blur, sharpen, and
 * sepia tone commands.
 */
public class ControllerFeatures implements ControllerFeatureSet {

  protected final ImageProcessor processor;
  protected final Map<String, Function<Scanner, ImageProcessingCommand>> knownCommands;

  /**
   * Create a new instance of controller features with a processor delegate to load images to,
   * save images from, and run commands via.
   * @param processor the processor delegate
   * @throws IllegalArgumentException if the processor is null
   */
  public ControllerFeatures(ImageProcessor processor) throws IllegalArgumentException {
    this.processor = Util.requireNonNullArg(processor);

    this.knownCommands = new HashMap<>();
    // Add all the simple image processing commands that this controller is capable of.
    this.knownCommands.put("horizontal-flip", (Scanner sc) -> new FlipHorizontally());
    this.knownCommands.put("vertical-flip", (Scanner sc) -> new FlipVertically());
    this.knownCommands.put("red-component", (Scanner sc) -> CommandImpls.RED_CHANNEL);
    this.knownCommands.put("green-component", (Scanner sc) -> CommandImpls.GREEN_CHANNEL);
    this.knownCommands.put("blue-component", (Scanner sc) -> CommandImpls.BLUE_CHANNEL);
    this.knownCommands.put("value-component", (Scanner sc) -> CommandImpls.VALUE);
    this.knownCommands.put("intensity-component", (Scanner sc) -> CommandImpls.INTENSITY);
    this.knownCommands.put("luma-component", (Scanner sc) -> CommandImpls.LUMA);
    this.knownCommands.put("brighten",
        (Scanner sc) -> new BrightenChannels(Util.requireNonNullArg(sc).nextInt()));
    this.knownCommands.put("gaussian-blur", (Scanner sc) -> CommandImpls.GAUSSIAN_BLUR);
    this.knownCommands.put("sharpen", (Scanner sc) -> CommandImpls.SHARPEN);
    this.knownCommands.put("sepia-tone", (Scanner sc) -> CommandImpls.SEPIA_TONE);
  }

  @Override
  public void loadFile(String path, String saveName) throws IllegalStateException {
    try {
      // Read in the image.
      Color[][] pixels = ImageUtil.readImage(path, new FileInputStream(path));
      ImageModel img = new SimpleImage(pixels);
      // Send image to processor.
      this.processor.loadImage(saveName, img);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("Could not find specified file to read from.");
    }
  }

  @Override
  public void saveImage(String name, String path) throws IllegalStateException {
    ImageModel img = this.processor.getImageState(name);
    try {
      OutputStream dest = new FileOutputStream(path);
      ImageUtil.writeImage(img, path, dest);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("Could not find specified file to output to.");
    }
  }

  @Override
  public void runProcessingCommand(String cmd, String name, String saveName, Scanner sc)
          throws IllegalStateException, NoSuchElementException {
    ImageProcessingCommand command = this.createProcessingCommand(cmd, sc);
    // Run command on the processor.
    this.processor.processImage(name, saveName, command);
  }

  protected ImageProcessingCommand createProcessingCommand(
          String cmd, Scanner sc
  ) throws IllegalStateException {
    Function<Scanner, ImageProcessingCommand> cmdGenerator = this.knownCommands.get(cmd);
    if (cmdGenerator == null) {
      throw new IllegalStateException(String.format("Command '%s' not known.", cmd));
    }

    ImageProcessingCommand processingCommand;
    try {
      processingCommand = cmdGenerator.apply(sc);
    } catch (NoSuchElementException e) {
      throw new IllegalStateException(String.format("Bad argument received for %s command.", cmd));
    }

    return processingCommand;
  }

}
