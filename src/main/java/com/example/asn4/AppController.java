package com.example.asn4;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class AppController {
    // a private attribute to hold our model
    private LineModel model;

    // a private attribute to hold our iModel
    private InteractionModel iModel;

    // current state of the controller
    private ControllerState currentState;

    // prevX and Y and I think I will also need startX startY to implement moves undo
    private double prevX, prevY, startX, startY, totalRotate, totalScaleFactor;   // for rotation and scaling I plane on saving as soon as something changes or even rotation or scale goes in opposite direction
    private int scalingUp;   // set this to 1 when we are scaling up and -1 when we are scaling down because I think using the % operation might also result in same floating point representation error we had for == operator

    // I think I will also need to store prevEndpoint of the line
    private Endpoint prevEndpoint;

    // looks like I will have to store the previous line as well
    private DLine prevLine;

    public AppController()
    {
        // initially set currentState to ready
        currentState = ready;
    }

    // a method to set our model in the view
    public void setModel(LineModel lineModel)
    {
        this.model = lineModel;
    }

    // a method to set iModel
    public void setiModel(InteractionModel iModel)
    {
        this.iModel = iModel;
    }

    // a function to snap endpoints to grid corners
    public void snap(double x, double y)
    {
        Endpoint ep = model.findNearPoint(x, y);
        if(ep != null)
        {
            prevX = ep.getX();
            prevY = ep.getY();
        }
    }

    // controller event setup methods
    public void handlePressed(MouseEvent mouseEvent){currentState.handlePressed(mouseEvent);};
    public void handleDragged(MouseEvent mouseEvent){currentState.handleDragged(mouseEvent);};
    public void handleReleased(MouseEvent mouseEvent){currentState.handleReleased(mouseEvent);};

    // this one is new this even identifies mouse movements to get hover effect
    public void handleMoved(MouseEvent mouseEvent){currentState.handleMoved(mouseEvent);}
    public void handleKeyPressed(KeyEvent keyEvent){currentState.handleKeyPressed(keyEvent);}

    // an abstract class declaration for controller states
    private abstract class ControllerState {
        public void handlePressed(MouseEvent mouseEvent){};
        public void handleReleased(MouseEvent mouseEvent){};
        public void handleDragged(MouseEvent mouseEvent){};
        public void handleMoved(MouseEvent mouseEvent){};
        public void handleKeyPressed(KeyEvent keyEvent){};
    }

    private ControllerState ready = new ControllerState() {
        @Override
        public void handlePressed(MouseEvent mouseEvent) {
            // save the rotation if there was any as soon as a new interaction happens
            if(totalRotate > 0 || totalRotate < 0)
            {
                saveRotate();
            }
            if(scalingUp != 0)
            {
                saveScaling();
            }

            // check if shift is down
            if(mouseEvent.isShiftDown())
            {
                // set prevX and prevY for line creation
                snap(mouseEvent.getX(), mouseEvent.getY());
            }
            else
            {
                // if shift is not down then check if a press is on an endpoint so that we can resize that line
                if(!iModel.getSelection().isEmpty())
                {
                    prevLine = model.onEndpoint(iModel.getSelection(), mouseEvent.getX(), mouseEvent.getY());
                    if(prevLine != null)
                    {
                        prevEndpoint = model.whichEndpoint(prevLine,mouseEvent.getX(), mouseEvent.getY());
                    }
                    else
                    {
                        // this is getting ready for a rubberband selection
                        prevX = mouseEvent.getX();
                        prevY = mouseEvent.getY();
                    }
                }
                else
                {
                    // this is getting ready for a rubberband selection
                    prevX = mouseEvent.getX();
                    prevY = mouseEvent.getY();
                }
            }
        }

        @Override
        public void handleDragged(MouseEvent mouseEvent) {
            // check if shift is down
            if(mouseEvent.isShiftDown())
            {
                // trying to use the insert command to implement undo functionality
                // first create a new createLine command
                CreateLine createLine = new CreateLine(model, iModel, prevX, prevY, mouseEvent.getX(), mouseEvent.getY());
                createLine.doit();
                // then push it on the UndoStack
                iModel.pushUndoStack(createLine);

                // now this is the tricky part because of how I had implemented it
                Groupable element = model.whichGroup(mouseEvent.getX(), mouseEvent.getY());
                if(!element.isGroup())
                {
                    prevLine = (DLine) element;

                    prevEndpoint = model.whichEndpoint(prevLine, mouseEvent.getX(), mouseEvent.getY());

                    if(prevEndpoint != null)
                    {
                        // set prevX and prevY to be the endpoint other than snap or mouse events coordinates
                        prevX = prevEndpoint.getX();
                        prevY = prevEndpoint.getY();

                        // and change the state to creating
                        currentState = resizing;
                    }
                }
            }
            else
            {
                // if shift is not down then check if a press is on an endpoint so that we can resize that line
                if(!iModel.getSelection().isEmpty())
                {
                    prevLine = model.onEndpoint(iModel.getSelection(), mouseEvent.getX(), mouseEvent.getY());
                    if(prevLine != null)
                    {
                        prevEndpoint = model.whichEndpoint(prevLine,mouseEvent.getX(), mouseEvent.getY());
                        // also create a temporary adjustEndpoint command before switching to resizing state
                        DCommand tempCommand = new AdjustEndpoint(model, iModel, prevLine, prevEndpoint, mouseEvent.getX(), mouseEvent.getY());

                        tempCommand.doit();


                        // have to set prevX and prevY so that it can be set for undo
                        snap(mouseEvent.getX(), mouseEvent.getY());

                        prevEndpoint = model.whichEndpoint(prevLine, prevX, prevY);

                        currentState = resizing;
                    }
                    else if(iModel.isInSelection(model.whichGroup(prevX, prevY)))   // see if prevX and prevY was on any of the selected lines
                    {
                        // a new temporary move command
                        DCommand tempCommand = new MoveItems(model, iModel, iModel.getSelection(), mouseEvent.getX() - prevX, mouseEvent.getY() - prevY);
                        tempCommand.doit();
                        startX = prevX;
                        startY = prevY;
                        prevX = mouseEvent.getX();
                        prevY = mouseEvent.getY();
                        currentState = moving;
                    }
                    else if(model.contains(mouseEvent.getX(), mouseEvent.getY()))
                    {
                        if(!mouseEvent.isControlDown())
                        {
                            // if control is up then we clear the selection first
                            iModel.clearSelection();
                        }
                        // a new line has been dragged
                        iModel.addToSelection(model.whichGroup(mouseEvent.getX(), mouseEvent.getY()));
                        prevX = mouseEvent.getX();
                        prevY = mouseEvent.getY();
                        startX = prevX;
                        startY = prevY;
                        currentState = moving;
                    }
                    else
                    {
                        // drag on background so create a rubberband according to the direction of drag
                        double currentX = mouseEvent.getX();
                        double currentY = mouseEvent.getY();
                        if(currentX < prevX)
                        {
                            // meaning drag was in opposite direction
                            if(currentX < prevY)
                            {
                                // meaning the drag is going towards upper left corner
                                iModel.addRubberband(currentX, currentY, prevX - currentX, prevY - currentY);
                            }
                            else
                            {
                                // meaning the drag is going down left
                                iModel.addRubberband(currentX, prevY, prevX - currentX, currentY - prevY);
                            }
                        }
                        else
                        {
                            if(currentX < prevY)
                            {
                                // the drag is towards top right corner
                                iModel.addRubberband(prevX, currentY, currentX - prevX, prevY - currentY);
                            }
                            else
                            {
                                // this is our initial drag action
                                // first add a new entity to model
                                iModel.addRubberband(prevX, prevY, currentX - prevX, currentY - prevY);
                            }
                        }
                        currentState = selecting;
                    }
                }
                else if (model.contains(mouseEvent.getX(), mouseEvent.getY()))
                {
                    // selection is already empty here
                    iModel.addToSelection(model.whichGroup(mouseEvent.getX(), mouseEvent.getY()));
                    prevX = mouseEvent.getX();
                    prevY = mouseEvent.getY();
                    startX = prevX;
                    startY = prevY;
                    currentState = moving;
                }
                else
                {
                    // drag on background so create a rubberband according to the direction of drag
                    double currentX = mouseEvent.getX();
                    double currentY = mouseEvent.getY();
                    if(currentX < prevX)
                    {
                        // meaning drag was in opposite direction
                        if(currentX < prevY)
                        {
                            // meaning the drag is going towards upper left corner
                            iModel.addRubberband(currentX, currentY, prevX - currentX, prevY - currentY);
                        }
                        else
                        {
                            // meaning the drag is going down left
                            iModel.addRubberband(currentX, prevY, prevX - currentX, currentY - prevY);
                        }
                    }
                    else
                    {
                        if(currentX < prevY)
                        {
                            // the drag is towards top right corner
                            iModel.addRubberband(prevX, currentY, currentX - prevX, prevY - currentY);
                        }
                        else
                        {
                            // this is our initial drag action
                            // first add a new entity to model
                            iModel.addRubberband(prevX, prevY, currentX - prevX, currentY - prevY);
                        }
                    }
                    currentState = selecting;
                }
            }
        }

        @Override
        public void handleMoved(MouseEvent mouseEvent) {
            if(model.contains(mouseEvent.getX(), mouseEvent.getY()))
            {
                Groupable element = model.whichGroup(mouseEvent.getX(), mouseEvent.getY());
                if(!element.isGroup())
                {
                    // if the element we are on is a single line then set it as on
                    iModel.setOn((DLine) element);
                }
            }
            else
            {
                // we only want on to be highlighted while the mouse is on the line so when it moves out clear on
                iModel.clearOn();
            }
        }

        @Override
        public void handleKeyPressed(KeyEvent keyEvent) {
            if(keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE)
            {
                if(!iModel.getSelection().isEmpty())
                {
                    // save the rotation if there was any as soon as a new interaction happens
                    if(totalRotate > 0 || totalRotate < 0)
                    {
                        saveRotate();
                    }

                    if(scalingUp != 0)
                    {
                        saveScaling();
                    }

                    // deletion should be simple just create a new command call doit on it and add to undostack
                    // and to keep selection persistent I plan on not clearing the selection
                    DCommand deleteCommand = new DeleteItems(model, iModel, iModel.getSelection());
                    iModel.pushUndoStack(deleteCommand);
                    deleteCommand.doit();
                }
            }
            else if(keyEvent.getCode() == KeyCode.RIGHT)
            {
                if(!iModel.getSelection().isEmpty())
                {
                    // lets indicate clockwise rotation with 1 and counter-clockwise using -1
                    if(totalRotate < 0)
                    {
                        // it means user has changed the rotation direction so save rotate
                        saveRotate();
                    }

                    if(scalingUp != 0)
                    {
                        saveScaling();
                    }
                    // keep rotating with a temporary command
                    DCommand tempCommand = new RotateItems(model, iModel, iModel.getSelection(), 1);
                    tempCommand.doit();

                    totalRotate++;
                }
            }
            else if(keyEvent.getCode() == KeyCode.LEFT)
            {
                if(!iModel.getSelection().isEmpty())
                {
                    // lets indicate clockwise rotation with 1 and counter-clockwise using -1
                    if(totalRotate > 0)
                    {
                        // it means user has changed the rotation direction so save rotate
                        saveRotate();
                    }

                    if(scalingUp != 0)
                    {
                        saveScaling();
                    }
                    // keep rotating with a temporary command
                    DCommand tempCommand = new RotateItems(model, iModel, iModel.getSelection(), -1);
                    tempCommand.doit();

                    totalRotate--;
                }
            }
            else if(keyEvent.getCode() == KeyCode.UP)
            {
                if(!iModel.getSelection().isEmpty())
                {
                    // save the rotation if there was any as soon as a new interaction happens
                    if(totalRotate > 0 || totalRotate < 0)
                    {
                        saveRotate();
                    }

                    // check if we were already scaling
                    if(scalingUp != 0)
                    {
                        if(scalingUp == -1) // using this instead of == with % because I think it can also break like ==
                        {
                            saveScaling();
                            // scale with a temp command for now
                            DCommand tempCommand = new ScaleItems(model, iModel, iModel.getSelection(), 1.1);
                            tempCommand.doit();

                            scalingUp = 1;
                            totalScaleFactor = 1.1;
                        }
                        else
                        {
                            // we were scaling up so keep scaling
                            DCommand tempCommand = new ScaleItems(model, iModel, iModel.getSelection(), 1.1);
                            tempCommand.doit();

                            scalingUp = 1;
                            totalScaleFactor *= 1.1;
                        }
                    }
                    else
                    {
                        // we were scaling up so keep scaling
                        DCommand tempCommand = new ScaleItems(model,iModel, iModel.getSelection(), 1.1);
                        tempCommand.doit();
                        scalingUp = 1;

                        totalScaleFactor = 1.1; // set the scaleFactor for first scale
                    }
                }
            }
            else if(keyEvent.getCode() == KeyCode.DOWN)
            {
                if(!iModel.getSelection().isEmpty())
                {
                    // save the rotation if there was any as soon as a new interaction happens
                    if(totalRotate > 0 || totalRotate < 0)
                    {
                        saveRotate();
                    }

                    // check if we were already scaling
                    if(scalingUp != 0)
                    {
                        if(scalingUp == 1)
                        {
                            saveScaling();
                            // scale with a temp command for now
                            DCommand tempCommand = new ScaleItems(model, iModel, iModel.getSelection(), 0.9);
                            tempCommand.doit();
                            scalingUp = -1;
                            totalScaleFactor = 0.9;
                        }
                        else
                        {
                            // we were scaling down so keep scaling
                            DCommand tempCommand = new ScaleItems(model, iModel, iModel.getSelection(), 0.9);
                            tempCommand.doit();
                            totalScaleFactor *= 0.9;
                            scalingUp = -1;
                        }
                    }
                    else
                    {
                        // we were scaling up so keep scaling
                        DCommand tempCommand = new ScaleItems(model, iModel, iModel.getSelection(), 0.9);
                        tempCommand.doit();
                        scalingUp = -1;

                        totalScaleFactor = 0.9; // set the scaleFactor for first scale
                    }
                }
            }
            else if(keyEvent.getCode() == KeyCode.G)
            {
                // group all the selection if not empty
                if(!iModel.getSelection().isEmpty())
                {
                    // save the rotation if there was any as soon as a new interaction happens
                    if(totalRotate > 0 || totalRotate < 0)
                    {
                        saveRotate();
                    }

                    if(scalingUp != 0)
                    {
                        saveScaling();
                    }

                    // create group command and add to stack
                    DCommand groupCommand = new Group(model, iModel);
                    groupCommand.doit();

                    iModel.pushUndoStack(groupCommand);
                }
            }
            else if(keyEvent.getCode() == KeyCode.U)
            {
                // group all the selection if not empty
                if(!iModel.getSelection().isEmpty())
                {
                    // save the rotation if there was any as soon as a new interaction happens
                    if(totalRotate > 0 || totalRotate < 0)
                    {
                        saveRotate();
                    }

                    if(scalingUp != 0)
                    {
                        saveScaling();
                    }

                    // create group command and add to stack
                    DCommand ungroupCommand = new Ungroup(model, iModel);
                    ungroupCommand.doit();

                    iModel.pushUndoStack(ungroupCommand);
                }
            }
            else if(keyEvent.getCode() == KeyCode.Z)
            {
                if(totalRotate > 0 || totalRotate < 0)
                {
                    saveRotate();
                }
                if(scalingUp != 0)
                {
                    saveScaling();
                }
                iModel.handleUndo();
            }
            else if(keyEvent.getCode() == KeyCode.R)
            {
                if(totalRotate > 0 || totalRotate < 0)
                {
                    saveRotate();
                }
                if(scalingUp != 0)
                {
                    saveScaling();
                }
                iModel.handleRedo();
            }
        }

        @Override
        public void handleReleased(MouseEvent mouseEvent) {
            if(model.contains(mouseEvent.getX(), mouseEvent.getY()))
            {
                if(!mouseEvent.isControlDown())
                {
                    iModel.clearSelection();
                }
                iModel.addToSelection(model.whichGroup(mouseEvent.getX(), mouseEvent.getY()));
            }
            else
            {
                // click is on the background so clear selection
                iModel.clearSelection();
            }
        }
    };

    private ControllerState resizing = new ControllerState() {
        @Override
        public void handleReleased(MouseEvent mouseEvent) {
            // record the prevX prevY location before changing it
            double oldPrevX, oldPrevY;
            oldPrevX = prevX;
            oldPrevY = prevY;
            // snap back to grid corner
            snap(mouseEvent.getX(), mouseEvent.getY());

            // first a temp command to snap to the nearest grid
            DCommand tempCommand = new AdjustEndpoint(model, iModel, prevLine, prevEndpoint, prevX, prevY);
            tempCommand.doit();

            // then a final command that will be added to tha stack
            DCommand finalCommand = new AdjustEndpoint(model, iModel, prevLine, new Endpoint(oldPrevX, oldPrevY), prevX, prevY);

            // we won't do it just add it to the stack
            iModel.pushUndoStack(finalCommand);

            // change state back to ready on mouse release
            prevEndpoint = null;
            prevLine = null;
            currentState = ready;
        }

        @Override
        public void handleDragged(MouseEvent mouseEvent) {
            // resize the line using temporary command
            DCommand tempCommand = new AdjustEndpoint(model, iModel, prevLine, prevEndpoint, mouseEvent.getX(), mouseEvent.getY());
            tempCommand.doit();
            prevEndpoint = model.whichEndpoint(prevLine, mouseEvent.getX(), mouseEvent.getY());
        }
    };

    private ControllerState moving = new ControllerState() {
        @Override
        public void handleReleased(MouseEvent mouseEvent) {
            // now here the real move command comes in that has distance from start till end
            DCommand moveCommand = new MoveItems(model, iModel, iModel.getSelection(), prevX - startX, prevY - startY);

            // push this command to the undostack
            iModel.pushUndoStack(moveCommand);
            currentState = ready;
        }

        @Override
        public void handleDragged(MouseEvent mouseEvent) {
            DCommand tempCommand = new MoveItems(model, iModel, iModel.getSelection(), mouseEvent.getX() - prevX, mouseEvent.getY() - prevY);
            tempCommand.doit();
            prevX = mouseEvent.getX();
            prevY = mouseEvent.getY();
        }
    };

    private ControllerState selecting = new ControllerState() {
        @Override
        public void handleReleased(MouseEvent mouseEvent) {
            // select here
            List<Groupable> selection = model.containsInRubberband(iModel.getRubberband());
            iModel.addToSelection(selection);
            iModel.removeRubberband();
            currentState = ready;
        }

        @Override
        public void handleDragged(MouseEvent mouseEvent) {
            iModel.resizeRubberband(mouseEvent.getX() - prevX, mouseEvent.getY() - prevY);
        }
    };

    // a private method to save rotation action as soon as user goes into some other interaction
    private void saveRotate()   // call this as soon as user goes into some other interaction other than rotate
    {
        // create a new rotate command with total Rotation
        DCommand rotateCommand = new RotateItems(model, iModel, iModel.getSelection(), totalRotate);

        // set totalRotate back to 0
        totalRotate = 0;

        // and push command to the undoStack
        iModel.pushUndoStack(rotateCommand);
    }

    // a private method to save scaling action as soon as user goes into some other interaction
    private void saveScaling()   // call this as soon as user goes into some other interaction other than scaling
    {
        // create a new rotate command with total Rotation
        DCommand scaleCommand = new ScaleItems(model, iModel, iModel.getSelection(), totalScaleFactor);

        // set totalScaleFactor back to 0
        totalScaleFactor = 0;

        scalingUp = 0;  // set scaling up back to 0

        // and push command to the undoStack
        iModel.pushUndoStack(scaleCommand);
    }
}
