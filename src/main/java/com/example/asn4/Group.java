package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class Group implements DCommand{
    // private model like for all the commands
    private LineModel model;
    private InteractionModel iModel;    // also take iModel for this one to keep selection persistent
    private List<Groupable> selection;
    private DGroup newGroup;

    public Group(LineModel model, InteractionModel iModel) {
        this.model = model;
        this.iModel = iModel;
        this.selection = new ArrayList<>();
        iModel.getSelection().forEach(item -> selection.add(item));
        newGroup = new DGroup(selection);
    }

    @Override
    public void doit() {
        model.group(newGroup);
        // also need to update the selection after Grouping and ungrouping
        iModel.clearSelection();
        iModel.addToSelection(newGroup);
    }

    @Override
    public void undo() {
        model.ungroup(newGroup);

        // also need to update the selection after Grouping and ungrouping
        iModel.clearSelection();
        iModel.addToSelection(selection);
    }
}
