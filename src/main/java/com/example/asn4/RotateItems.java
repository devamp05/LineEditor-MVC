package com.example.asn4;

import java.util.List;

public class RotateItems implements DCommand{
    // private model like for all the commands, a list of elements to rotate and direction for rotate
    private LineModel model;
    private List<Groupable> elements;
    private double direction;

    private InteractionModel iModel;    // imodel to preserve selection

    public RotateItems(LineModel model, InteractionModel iModel, List<Groupable> elements, double direction) {
        this.model = model;
        this.elements = elements;
        this.direction = direction;
        this.iModel = iModel;
    }

    @Override
    public void doit() {
        elements.forEach(element -> {   // all the rotating elements should be in the selection
            if(!iModel.isInSelection(element))
            {
                iModel.addToSelection(element);
            }
        });
        model.rotate(elements, direction);
    }

    @Override
    public void undo() {
        elements.forEach(element -> {   // all the rotating elements should be in the selection
            if(!iModel.isInSelection(element))
            {
                iModel.addToSelection(element);
            }
        });
        model.rotate(elements, -direction); // just rotate in the opposite direction
    }
}
