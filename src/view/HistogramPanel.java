package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import util.Util;

/**
 * Represents the implementation for histogram panels which takes in Maps of histogram data and
 * converts it to a GUI representation via a bar chart. The X-axis represents the data value
 * between 0-255 and the Y-axis represents the relative frequency of the value.
 */
public class HistogramPanel extends JPanel {

  private static final int HEIGHT_MULTIPLIER = 1500;
  private Map<Integer, Integer> valueHeights;
  private final Color color;

  /**
   * Initializes a GUI histogram panel with an empty histogram.
   * @param color the color to use to draw the histogram
   * @throws IllegalArgumentException if the given color is null
   */
  public HistogramPanel(Color color) throws IllegalArgumentException {
    super();
    this.color = Util.requireNonNullArg(color);
    this.valueHeights = new HashMap<>();
    this.setPreferredSize(new Dimension(256, 100));
    this.setBackground(Color.WHITE);
  }

  @Override
  public void paintComponent(Graphics g) {
    this.setBackground(Color.WHITE);
    super.paintComponent(g);
    g.setColor(this.color);
    int originX = 0;
    int originY = 0;
    for (Map.Entry<Integer, Integer> entry : this.valueHeights.entrySet()) {
      g.drawRect(originX + entry.getKey(), originY + this.getHeight() - entry.getValue(),
              1, entry.getValue());
    }
  }

  /**
   * Updates the stored histogram hashmap to the supplied one.
   * @param valueOccurrences a hashmap of values and their occurrence counts
   * @throws IllegalArgumentException if the given map is null
   */
  public void setHistogram(Map<Integer, Integer> valueOccurrences) throws IllegalArgumentException {
    Util.requireNonNullArg(valueOccurrences);
    int totalOccurrences = 0;
    int maxValOccurrences = 0;
    for (Integer occur : valueOccurrences.values()) {
      totalOccurrences += occur;
      maxValOccurrences = Math.max(maxValOccurrences, occur);
    }
    int maxHeight = Math.round(HEIGHT_MULTIPLIER * ((float) maxValOccurrences
            / (float) totalOccurrences));
    this.setPreferredSize(new Dimension(256, maxHeight));
    this.invalidate();
    this.valueHeights = new HashMap<>();
    for (Map.Entry<Integer, Integer> entry : valueOccurrences.entrySet()) {
      this.valueHeights.put(entry.getKey(), Math.round(HEIGHT_MULTIPLIER * ((float) entry.getValue()
              / (float) totalOccurrences)));
    }
  }
}
