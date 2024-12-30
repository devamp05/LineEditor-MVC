package com.example.asn4;

import java.util.List;

public class MoveItems implements DCommand{
    // private model like for all the commands, a list of elements to move and dX, dY for move
    private LineModel model;
    private List<Groupable> elements;
    private double dX, dY;

    private InteractionModel iModel;    // imodel to preserve selection

    public MoveItems(LineModel model, InteractionModel iModel, List<Groupable> elements, double dX, double dY) {
        this.model = model;
        this.elements = elements;
        this.dX = dX;
        this.dY = dY;
        this.iModel = iModel;
    }

    @Override
    public void doit() {
        elements.forEach(element -> {   // all the moving elements should be in the selection
            if(!iModel.isInSelection(element))
            {
                iModel.addToSelection(element);
            }
        });
        model.move(elements, dX, dY);
    }

    @Override
    public void undo() {
        elements.forEach(element -> {
            if(!iModel.isInSelection(element))
            {
                iModel.addToSelection(element);
            }
        });
        model.move(elements, -dX, -dY); // move back for undo
    }
}
