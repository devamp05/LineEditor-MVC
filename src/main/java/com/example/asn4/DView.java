package com.example.asn4;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;

public class DView extends StackPane implements Subscriber {

    // a private attribute to hold our model
    private LineModel model;

    // a private attribute to hold our iModel
    private InteractionModel iModel;

    // private attribute to hold gc
    private GraphicsContext gc;

    // private attributes to hold viewWidth and Height not required for now but maybe required later
    private double viewWidth, viewHeight;

    public DView(double viewWidth, double viewHeight)
    {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        // create a new canvas
        Canvas canvas = new Canvas(viewWidth, viewHeight);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
        Platform.runLater(this::requestFocus);
    }

    private void draw()
    {
        // clear the rect
        gc.clearRect(0,0, viewWidth, viewHeight);

        // draw the grid

        // set the stroke to black first
        gc.setStroke(Color.BLACK);

        // draw horizontal lines
        double gridWidth = model.getGridWidth();

        for(int i = 0; i <= viewHeight; i += gridWidth)
        {
            gc.strokeLine(0, i, viewWidth, i);
        }

        // draw vertical lines
        double gridHeight = model.getGridHeight();

        for(int i = 0; i <= viewWidth; i += gridHeight)
        {
            gc.strokeLine(i, 0, i, viewHeight);
        }

        model.getEntities().forEach(element ->{
            if (element.isGroup()) {
                // we only display the bounding box of selected group
                if(iModel.isInSelection(element))
                {
                    // draw the outermost bounding box
                    gc.setStroke(Color.HOTPINK);
                    gc.strokeRect(element.getXIn(), element.getYIn(), element.getXOut() - element.getXIn(), element.getYOut() - element.getYIn());
                }

                // now draw all the lines in the element
                element.getChildren().forEach(this::drawInnerGroup);
            }
            else
            {
                drawLine(element);
            }
        });
        Rubberband rubberband = iModel.getRubberband();
        if(rubberband != null)
        {
            // set stroke to red like in the picture
            gc.setStroke(Color.RED);

            // set the dashes
            gc.setLineDashes(8, 5);

            // draw the rectangle
            gc.strokeRect(rubberband.getX(), rubberband.getY(), rubberband.getWidth(), rubberband.getHeight());

            // set stroke back to normal
            gc.setLineDashes();
            gc.setLineWidth(1);
        }
    }

    // method to draw inner groups
    private void drawInnerGroup(Groupable group)
    {
        if(!group.isGroup())
        {
            // if current element is not a group then call drawLine
            drawLine(group);
        }
        else
        {
            group.getChildren().forEach(this::drawInnerGroup);
        }
    }

    // method to draw a line
    private void drawLine(Groupable line)
    {
        DLine castToLine = (DLine) line;
        if(iModel.isOn(castToLine))
        {
            // first set line to be 10px wide
            gc.setLineWidth(10);
            // stoke a grap line
            gc.setStroke(Color.GRAY);
            gc.strokeLine(castToLine.getX1(), castToLine.getY1(), castToLine.getX2(), castToLine.getY2());
            // change line back to 1px wide so that our normal line is drawn above this one
            gc.setLineWidth(1);
        }
        // change stroke to pink if current line is selected, otherwise keep it as dark purple
        if(iModel.isInSelection(line))  // this should return false even if line is in selection but as part of a group
        {
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.GRAY);
            // draw handles on each endpoints
            gc.fillOval(castToLine.getX1() - model.getCornerRadius(), castToLine.getY1() - model.getCornerRadius(),
                    model.getCornerRadius() * 2, model.getCornerRadius() * 2);  // because we want our oval to expand from the center
            gc.strokeOval(castToLine.getX1() - model.getCornerRadius(), castToLine.getY1() - model.getCornerRadius(),
                    model.getCornerRadius() * 2, model.getCornerRadius() * 2);

            gc.setFill(Color.LIGHTGREEN);
            gc.fillOval(castToLine.getX2() - model.getCornerRadius(), castToLine.getY2() - model.getCornerRadius(),
                    model.getCornerRadius() * 2, model.getCornerRadius() * 2);
            gc.strokeOval(castToLine.getX2() - model.getCornerRadius(), castToLine.getY2() - model.getCornerRadius(),
                    model.getCornerRadius() * 2, model.getCornerRadius() * 2);

            gc.setStroke(Color.PINK);
        }
        else
        {
//            gc.setStroke(Color.web("#221C35"));  // dark purple color code that I found online on colorcodes.io
            gc.setStroke(Color.PURPLE); // purple looks better the dark purple one is hard to differentiate between lines and grids
        }
        gc.strokeLine(castToLine.getX1(), castToLine.getY1(), castToLine.getX2(), castToLine.getY2());
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
        // can call the draw method once the iModel is set assuming model is set before setting up iModel
        // because we will need both for drawing view and trying to keep draw method private to view
        this.draw();
    }

    // a method to setup events on the controller
    public void setupEvents(AppController controller)
    {
        // setup events in the controller
        this.setOnMousePressed(controller::handlePressed);
        this.setOnMouseDragged(controller::handleDragged);
        this.setOnMouseReleased(controller::handleReleased);
        this.setOnMouseMoved(controller::handleMoved);
        this.setOnKeyPressed(controller::handleKeyPressed);
    }

    @Override
    public void modelChanged() {this.draw();}
}
