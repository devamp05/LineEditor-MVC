package com.example.asn4;

public class Rubberband {
    // rubberband rectangle coordinates
    private double x, y, width, height;

    public Rubberband(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    // resize method from Box class of my assignment 3
    public void resize(double width, double height)
    {
        if(width < 0)
        {
            // this means coordinates have changed

            // now x should be previously where it was - change in its width in the negative direction
            x = x + width + this.width;
            this.width = - width;
        }
        else
        {
            this.width = width;
        }
        if(height < 0)
        {
            // in this case y has changed so it should be previously where it was - change in its negative height
            // or how far it wants to go in the other direction
            y = y + height + this.height;
            this.height = - height;
        }
        else
        {
            this.height = height;
        }
    }
}
