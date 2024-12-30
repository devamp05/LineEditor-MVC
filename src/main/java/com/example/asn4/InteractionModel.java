package com.example.asn4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class InteractionModel {
    // a private list to hold subscribers
    private ArrayList<Subscriber> subscribers;

    // an array list for multiple selection
    private HashSet<Groupable> selectedElements;

    // a new DLine attribute to identify if mouse is moved over a line
    private DLine on;

    // Rubberband for Rubberband selection
    private Rubberband rubberband;

    // undo and redo stack
    private Stack<DCommand> undoStack;  // there are ways to implement them manually but no harm in using
    // the built in ones like we did for the lab
    private Stack<DCommand> redoStack;

    public InteractionModel()
    {
        this.subscribers = new ArrayList<>();
        this.selectedElements = new HashSet<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    // a method to add a subscriber
    public void addSubscriber(Subscriber subscriber)
    {
        subscribers.add(subscriber);
    }

    // a method to add a line to selected if not there already and remove from selected if it is there
    public void addToSelection(Groupable element)
    {
        if(selectedElements.contains(element))
        {
            selectedElements.remove(element);
        }
        else
        {
            selectedElements.add(element);
        }
        notifySubscribers();
    }

    // a new definition for add to selection that takes a list as an argument
    public void addToSelection(List<Groupable> elements)
    {
        elements.forEach(this::addToSelection);
    }

    // a method to get selection list
    public List<Groupable> getSelection()
    {
        return this.selectedElements.stream().toList();
    }

    // a method to check if a line is in selection this can be done in view as well by getting the selections but I
    // think this is better
    public boolean isInSelection(Groupable element)
    {
        return selectedElements.contains(element);
    }

    public void clearSelection()
    {
        selectedElements.clear();
        notifySubscribers();
    }


    public void setOn(DLine on) {
        this.on = on;
        notifySubscribers();
    }

    public boolean isOn(DLine line)
    {
        return on == line;
    }

    public void clearOn()
    {
        on = null;
        notifySubscribers();
    }

    // rubberband methods to add resize get and remove rubberband
    public void addRubberband(double x, double y, double width, double height)
    {
        rubberband = new Rubberband(x, y, width, height);
        notifySubscribers();
    }

    public void resizeRubberband(double width, double height)
    {
        rubberband.resize(width, height);
        notifySubscribers();
    }

    public Rubberband getRubberband()
    {
        return rubberband;
    }

    public void removeRubberband()
    {
        rubberband = null;
        notifySubscribers();
    }

    // methods to undo and redo
    public void handleUndo()
    {
        if(!undoStack.isEmpty())
        {
            // get the last command inserted from the undostack
            DCommand undoCommand = undoStack.pop();

            // undo it
            undoCommand.undo();

            // put the command on the redo stack
            redoStack.push(undoCommand);

            notifySubscribers();
        }
    }

    public void handleRedo()
    {
        if(!redoStack.isEmpty())
        {
            // get the last command inserted from the redostack
            DCommand redoCommand = redoStack.pop();

            // redo it
            redoCommand.doit();

            // put the command on the undo stack
            undoStack.push(redoCommand);

            notifySubscribers();
        }
    }

    // methods to push new commands to the undo and redo stacks
    public void pushUndoStack(DCommand newCommand)
    {
        undoStack.push(newCommand);
    }

    private void notifySubscribers()
    {
        subscribers.forEach(subscriber -> subscriber.modelChanged());
    }
}
