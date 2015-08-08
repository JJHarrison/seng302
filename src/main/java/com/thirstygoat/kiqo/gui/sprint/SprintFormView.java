package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.gui.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintFormView implements FxmlView<SprintFormViewModel>, Initializable {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField goalTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField releaseTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField teamTextField;
    @FXML
    private TextField backlogTextField;
    @FXML
    private GoatFilteredListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @InjectViewModel
    private SprintFormViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        goalTextField.textProperty().bindBidirectional(viewModel.goalProperty());
        startDatePicker.valueProperty().bindBidirectional(viewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
        releaseTextField.textProperty().bindBidirectional(viewModel.releaseShortNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        teamTextField.textProperty().bindBidirectional(viewModel.teamShortNameProperty());
        backlogTextField.textProperty().bindBidirectional(viewModel.backlogShortNameProperty());

        storySelectionView.setHeader(new Label("Stories in Sprint:"));
        storySelectionView.setSourceItems(viewModel.sourceStories());
        storySelectionView.setTargetItems(viewModel.stories());

        viewModel.sourceStories().addListener((ListChangeListener<Story>) c -> {
            storySelectionView.setSourceItems(viewModel.sourceStories());
        });

        viewModel.stories().addListener((ListChangeListener<Story>) c -> {
            storySelectionView.setTargetItems(viewModel.stories());
        });

        releaseTextField.disableProperty().bind(viewModel.releaseEditableProperty().not());
        okButton.disableProperty().bind(viewModel.validProperty().not());
        
        FxUtils.setTextFieldSuggester(backlogTextField, viewModel.getBacklogsSupplier());
        FxUtils.setTextFieldSuggester(teamTextField, viewModel.getTeamsSupplier());
        FxUtils.setTextFieldSuggester(releaseTextField, viewModel.getReleasesSupplier());

        viewModel.setListeners();

        Platform.runLater(() -> {
            goalTextField.requestFocus();
            // do this in here to ensure textFields definitely exist
            attachValidators();
        });
    }

    private void attachValidators() {
        ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
        validationVisualizer.initVisualization(viewModel.goalValidation(), goalTextField, true);
        validationVisualizer.initVisualization(viewModel.releaseValidation(), releaseTextField, true);
        validationVisualizer.initVisualization(viewModel.startDateValidation(), startDatePicker, true);
        validationVisualizer.initVisualization(viewModel.endDateValidation(), endDatePicker, true);
        validationVisualizer.initVisualization(viewModel.teamValidation(), teamTextField, true);
        validationVisualizer.initVisualization(viewModel.backlogValidation(), backlogTextField, true);
    }

    public void okAction() {
        viewModel.okAction();
    }

    public void cancelAction() {
        viewModel.cancelAction();
    }
}