import org.junit.Before;
import org.junit.Test;

import model.color.Color;
import model.color.IColor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests all the functionality for the IColor interface through its implementation in the Color
 * class, revolving around the 3 RGB channels and proper representations and computations
 * for pixels.
 */
public class ColorTest {
  Color black;
  Color white;
  Color brown;
  Color purple;

  @Before
  public void setUp() {
    this.black = new Color(0, 0, 0);
    this.white = new Color(255, 255, 255);
    this.brown = new Color(102, 51, 0);
    this.purple = new Color(76, 0, 153);
  }

  @Test
  public void testRed() {
    assertEquals(0, this.black.red());
    assertEquals(255, this.white.red());
    assertEquals(102, this.brown.red());

    Color clampedBlack = new Color(-1, 0, 0);
    assertEquals(0, clampedBlack.red());

    Color clampedWhite = new Color(256, 255, 255);
    assertEquals(255, clampedWhite.red());
  }

  @Test
  public void testGreen() {
    assertEquals(0, this.black.green());
    assertEquals(255, this.white.green());
    assertEquals(51, this.brown.green());

    Color clampedBlack = new Color(0, -10, 0);
    assertEquals(0, clampedBlack.green());

    Color clampedWhite = new Color(255, 270, 255);
    assertEquals(255, clampedWhite.green());
  }

  @Test
  public void testBlue() {
    assertEquals(0, this.black.blue());
    assertEquals(255, this.white.blue());
    assertEquals(153, this.purple.blue());

    Color clampedBlack = new Color(0, 0, -40);
    assertEquals(0, clampedBlack.blue());

    Color clampedWhite = new Color(255, 255, 300);
    assertEquals(255, clampedWhite.blue());
  }

  @Test
  public void testGetChannel() {
    assertEquals(102, this.brown.getChannel(IColor.Channel.Red));
    assertEquals(51, this.brown.getChannel(IColor.Channel.Green));
    assertEquals(0, this.brown.getChannel(IColor.Channel.Blue));
    assertEquals(76, this.purple.getChannel(IColor.Channel.Red));
    assertEquals(0, this.purple.getChannel(IColor.Channel.Green));
    assertEquals(153, this.purple.getChannel(IColor.Channel.Blue));
  }

  @Test
  public void testAdd() {
    Color purpleAndBrown = this.brown.add(this.purple);
    assertEquals(178, purpleAndBrown.red());
    assertEquals(51, purpleAndBrown.green());
    assertEquals(153, purpleAndBrown.blue());

    Color purpleAndWhite = this.purple.add(this.white);
    assertEquals(255, purpleAndWhite.red());
    assertEquals(255, purpleAndWhite.green());
    assertEquals(255, purpleAndWhite.blue());

    Color purpleDarkened = this.purple.add(-50, -50, -50);
    assertEquals(26, purpleDarkened.red());
    assertEquals(0, purpleDarkened.green());
    assertEquals(103, purpleDarkened.blue());

    Color brownBrightened = this.brown.add(160, 160, 160);
    assertEquals(255, brownBrightened.red());
    assertEquals(211, brownBrightened.green());
    assertEquals(160, brownBrightened.blue());
  }

  @Test
  public void testEquals() {
    assertFalse(this.brown.equals(this.purple));
    assertFalse(this.white.equals(this.black));
    assertFalse(this.black.equals(new Color[][]{{new Color(102, 51, 0)}}));

    Color brown2 = new Color(102, 51, 0);
    assertTrue(this.white.equals(this.white));
    assertTrue(this.brown.equals(brown2));
  }

}

