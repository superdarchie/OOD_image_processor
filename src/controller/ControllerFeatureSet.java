package controller;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Represents the functionality that a controller advertises to the view to allow interaction with
 * the command system and loading/saving images to the processor.
 */
public interface ControllerFeatureSet {

  /**
   * Read in an image from the filesystem and save to the processor.
   * @param path the path to load the file from
   * @param saveName the name to save the image with internally
   * @throws IllegalStateException if the file does not exist
   */
  void loadFile(String path, String saveName) throws IllegalStateException;

  /**
   * Write a stored image from the processor out to the filesystem.
   * @param name the name of the image to save
   * @param path the file path to save to
   * @throws IllegalStateException if the file cannot be created
   */
  void saveImage(String name, String path) throws IllegalStateException;

  /**
   * Runs the supplied image processing command on the image with the given name in the stored
   * processor and save the output to an image with the save name. Uses a scanner if additional
   * command arguments are required.
   * @param cmd the command to run
   * @param name the name of the image to process
   * @param saveName the name to save the resultant image to
   * @param sc the scanner for extra arguments
   * @throws IllegalStateException if the command is invalid
   * @throws NoSuchElementException if more arguments are required but not accessible by the scanner
   */
  void runProcessingCommand(String cmd, String name, String saveName, Scanner sc)
      throws IllegalStateException, NoSuchElementException;

}

