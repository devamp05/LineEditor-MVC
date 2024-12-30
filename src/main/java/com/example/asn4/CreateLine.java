package com.example.asn4;

public class CreateLine implements DCommand{
    // I think we will have to keep a reference of the model like we have done in lab
    private LineModel model;

    // and I think we will also need a reference to the line object we added to the model to undo it
    private DLine line;

    // I think we will also need the iModel to preserve selection like in the videos
    private InteractionModel iModel;

    public CreateLine(LineModel model, InteractionModel iModel, double x1, double y1, double x2, double y2) {
        this.model = model;
        this.iModel = iModel;
        line = new DLine(x1, y1, x2, y2, model.getCornerRadius());
    }

    @Override
    public void doit() {
        model.add(line);
        iModel.clearSelection();
        iModel.addToSelection(line);
    }

    @Override
    public void undo() {
        model.remove(line);
    }
}
