# LineEditor-MVC

This project is a JavaFX application for drawing, manipulating, and interacting with lines using a full **Model-View-Controller (MVC)** architecture. It demonstrates key concepts such as event handling, snapping objects to a grid, undo/redo functionality, grouping, selection and multiple selection. The project was developed as part of a university assignment and highlights advanced JavaFX and software design principles.

## Features

### Part 1: Point Transforms and Snap-to-Grid
- Draw lines by holding the **Shift key** and dragging the mouse.
- Snap endpoints of lines to a 20-pixel grid when created or moved.
- Visual interaction features:
  - Selected lines are displayed with pink endpoints and a pink line.
  - Non-selected lines are drawn in dark purple.
  - Hovering near a line (within a 5-pixel threshold) highlights the line with a 10-pixel-wide gray background.
- Rotate selected lines using the arrow keys:
  - **Left Arrow**: Counterclockwise rotation.
  - **Right Arrow**: Clockwise rotation.
- Scale selected lines using the arrow keys:
  - **Up Arrow**: Scale up.
  - **Down Arrow**: Scale down.
- Delete selected lines using the **Delete** or **Backspace** keys.

### Part 2: Multiple Selection and Grouping
- **Multiple Selection**:
  - Hold the **Control key** and click to select/deselect multiple items.
  - Use a rubber-band selection by dragging the mouse to select multiple items within a rectangle.
  - Rubber-band rectangle is displayed as a dotted line.
- **Grouping**:
  - Press the **G key** to group selected items into a group. Groups are displayed with a pink bounding box.
  - Press the **U key** to ungroup the first selected group.
  - Nested groups are supported.

### Part 3: Undo/Redo
- Full undo/redo functionality implemented with a command stack system.
- Supported commands:
  - Create line
  - Delete items
  - Adjust endpoint
  - Move items
  - Rotate items
  - Scale items
  - Group/ungroup items
- Undo: Press **Z** to undo the last action.
- Redo: Press **R** to redo the last undone action.

## Technologies and Concepts
- **JavaFX**: For the user interface and graphical interactions.
- **Model-View-Controller (MVC)**: Clear separation of concerns:
  - `LineModel`: Represents the data layer, storing objects like lines and groups.
  - `DView`: Immediate-mode graphical view for rendering objects.
  - `AppController`: Handles user interactions and interprets input using a state machine.
- **Publish-Subscribe**: Communication between models and views.
- **Point Transforms**: For rotating and scaling objects.
- **State Machine**: For interaction handling in the controller.

## Project Structure
- `EditorApp`: The main application class that initializes the project.
- `MainUI`: Organizes views into an interface.
- `LineModel`: Stores all line and group objects.
- `DLine`: Represents individual line objects.
- `Endpoint`: Represents line endpoints.
- `DView`: Provides a graphical view of objects in the model.
- `AppController`: Handles user input and events.
- `Rubberband`: Stores details of the rubber-band rectangle for selection.
- `DGroup`: Represents groups of objects.
- `Groupable`: Interface for groupable objects.
- `DCommand`: Interface for commands used in the undo/redo system.
