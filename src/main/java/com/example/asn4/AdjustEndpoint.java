package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class AdjustEndpoint implements DCommand{
    // a reference of the model
    private LineModel model;
    private DLine line;
    private Endpoint prevEndpoint;
    private double newX, newY;

    // private iModel to preserve selection
    private InteractionModel iModel;

    private ArrayList<Groupable> selection;

    public AdjustEndpoint(LineModel model, InteractionModel iModel, DLine line, Endpoint prevEndpoint, double newX, double newY) {
        this.model = model;
        this.iModel = iModel;
        this.line = line;
        this.prevEndpoint = prevEndpoint;
        this.newX = newX;
        this.newY = newY;
        this.selection = new ArrayList<>();
        iModel.getSelection().forEach(item -> selection.add(item));
    }

    @Override
    public void doit() {
        // preserve selection
        iModel.clearSelection();
        iModel.addToSelection(selection);
        model.resizeLine(line, prevEndpoint, newX, newY);
    }

    @Override
    public void undo() {
        // preserve selection
        iModel.clearSelection();
        iModel.addToSelection(selection);
        model.resizeLine(line, new Endpoint(newX, newY), prevEndpoint.getX(), prevEndpoint.getY());
    }
}
