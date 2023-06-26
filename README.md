# ood_image_processing
###### A program authored by Nathaniel Dorson and Dallon Archibald.

### Completed Functionality

- Downscale an image implementation: Yes
- Script command to downscale an image: Yes
- Downscale an image from GUI: Yes
- Partially manipulate an image implementation via masking: Yes
- Script command to mask an image: Yes

### Downscale Addition Explanation

- Created extended controllers that support the new command implementation via composition
of an extended features object, further explained below in the "Changes in Design for Extra Credit".
  - Created a `Downscale` command that implements `ImageProcessingCommand`, added to the command
pattern with the `ExtendedControllerFeatures`.
  - Created a new main class `ExtendedMain` that handles the new functionality with the 
`ExtendedGUIController` or `ExtendedInteractiveController` and `ExtendedScriptedController`,
for running the image processor through the GUI or script commands respectively.
- Added the Downscale button and corresponding text fields for width and height directly to the
existing GridBagLayout in the `SwingGUIView`.
  - Could have achieved the same result through extension, but that would also require changes to
the original code to make some things protected and such, and this way was simpler.

### Masking Addition Explanation

- Created a `MaskedCommand` that implements `ImageProcessingCommand` and represents the
functionality for partial image manipulation on a specific command and mask.
- Overrode the `createProcessingCommand` method in the `ExtendedControllerFeatures` to create
masked commands to be run when applicable.
  - The mask is the last item supplied by the scanner.
  - First the `ExtendedControllerFeatures` runs the superclass implementation of `createProcessingCommand`
to create the base command. Then, if a mask image is also provided, wraps the base command with a
`MaskedCommand` instantiation.

### Design Explanation

#### Changes in Design for Extra Credit

- Added constructors to specify the features object delegate for controllers.
  - This enables controller extension by first extending the `ControllerFeatures` class, and then
passing an instance of that object off to the controllers we already implemented to easily extend
their functionality.
- Added a helper method to first build a processing command before running it for the controllers.
  - This allows extensions of controllers to override how processing commands are built, which
enables the extra logic needed for masking. It changes nothing about how our old code works, just
makes it more flexible.

#### Changes in Design Between Part 2 and 3

- Abstracted controller functionality into the `ControllerFeatures` class.
  - In order to have a features delegate for the gui controller to pass to the view for interaction
purposes, we were duplicating a lot of the code that was written directly into our `SimpleImageProcessorController`,
such as the code for loading and saving images, and running image processing commands. To avoid this duplication,
we made the `ControllerFeatures` usable in our original controllers and gave them an instance of
this class to delegate some of their functionality to.
  - The `GuiControllerFeatures` then extends `ControllerFeatures` with a very small amount of code
that allows for updating the view when images are processed/loaded (since the original controllers
have no need for a view, and therefore this is unique functionality for the GUI controller).

#### Changes in Design Between Part 1 and 2

- Moved all logic for commands into the command classes
  - In order to make our design more extensible we moved logic away from the `Image` implementation
and into the command classes. (e.g. a map pixels method was moved out of the simple image impl) Since
all the logic for image processing is now within the image processing commands, new commands can be
added without having to extend the image model.
- Removed the self-processing part of the image model
  - Previously we had separate interfaces `ImageModel` and `ImageModelState`, with the first
supposed to represent an image that could be processed, and the second representing a readonly image.
However, in this assignment we realized that to keep our design extensible, we ought to put all
image processing logic in the command pattern objects, and rely on reading
- Added a factory method in `ImageModel`
  - the new `createNew` method in `ImageModel` effectively allows for constructor polymorphism - it
is useful in image processing commands, where the command has been provided an image interface, but
would like to return the same concrete implementation as the image passed in. This `createNew` method
allows for this behavior in an intuitive way.
- Added a static set of command implementations to reduce cluttering in the controller and also
clearly show what commands the program supports
- Replaced old versions of commands (luma, red-component, etc.) with new versions that
are ColorTransformations
  - Simple change, for consistency
- Added an enum representing color channel (one of: red, green, blue)
  - This is useful to allow for applying filters to specific channels of images. Along with this
addition we added methods on the color interface to get the channel value when provided an instance
of the enum.
- Removed logic from the `IColor` interface (e.g. `IColor` no longer provides a `value()` method to
get color value)
  - Since there are many different ways to process a color to get some new color based on
the channels, it makes sense to have all processing be done by reading the color channels and then
externally creating a new color, rather than having to extend the `Color` interface for each new
way to process colors.
- Removed the `ImageWriter` interface
  - As soon as support for more than just PPMs was needed, the ability to properly mock an image writer
using dependency injection became cumbersome. The interface and implementations were also a little
clunky. We have switched to have image writing capabilities in the `ImageUtil` class (mirroring
image reading), and allow for mocking using an optional image data destination override.

#### General Structure

**_Controller_**
- The `ImageProcessorController` interface represents a controller for an image processor program
- `SimpleImageProcessorController` is an abstract implementation of the above interface, which knows
  of a basic set of commands, and has the ability to run commands using an image processor, but does
  not implement the `run` method, leaving that to concrete implementations
- `ScriptedImageProcessorController` and `InteractiveImageProcessorController` are concrete
  extensions for `SimpleImageProcessorController`. The difference is in their `run` methods; the
  scripted controller is for use with scripts and therefore fails if any exception is thrown, whereas
  the interactive controller wants to be more forgiving and therefore responds to the user alerting
  them of commands not working and asking them for re-entry.
- `GuiImageProcessorController` is an implementation of `ImageProcessorController` that controls a
  GUI view, and advertises a features object to that view to allow the view to request functionality
  based on button presses and the like.
- `ControllerFeatureSet` is an interface that represents the actions that all image processor
  controllers can do: load, save, and process images.
- `ControllerFeatures` is an implementation of the above interface with all the functionality common
  to all the controllers.
- `GuiControllerFeatures` extends `ControllerFeatures` and additionally controls a view object so
  that when images are loaded or processed, the view can be informed accordingly.

**_Commands_**
- An `ImageProcessingCommand` represents a command that can process an image model to make a new image.
- All the concrete implementations of `ImageProcessingCommand` are used to allow for some sort of
  useful command to be run on image models.
- Static concrete implementations of `ImageProcessingCommand`s are stored in `CommandImpls`, which
  acts as a sort of utility class to pre-define the commands used in the image processor for easy
  reference and consistency across multiple locations.

**_Model_**
- The `ImageProcessor` represents an image processor that keeps track of multiple images.
- `SimpleImageProcessor` implements the ability to load images, retrieve images, and process images
  using commands that are passed to it from callers.
- The `ImageModel` interface represents an observable version of an image.
- `SimpleImage` implements the above image interface to allow for image instantiation, dimension
  retrieval, and individual pixel (color) retrieval on images.
- The `IColor` interface represents the actions that should be available for a basic color representation.
- The `Color` class implements the above interface to allow for colors to be represented, added together,
  and have their channels retrieved.

**_View_**
- The `ImageProcessorView` represents the functionality of a view in an image processing program.
- The `SwingGuiView` is the main implementor of the above interface, and is set up to provide a
  nice graphical user interface that allows the user to control an image processing program. Control
  is achieved via action requests with a controller features object that is passed by the controller.
- The `HistogramPanel` class is an implementation of a GUI panel that can display a histogram. Used
  in the `SwingGuiView` class.

**_Utility_**
- The `ImageUtil` class provides utility functions for reading all sorts of images in to the
  internal representation for images.
- The `ImageUtil` class provides utility functions for writing `ImageModel` representations of images
  out to the filesystem.
- The `ImageUtil` class provides utility functions for converting `ImageModel`s into BufferedImages
  and hashmap histogram representations of the values and occurrences of colors in the image.
- The `Util` class provides a `requireNonNullArg` method that is very similar to `Objects.requireNonNull`,
  but throws an `IllegalArgumentException` for consistency with how we normally handle errors.


#### Notable Design Decisions

- Luma calculated as the closest integer to the result of `0.2126 * r + 0.7152 * g + 0.0722 * b`
- If a PPM image with a bit-depth other than 8 is loaded, the values are all scaled to the appropriate place in the range 0-255
- You can overwrite/update the internally saved files if you run a command using the same `<name>`
  and `<updated name>`
- An image must be at least 1x1 pixel in order to be considered a valid image
- Even though comment lines are permitted in scripts, comments are dropped when images are
  moved into an internal representation and therefore not restored at the end. This makes sense
  because comments might be pointing out specific lines in the `ppm` file - so if the image is
  transformed in some way, there is no way to know where (or even if) it would be appropriate
  to replace the comment.

#### Example Images

The images in the `res/` and `test/` directories are of our own creation,
except for `res/view_of_castello.jpg`, which is [in the public domain](https://www.nga.gov/collection/art-object-page.130897.html).
