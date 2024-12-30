package com.example.asn4;

import java.util.List;

public class ScaleItems implements DCommand{
    // private model like for all the commands, a list of elements to scale and scaleFactor for scaling
    private LineModel model;
    private List<Groupable> elements;
    private double scaleFactor;

    private InteractionModel iModel;    // imodel to preserve selection

    public ScaleItems(LineModel model, InteractionModel iModel, List<Groupable> elements, double scaleFactor) {
        this.model = model;
        this.elements = elements;
        this.scaleFactor = scaleFactor;
        this.iModel = iModel;
    }

    @Override
    public void doit() {
        elements.forEach(element -> {   // all the scaling elements should be in the selection
            if(!iModel.isInSelection(element))
            {
                iModel.addToSelection(element);
            }
        });
        model.scale(elements, scaleFactor);
    }

    @Override
    public void undo() {
        elements.forEach(element -> {   // all the scaling elements should be in the selection
            if(!iModel.isInSelection(element))
            {
                iModel.addToSelection(element);
            }
        });
        model.scale(elements, 1/scaleFactor); // divide 1 by the scaleFactor to reverse scaling effect
    }
}
