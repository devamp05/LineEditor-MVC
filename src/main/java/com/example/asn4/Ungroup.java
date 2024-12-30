package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class Ungroup implements DCommand{
    // almost same attributes as group
    private LineModel model;
    private InteractionModel iModel;    // also take iModel for this one to keep selection persistent
    private DGroup group;

    public Ungroup(LineModel model, InteractionModel iModel) {
        this.model = model;
        this.iModel = iModel;

        // get all the groups from the selection
//        iModel.getSelection().forEach(item
        for(Groupable item: iModel.getSelection())
        {
            if(item.isGroup())
            {
                group = (DGroup) item;
                break;
            }
        }
    }
    @Override
    public void doit() {
        List<Groupable> ungrouped = model.ungroup(group);
        if(ungrouped != null)
        {
            // also need to update the selection after Grouping and ungrouping
            iModel.clearSelection();
            iModel.addToSelection(ungrouped);
        }
    }

    @Override
    public void undo() {
        // group each group again reuse old references
        model.group(group);
        // also need to update the selection after Grouping and ungrouping
        iModel.clearSelection();
        iModel.addToSelection(group);
    }
}
