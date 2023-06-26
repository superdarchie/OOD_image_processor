package view;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.ControllerFeatureSet;
import model.image.ImageModel;
import util.ImageUtil;
import util.Util;

/**
 * This class represents a GUI view for an image processing program that is implemented using Java
 * Swing.
 */
public class SwingGuiView extends JFrame implements ImageProcessorView {

  private final static String IMG_NAME = "current-image";
  private ControllerFeatureSet actionObject;

  // An image label to display the currently selected image.
  private final JLabel imageLabel;
  private HistogramPanel redHistogramPanel;
  private HistogramPanel greenHistogramPanel;
  private HistogramPanel blueHistogramPanel;
  private HistogramPanel intensityHistogramPanel;
  private final JLabel loadFileDisplay;
  private final JLabel saveFileDisplay;
  private final JSpinner brightenIncrement;
  private final JTextField downscaleWidth;
  private final JTextField downscaleHeight;

  /**
   * Create a new GUI view for the given state of a marble solitaire game.
   */
  public SwingGuiView() {

    super("Image Processing Program");

    this.setLayout(new BorderLayout());
    this.setSize(1600, 1200);

    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // Add the image view to the center of the view.
    imageLabel = new JLabel();
    JScrollPane imageScrollPane = new JScrollPane(imageLabel);
    imageScrollPane.setPreferredSize(new Dimension(400, 400));
    this.add(imageScrollPane, BorderLayout.CENTER);

    // Set up histogram panels.
    this.redHistogramPanel = new HistogramPanel(Color.RED);
    this.greenHistogramPanel = new HistogramPanel(Color.GREEN);
    this.blueHistogramPanel = new HistogramPanel(Color.BLUE);
    this.intensityHistogramPanel = new HistogramPanel(Color.DARK_GRAY);
    JScrollPane redHistogramScrollable = new JScrollPane(this.redHistogramPanel);
    redHistogramScrollable.setPreferredSize(new Dimension(300, 150));
    redHistogramScrollable.setBorder(new TitledBorder("Red histogram"));
    JScrollPane greenHistogramScrollable = new JScrollPane(this.greenHistogramPanel);
    greenHistogramScrollable.setPreferredSize(new Dimension(300, 150));
    greenHistogramScrollable.setBorder(new TitledBorder("Green histogram"));
    JScrollPane blueHistogramScrollable = new JScrollPane(this.blueHistogramPanel);
    blueHistogramScrollable.setPreferredSize(new Dimension(300, 150));
    blueHistogramScrollable.setBorder(new TitledBorder("Blue histogram"));
    JScrollPane intensityHistogramScrollable = new JScrollPane(this.intensityHistogramPanel);
    intensityHistogramScrollable.setPreferredSize(new Dimension(300, 150));
    intensityHistogramScrollable.setBorder(new TitledBorder("Intensity histogram"));

    // Add all histograms to a section at the bottom.
    JPanel histograms = new JPanel();
    histograms.setLayout(new FlowLayout());
    this.add(histograms, BorderLayout.SOUTH);
    histograms.setBorder(BorderFactory.createTitledBorder("Histograms"));
    histograms.add(redHistogramScrollable);
    histograms.add(greenHistogramScrollable);
    histograms.add(blueHistogramScrollable);
    histograms.add(intensityHistogramScrollable);

    // Create a section for all the GUI program commands on the right side.
    JPanel processingActions = new JPanel();
    processingActions.setBorder(BorderFactory.createTitledBorder("Commands"));
    processingActions.setLayout(new GridLayout(15, 0));
    this.add(processingActions, BorderLayout.EAST);

    // Add the file loading button.
    JButton loadBtn = new JButton("Load");
    this.loadFileDisplay = new JLabel("File path will appear here");
    loadBtn.addActionListener(actionEvent -> requestAction("load"));
    JPanel loadSection = new JPanel();
    loadSection.setLayout(new FlowLayout());
    loadSection.add(loadBtn);
    loadSection.add(this.loadFileDisplay);
    processingActions.add(loadSection);

    // Add the file saving button.
    JButton saveBtn = new JButton("Save");
    this.saveFileDisplay = new JLabel("File path will appear here");
    saveBtn.addActionListener(actionEvent -> requestAction("save"));
    JPanel saveSection = new JPanel();
    saveSection.setLayout(new FlowLayout());
    saveSection.add(saveBtn);
    saveSection.add(this.saveFileDisplay);
    processingActions.add(saveSection);

    // Add all the basic command buttons with as little boilerplate as possible.
    Map<String, String> basicCommandButtons = new LinkedHashMap<>();
    basicCommandButtons.put("Flip Horizontally", "horizontal-flip");
    basicCommandButtons.put("Flip Vertically", "vertical-flip");
    basicCommandButtons.put("Red component", "red-component");
    basicCommandButtons.put("Green component", "green-component");
    basicCommandButtons.put("Blue component", "blue-component");
    basicCommandButtons.put("Greyscale component", "luma-component");
    basicCommandButtons.put("Value component", "value-component");
    basicCommandButtons.put("Intensity component", "intensity-component");
    basicCommandButtons.put("Gaussian Blur", "gaussian-blur");
    basicCommandButtons.put("Sharpen", "sharpen");
    basicCommandButtons.put("Sepia Tone", "sepia-tone");
    for (Map.Entry<String, String> entry : basicCommandButtons.entrySet()) {
      JButton btn = new JButton(entry.getKey());
      btn.addActionListener(actionEvent -> requestAction(entry.getValue()));
      processingActions.add(btn);
    }

    // Add the button for brighten, which gets special treatment because it needs a spinner for
    // brighten increment input.
    JButton brightenBtn = new JButton("Brighten");
    JLabel brightenLabel = new JLabel("Brighten increment:");
    this.brightenIncrement = new JSpinner(new SpinnerNumberModel(0, -255, 255, 1));
    brightenBtn.addActionListener(actionEvent -> requestAction("brighten"));
    JPanel brightenSection = new JPanel();
    brightenSection.setLayout(new FlowLayout());
    brightenSection.add(brightenBtn);
    brightenSection.add(brightenLabel);
    brightenSection.add(this.brightenIncrement);
    processingActions.add(brightenSection);

    // Add the button for downsizing, which takes in the width and height for resizing
    JButton downscaleBtn = new JButton("Downscale");
    JLabel downscaleWidthLabel = new JLabel("Width:");
    this.downscaleWidth = new JTextField();
    this.downscaleWidth.setPreferredSize(new Dimension(40, 20));
    JLabel downscaleHeightLabel = new JLabel("Height");
    this.downscaleHeight = new JTextField();
    this.downscaleHeight.setPreferredSize(new Dimension(40, 20));
    downscaleBtn.addActionListener(actionEvent -> requestAction("downscale"));
    JPanel downsizeSection = new JPanel();
    downsizeSection.add(downscaleBtn);
    downsizeSection.add(downscaleWidthLabel);
    downsizeSection.add(this.downscaleWidth);
    downsizeSection.add(downscaleHeightLabel);
    downsizeSection.add(this.downscaleHeight);
    processingActions.add(downsizeSection);

    pack();
    this.repaint();
  }

  @Override
  public void displayImage(ImageModel img) {
    Util.requireNonNullArg(img);
    this.imageLabel.setIcon(new ImageIcon(
            ImageUtil.imageToBufferedImage(img)));
    this.redHistogramPanel.setHistogram(
            ImageUtil.imageToHistogram(img, model.color.Color::red));
    this.greenHistogramPanel.setHistogram(
            ImageUtil.imageToHistogram(img, model.color.Color::green));
    this.blueHistogramPanel.setHistogram(
            ImageUtil.imageToHistogram(img, model.color.Color::blue));
    this.intensityHistogramPanel.setHistogram(ImageUtil.imageToHistogram(img,
        (model.color.Color c) -> (c.red() + c.green() + c.blue()) / 3));
    this.repaint();
  }

  @Override
  public void setActionObject(ControllerFeatureSet actionObject) {
    this.actionObject = Util.requireNonNullArg(actionObject);
    this.setVisible(true);
  }

  /**
   * Request that the controller feature object perform a particular action based on the input
   * string.
   * Separates controller and view responsibility by passing the GUI action to the controller
   * for respective handling in its command pattern.
   * @param action load, save, brighten, or any generic image processing command
   */
  private void requestAction(String action) {
    if (this.actionObject == null) {
      return;
    }
    try {
      switch (action) {
        case "load":
          JFileChooser fChooser = new JFileChooser(".");
          FileNameExtensionFilter filter = new FileNameExtensionFilter(
                  "PPM, BMP, JPG, and PNG Images", "ppm", "bmp", "jpg", "png");
          fChooser.setFileFilter(filter);
          int retVal = fChooser.showOpenDialog(this);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            File f = fChooser.getSelectedFile();
            this.actionObject.loadFile(f.getAbsolutePath(), IMG_NAME);
            this.loadFileDisplay.setText(f.getName());
          }
          break;
        case "save":
          fChooser = new JFileChooser(".");
          retVal = fChooser.showSaveDialog(this);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            File f = fChooser.getSelectedFile();
            this.actionObject.saveImage(IMG_NAME, f.getAbsolutePath());
            this.saveFileDisplay.setText(f.getName());
          }
          break;
        case "brighten":
          String increment = Integer.toString((Integer) this.brightenIncrement.getValue());
          this.actionObject.runProcessingCommand(action, IMG_NAME, IMG_NAME,
                                                 new Scanner(increment));
          break;
        case "downscale":
          this.actionObject.runProcessingCommand(action, IMG_NAME, IMG_NAME,
                  new Scanner(this.downscaleWidth.getText() + " " + this.downscaleHeight.getText())); // todo good?
          break;
        default:
          this.actionObject.runProcessingCommand(action, IMG_NAME, IMG_NAME, null);
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      JOptionPane.showMessageDialog(null, e.getMessage());
    }
  }
}

