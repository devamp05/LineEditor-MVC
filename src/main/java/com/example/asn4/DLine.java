package com.example.asn4;

import java.util.List;

public class DLine implements Groupable{
    // to store start and end locations
    private double x1, x2, y1, y2;

    // to store how big is the corner endpoint
    private double cornerRadius;

    // threshold distance allowed to consider a position is within a lines region
    private double thresholdDistance;

    public DLine(double x1, double y1, double x2, double y2, double cornerRadius)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.cornerRadius = cornerRadius;
        thresholdDistance = 5;  // setting thresholdDistance to 5 manually for now but this can be passed
        // as constructor parameter or in a setFunction
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    // so, this function was having difficulty when I used == problem and I tried to print the line coordinates
    // and endpoint coordinates that I passed and they looked same on printf then I realized that it maybe because
    // of that floating point representation problem we learn about in CMPT 215 then I tried to find a professional
    // solution for it but I found some people having the same difficulty on stack overflow but there
    // wasn't a professional way to solve this mentioned in there so I used what came to my mind which was
    // checking their difference and considering it to be 0 if it is very close to 0 something like 0.000000001 or less
    // is considered to be 0, I saw that printf was printing 6 0s after the decimal and knew 6 places after
    // decimal point were same for both the numbers but I have used 8 0's just because I like number 8 more and it works
    // a method to resize line based on which endpoint is dragged
    public void resize(Endpoint endpoint, double newX, double newY)
    {
        if(endpoint != null)
        {
            // if x1, y1 endpoint is dragged then resize from that end
            if (Math.abs(endpoint.getX() - getX1()) <= 0.000000001 && Math.abs(endpoint.getY() - getY1()) <= 0.000000001) {
                x1 = newX;
                y1 = newY;
            }
            // otherwise resize from the other end but make sure other end is passed correctly
            else if (Math.abs(endpoint.getX() - getX2()) <= 0.000000001 && Math.abs(endpoint.getY() - getY2()) <= 0.000000001) {
                x2 = newX;
                y2 = newY;
            }
            else
            {
//                System.out.printf("line: %f, %f, %f, %f, endPoint: %f, %f", x1, y1, x2, y2, endpoint.getX(), endpoint.getY());
//                System.out.println("problem here");
            }
        }
    }

    // method to check if a click is on any one of the lines endpoint defined by the handle circle with cornerRadius
    public boolean onEndPoint(double x, double y)
    {
        // the radius formula used in lineDemo lab
        return Math.hypot(x - x1, y - y1) < cornerRadius || Math.hypot(x - x2, y - y2) < cornerRadius;
    }

    // method to check which endpoint was the click on
    public Endpoint whichEndPoint(double x, double y)
    {
        // hold distance from endpoints in this variable so that we drag the correct one in edge case when both
        // are very close
        double distanceBwEP1, distanceBwEP2;

        distanceBwEP1 = Math.hypot(x - x1, y - y1);

        distanceBwEP2 = Math.hypot(x - x2, y - y2);

        if(distanceBwEP1 <= cornerRadius && distanceBwEP2 <= cornerRadius)
        {
            // edge case when both are in the handle distance,
            if(distanceBwEP1 < distanceBwEP2)
            {
                return new Endpoint(x1, y1);
            }
            return new Endpoint(x2, y2);
        }
        else if(distanceBwEP1 <= cornerRadius)
        {
            return new Endpoint(x1, y1);
        }
        else if(distanceBwEP2 <= cornerRadius)
        {
            return new Endpoint(x2, y2);
        }
        else return null;
    }

    // function to check if a position is within the threshold distance of our line
    public boolean contains(double x, double y)
    {
        if(distanceFromLine(x, y) > thresholdDistance)
        {
            return false;
        }
        // I had to search a formula for a distance between a point and a line segment because the ones provided in
        // the lecture notes were calculating perpendicular distance making contains return true even if a point was
        // outside the line but on the diagonal
        double dotProduct = ((x - x1) * (x2 - x1)) + ((y - y1) * (y2 - y1));
        double squaredLength = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);

        return dotProduct >= 0 && dotProduct <= squaredLength;
    }

    // a function to return distance from the line
    private double distanceFromLine(double x, double y)
    {
        // first calculate the line length used in that formula
        double length = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));

        // I took the formula from week 7 lecture slides 13-interactive-graphics.pdf page 14
        double ratioA = (y1 - y2) / length;
        double ratioB = (x2 - x1) / length;
        double ratioC = -1 * ((y1 - y2) * x1 + (x2 - x1) * y1) / length;

        return Math.abs((ratioA * x) + (ratioB * y) + ratioC);
    }

    // a new contains method that checks if a line is in a rubberbands dimensions
    public boolean containsInRubberband(Rubberband rubberband)
    {
        return rubberband.getX() <= x1 && rubberband.getX() <= x2 && x1 <= (rubberband.getX() + rubberband.getWidth()) && x2 <= (rubberband.getX() + rubberband.getWidth())
                && rubberband.getY() <= y1 && rubberband.getY() <= y2 && y1 <= (rubberband.getY() + rubberband.getHeight()) && y2 <= (rubberband.getY() + rubberband.getHeight());
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public List<Groupable> getChildren() {
        return null;
    }

    @Override
    public void move(double dX, double dY) {
        x1 += dX;
        y1 += dY;
        x2 += dX;
        y2 += dY;
    }

    @Override
    public void rotate(double alpha, double centerX, double centerY) {
        // need to store this because they change after applying transforms
        double translatedX1, translatedY1, translatedX2, translatedY2;
        double newX1, newY1, newX2, newY2;


        // rotate with point transform using formula in the slides

        // first translate to origin
        translatedX1 = x1 - centerX;
        translatedY1 = y1 - centerY;

        // then rotate
        newX1 = Math.cos(alpha) * translatedX1 - Math.sin(alpha) * translatedY1;
        newY1 = Math.sin(alpha) * translatedX1 + Math.cos(alpha) * translatedY1;

        // then translate back
        x1 = newX1 + centerX;
        y1 = newY1 + centerY;

        // and repeat for x2, y2
        translatedX2 = x2 - centerX;
        translatedY2 = y2 - centerY;

        newX2 = Math.cos(alpha) * translatedX2 - Math.sin(alpha) * translatedY2;
        newY2 = Math.sin(alpha) * translatedX2 + Math.cos(alpha) * translatedY2;

        x2 = newX2 + centerX;
        y2 = newY2 + centerY;
    }

    @Override
    public void rotate(double alpha) {
        // need to store this because they change after applying transforms
        double centerX, centerY, translatedX1, translatedY1, translatedX2, translatedY2;
        double newX1, newY1, newX2, newY2;

        // get center to rotate around center
        centerX = (x1 + x2)/2;
        centerY = (y1 + y2)/2;

        // rotate with point transform using formula in the slides

        // first translate to origin
        translatedX1 = x1 - centerX;
        translatedY1 = y1 - centerY;

        // then rotate using formulas given in the lecture slide of week 7
        newX1 = Math.cos(alpha) * translatedX1 - Math.sin(alpha) * translatedY1;
        newY1 = Math.sin(alpha) * translatedX1 + Math.cos(alpha) * translatedY1;

        // then translate back
        x1 = newX1 + centerX;
        y1 = newY1 + centerY;

        // and repeat for x2, y2
        translatedX2 = x2 - centerX;
        translatedY2 = y2 - centerY;

        newX2 = Math.cos(alpha) * translatedX2 - Math.sin(alpha) * translatedY2;
        newY2 = Math.sin(alpha) * translatedX2 + Math.cos(alpha) * translatedY2;

        x2 = newX2 + centerX;
        y2 = newY2 + centerY;

    }

    @Override
    public void scale(double scaleFactor) {
        // same like rotation first translate center to origin scale and then translate back
        double centerX, centerY, translatedX1, translatedY1, translatedX2, translatedY2;
        double newX1, newY1, newX2, newY2;

        // get center to rotate around center
        centerX = (x1 + x2)/2;
        centerY = (y1 + y2)/2;

        // first translate to origin
        translatedX1 = x1 - centerX;
        translatedY1 = y1 - centerY;

        newX1 = translatedX1 * scaleFactor;
        newY1 = translatedY1 * scaleFactor;

        // then translate back
        x1 = newX1 + centerX;
        y1 = newY1 + centerY;

        // and repeat for x2, y2
        translatedX2 = x2 - centerX;
        translatedY2 = y2 - centerY;

        newX2 = translatedX2 * scaleFactor;
        newY2 = translatedY2 * scaleFactor;

        x2 = newX2 + centerX;
        y2 = newY2 + centerY;
    }

    // think of line as a box and return accordingly
    @Override
    public double getXIn() {
        if(x1 < x2)
        {
            return x1;
        }
        return x2;
    }

    @Override
    public double getYIn() {
        if(y1 < y2)
        {
            return y1;
        }
        return y2;
    }

    // for width and height return the outside coordinate
    @Override
    public double getXOut() {
        if(x1 > x2)
        {
            return x1;
        }
        return x2;
    }

    @Override
    public double getYOut() {
        if(y1 > y2)
        {
            return y1;
        }
        return y2;
    }
}
