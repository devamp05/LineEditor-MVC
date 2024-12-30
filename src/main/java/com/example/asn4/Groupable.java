package com.example.asn4;

import java.util.List;

// implement the groupable interface as discussed in the class from class notes
public interface Groupable {
    boolean isGroup();
    List<Groupable> getChildren();
    void move(double dX, double dY);
    void rotate(double angle, double centerX, double centerY);
    void rotate(double angle);
    void scale(double scaleFactor);

    // I think we will need some extra methods
    // coordinates of
    double getXIn();
    double getYIn();
    double getXOut();
    double getYOut();

    // I think contains is also needed in both
    boolean contains(double x, double y);
    boolean containsInRubberband(Rubberband rubberband);
}