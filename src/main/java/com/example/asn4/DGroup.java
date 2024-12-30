package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class DGroup implements Groupable{
    private ArrayList<Groupable> children;

    // also need to know the bounding box of each group
    private double xIn, yIn, xOut, yOut;

    public DGroup(List<Groupable> selection)
    {
        children = new ArrayList<>();

        // first set x and y to a big number because initially they are always 0 in java so our lines x and
        // y won't change them because they will be smaller
        xIn = Double.MAX_VALUE;
        yIn = Double.MAX_VALUE;

        // like prof said in the lecture it is important to add each children from selection to Groupable individually
        // because selection might change later
        selection.forEach(group ->
        {
            children.add(group);
            // setup x, y, width and height accordingly
            if(group.getXIn() < xIn)
            {
                xIn = group.getXIn();
            }
            if(group.getYIn() < yIn)
            {
                yIn = group.getYIn();
            }
            if(group.getXOut() > xOut)
            {
                xOut = group.getXOut();
            }
            if(group.getYOut() > yOut)
            {
                yOut = group.getYOut();
            }
        });
    }
    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public List<Groupable> getChildren() {
        return children;
    }

    @Override
    public void move(double dX, double dY) {
        // call move on each children
        children.forEach(group -> group.move(dX, dY));
        updateBoxCoordinates();
    }

    @Override
    public void rotate(double angle, double centerX, double centerY) {
        children.forEach(group -> group.rotate(angle, centerX, centerY));
        updateBoxCoordinates();
    }

    @Override
    public void rotate(double angle) {
        // if no center coordinates are given then it means we are the outermost group so we have to pass in our
        // bounding box coordinates
        double centerX = (xIn + (xOut))/2; // our bounding box coordinates are simple
        double centerY = (yIn + (yOut))/2;
        rotate(angle, centerX, centerY);
    }

    @Override
    public void scale(double scaleFactor) {
        children.forEach(group -> group.scale(scaleFactor));
        updateBoxCoordinates();
    }

    public double getXIn()
    {
        return xIn;
    }

    @Override
    public double getYIn() {
        return yIn;
    }

    @Override
    public double getXOut() {
        return xOut;
    }

    @Override
    public double getYOut() {
        return yOut;
    }

    @Override
    public boolean contains(double x, double y) {
        return children.stream().anyMatch(group -> group.contains(x, y));
    }

    @Override
    public boolean containsInRubberband(Rubberband rubberband) {
        // if our bounding box is inside the rubberband then it is cointained in rubberband
        return rubberband.getX() <= xIn && (rubberband.getX() + rubberband.getWidth()) >= xOut &&
                rubberband.getY() <= yIn && (rubberband.getY() + rubberband.getHeight()) >= yOut;
    }

    // a method to update the bounding box coordinates once something changes like move, rotation or scaling
    private void updateBoxCoordinates()
    {
        // first set them to null
        xIn = Double.MAX_VALUE;
        yIn = Double.MAX_VALUE;
        xOut = 0;
        yOut = 0;

        // then recalculate
        children.forEach(group ->
        {
            // update x, y, width and height accordingly
            if(group.getXIn() < xIn)
            {
                xIn = group.getXIn();
            }
            if(group.getYIn() < yIn)
            {
                yIn = group.getYIn();
            }
            if(group.getXOut() > xOut)
            {
                xOut = group.getXOut();
            }
            if(group.getYOut() > yOut)
            {
                yOut = group.getYOut();
            }
        });
    }
}
