package com.thirstygoat.kiqo.gui.sprint;
import com.thirstygoat.kiqo.util.StringConverters;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import com.thirstygoat.kiqo.gui.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.model.Story;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;

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
    private GoatListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();

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

        attachValidators();

        okButton.disableProperty().bind(viewModel.validProperty().not());
        
        // TODO register autocompleting textfields
        // FormController.setTextFieldSuggester

        Platform.runLater(nameTextField::requestFocus);
    }
    


    public void attachValidators() {
        validationVisualizer = new ControlsFxVisualizer();
        validationVisualizer.initVisualization(viewModel.longNameValidation(), nameTextField, true);
        validationVisualizer.initVisualization(viewModel.goalValidation(), goalTextField);
        validationVisualizer.initVisualization(viewModel.releaseValidation(), releaseTextField);
        validationVisualizer.initVisualization(viewModel.startDateValidation(), startDatePicker, true);
        validationVisualizer.initVisualization(viewModel.endDateValidation(), endDatePicker, true);
        validationVisualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextField);
        validationVisualizer.initVisualization(viewModel.teamValidation(), teamTextField, true);
        validationVisualizer.initVisualization(viewModel.backlogValidation(), backlogTextField, true);
    }

    public void okAction() {
        viewModel.okAction();
    }

    public void cancelAction() {
        viewModel.cancelAction();
    }

    public SprintFormViewModel getViewModel() {
        return viewModel;
    }
}

