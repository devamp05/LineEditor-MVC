package com.example.asn4;

import javafx.scene.layout.StackPane;

public class MainUI extends StackPane {
    // setup MVC architecture with private attributes
    private DView view;
    private AppController controller;
    private LineModel model;
    private InteractionModel iModel;
    private double fixedViewWidth, fixedViewHeight;
    public MainUI()
    {
        // setup fixed size of DView as described in the software requirements
        fixedViewWidth = 1000;
        fixedViewHeight = 800;

        // initialize our view, controller, model and iModel
        view = new DView(fixedViewWidth, fixedViewHeight);
        controller = new AppController();
        model = new LineModel();
        iModel = new InteractionModel();

        // setup the MVC architecture
        view.setModel(model);
        view.setiModel(iModel);
        view.setupEvents(controller);

        model.addSubscriber(view);
        iModel.addSubscriber(view);

        controller.setModel(model);
        controller.setiModel(iModel);

        // add view to our pane
        this.getChildren().add(view);
    }
}
