import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Optional;

import controller.ImageProcessorController;
import controller.ScriptedImageProcessorController;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

/**
 * Test class for a scripted controller.
 * Tests that scripted controllers fail on exceptions,
 * as the script should be modified and run again.
 * Tests running a provided test script to modify an image.
 */
public class ScriptedControllerTest extends AbstractControllerTest {
  @Override
  protected ImageProcessorController makeController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> dest
  ) {
    return new ScriptedImageProcessorController(processor, input, dest);
  }

  @Test
  public void testErrorThrows() {
    ImageProcessor processor = new SimpleImageProcessor();

    String commands = "load " + assetDir + "test_img.ppm img\n" +
            "brighten img img-bright\n" +
            "undefined-command img img-new\n" +
            "blah blah blah\n" +
            "quit";

    ImageProcessorController controller = new ScriptedImageProcessorController(
            processor,
            new StringReader(commands),
            Optional.of(new ByteArrayOutputStream())
    );
    assertThrows(IllegalStateException.class, controller::run);
  }

  @Test
  public void testRunFromScript() {
    ImageProcessor processor = new SimpleImageProcessor();

    try {
      Readable scriptReader = new FileReader("" + assetDir + "test_script");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageProcessorController controller = this.makeController(
          processor,
          scriptReader,
          Optional.of(out)
      );
      controller.run();
      assertEquals(testImgModified, processor.getImageState("bright-flip"));
      assertEquals(testImgModifiedString, out.toString());
    } catch (FileNotFoundException e) {
      fail();
    }
  }

}
