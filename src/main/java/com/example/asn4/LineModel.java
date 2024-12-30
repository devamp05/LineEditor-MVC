package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class LineModel {
    // a private list to hold lines of our model
    private ArrayList<Groupable> elements;

    // a private list to hold subscribers
    private ArrayList<Subscriber> subscribers;

    // private attribute to hold grid dimensions
    private double gridWidth, gridHeight;

    // attribute to store handles corner radius
    private double cornerRadius;

    // fixed rotation angle for lines
    private double rotationAngle;

    public LineModel()
    {
        this.subscribers = new ArrayList<>();
        this.elements = new ArrayList<>();
        // initially our grid is 20 * 20
        this.gridWidth = 20;
        this.gridHeight = 20;

        this.cornerRadius = 5;  // can also have a method to set this or pass it from MainUI but for now keeping it like this

        this.rotationAngle = 3;    // manually set rotation angle here but this can be passed through constructor or method as well
    }

    // a method to add a subscriber
    public void addSubscriber(Subscriber subscriber)
    {
        subscribers.add(subscriber);
    }

    // getters for  gridWidth and height
    public double getGridWidth() {
        return gridWidth;
    }

    public double getGridHeight() {
        return gridHeight;
    }


    public ArrayList<Groupable> getEntities() {
        return elements;
    }

    // another add to use for create, this could have been done using the other one but I think its not a problem to have 2 of this
    public void add(DLine line)
    {
        elements.add(line);
        notifySubscribers();
    }

    // a method to add a list of groupables to the model for undo delete
    public void add(List<Groupable> items)
    {
        elements.addAll(items);
        notifySubscribers();
    }

    // method to resize line based on an endpoint so line is resizeable from both the ends
    public void resizeLine(DLine line, Endpoint prevEndpoint, double newX, double newY)
    {
        line.resize(prevEndpoint, newX, newY);
        notifySubscribers();
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    // another method definition that takes a list of lines and checks if a click was on any of the endpoints of that list
    public DLine onEndpoint(List<Groupable> elements, double x, double y)
    {
        for(Groupable element: elements)
        {
            if(!element.isGroup())  // we know it is a line if its not a group, using instanceof would be
                // better I think but this will work in our case this also makes sure to only check lines
                // that are in the selection and not the groups because groups don't show their endpoints
            {
                if(((DLine)element).onEndPoint(x, y))
                {
                    return ((DLine)element);
                }
            }
        }
        return null;
    }

    public Endpoint whichEndpoint(DLine line, double x, double y)
    {
        return line.whichEndPoint(x, y);
    }

    // a method to findNearPoint to snap to grid
    public Endpoint findNearPoint(double x, double y)
    {
        // we have to snap to the nearest point on the grid
        double remainder = x % gridWidth;
        if(remainder > gridWidth/2)
        {
            x += gridWidth - remainder;
        }
        else
        {
            x -= remainder;
        }
        remainder = y % gridHeight;
        if(remainder > gridHeight/2)
        {
            y += gridHeight - remainder;
        }
        else
        {
            y -= remainder;
        }
        return new Endpoint(x, y);
    }

    public boolean contains(double x, double y)
    {
        return elements.stream().anyMatch(element -> element.contains(x, y));  // alternative for forEach like TA showed in lab
    }

    public Groupable whichGroup(double x, double y)
    {
        return elements.stream().filter(element -> element.contains(x,y)).findFirst().orElse(null); // also shown by TA in lab
    }

    public void move(List<Groupable> elements, double dX, double dY)
    {
        elements.forEach(element -> element.move(dX, dY));
        notifySubscribers();
    }

    public void rotate(List<Groupable> elements, double direction)
    {
        // convert degrees to radians and rotate
        elements.forEach(element -> element.rotate(direction * Math.toRadians(rotationAngle)));
        notifySubscribers();
    }

    public void scale(List<Groupable> elements, double scaleFactor)
    {
        // convert degrees to radians and rotate
        elements.forEach(element -> element.scale(scaleFactor));
        notifySubscribers();
    }

    public void remove(List<Groupable> elements)
    {
        this.elements.removeAll(elements);
        notifySubscribers();
    }

    // another definition of remove to be used by insert command
    public void remove(Groupable line)
    {
        this.elements.remove(line);
        notifySubscribers();
    }

    public List<Groupable> containsInRubberband(Rubberband rubberband)
    {
        ArrayList<Groupable> contains = new ArrayList<>();
        elements.forEach(element -> {
            if(element.containsInRubberband(rubberband)) contains.add(element);
        });
        return contains;
    }

    // We will need another Group method which accepts a Group and adds it to the model instead of creating one itself for undo redo.
    public void group(DGroup newGroup)
    {
        if(newGroup != null)
        {
            // first remove all the children of this new group from the model
            newGroup.getChildren().forEach(item -> elements.remove(item));

            // then add the newGroup to the model
            elements.add(newGroup);

            notifySubscribers();
        }
    }

//    public List<Groupable> ungroup(List<Groupable> selection)
//    {
//        List<Groupable> newSel = new ArrayList<>();
//        // for all the groups in the selection
//        selection.forEach(element -> {
//            if(element.isGroup())
//            {
//                // remove the group from the model
//                elements.remove(element);
//
//                // and add all its children to the model
//                elements.addAll(element.getChildren());
//                newSel.addAll(element.getChildren());
//            }
//            else
//            {
//                newSel.add(element);
//            }
//        });
//        notifySubscribers();
//        return newSel;
//    }

    // a new ungroup to be called by groups undo command because it will always have a single Group
    public List<Groupable> ungroup(DGroup group)
    {
        if(group != null)
        {
            // remove the group from the model
            elements.remove(group);

            // and add all its children to the model
            elements.addAll(group.getChildren());

            notifySubscribers();

            return group.getChildren();
        }
        return null;
    }

    private void notifySubscribers()
    {
        subscribers.forEach(subscriber -> subscriber.modelChanged());
    }
}
