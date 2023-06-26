import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Optional;

import controller.ImageProcessorController;
import controller.InteractiveImageProcessorController;
import model.processor.ImageProcessor;
import model.processor.SimpleImageProcessor;

import static org.junit.Assert.assertEquals;

/**
 * Tests the functionality in the InteractiveImageProcessorController by checking
 * invalid input with custom commands.
 */
public class InteractiveControllerTest extends AbstractControllerTest {
  @Override
  protected ImageProcessorController makeController(
      ImageProcessor processor,
      Readable input,
      Optional<OutputStream> dest
  ) {
    return new InteractiveImageProcessorController(processor, input, dest, new StringBuilder());
  }

  @Test
  public void testErrorRecovery() {
    ImageProcessor processor = new SimpleImageProcessor();

    String commands = "blargh\n" +
            "load " + assetDir + "test_img.ppm img\n" +
            "brighten img img-bright\n" +
            "undefined-command img img-new\n" +
            "blah blah blah\n" +
            "quit";

    StringBuilder responseLog = new StringBuilder();
    ImageProcessorController controller = new InteractiveImageProcessorController(
        processor,
        new StringReader(commands),
        Optional.of(new ByteArrayOutputStream()),
        responseLog
    );
    controller.run();
    assertEquals("Encountered exception: Command 'blargh' has insufficient arguments.\n" +
        "Try re-entering the command?\n" +
        "Encountered exception: Bad argument received for brighten command.\n" +
        "Try re-entering the command?\n" +
        "Encountered exception: Command 'undefined-command' not known.\n" +
        "Try re-entering the command?\n" +
        "Encountered exception: Command 'blah' not known.\n" +
        "Try re-entering the command?\n" +
        "Done.",
        responseLog.toString());
  }

}
