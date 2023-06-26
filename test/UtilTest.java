import org.junit.Test;

import model.image.ImageModel;
import model.image.SimpleImage;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;
import model.color.Color;
import util.Util;

import static org.junit.Assert.assertTrue;

/**
 * Tests the general functionality of the static Util class used throughout the program.
 */
public class UtilTest {
  @Test(expected = IllegalArgumentException.class)
  public void testRequireNonNullArgException1() {
    Util.requireNonNullArg(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRequireNonNullArgException2() {
    Util.requireNonNullArg(new SimpleImageProcessor().getImageState("Nothing exists yet."));
  }

  @Test
  public void testRequireNonNullArg() {
    ImageModel img = Util.requireNonNullArg(
            new SimpleImage(new Color[][]{{ new Color(0, 0, 0)}}));
    assertTrue(img instanceof SimpleImage);

    ImageProcessor processor = Util.requireNonNullArg(new SimpleImageProcessor());
    assertTrue(processor instanceof SimpleImageProcessor);
  }
}
