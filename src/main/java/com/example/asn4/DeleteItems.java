package com.example.asn4;

import java.util.List;

public class DeleteItems implements DCommand{
    // a reference of the model
    private LineModel model;

    // and a reference to the items we delete
    private List<Groupable> items;

    // private iModel to preserve selection
    private InteractionModel iModel;

    public DeleteItems(LineModel model, InteractionModel iModel, List<Groupable> items) {
        this.model = model;
        this.iModel = iModel;
        this.items = items;
    }

    @Override
    public void doit() {
        model.remove(items);
        iModel.clearSelection();
    }

    @Override
    public void undo() {
        model.add(items);
        iModel.addToSelection(items);
    }
}
