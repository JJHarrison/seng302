package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.gui.viewModel.StoryFormViewModel;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.validation.ValidationSupport;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Carina on 15/05/2015.
 */
public class StoryFormController extends FormController<Story> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private StoryFormViewModel viewModel;
    private Stage stage;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private boolean valid = false;
    private Command<?> command;
    // Begin FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField creatorTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private TextField priorityTextField;
    @FXML
    private ComboBox<Scale> estimationScaleComboBox;
    @FXML
    private GoatListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Hyperlink storyCycleHyperLink;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        viewModel = new StoryFormViewModel();
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        priorityTextField.setText(Integer.toString(Story.DEFAULT_PRIORITY));
        Platform.runLater(longNameTextField::requestFocus);
        setStoryCycleHyperLinkInfo();
        storySelectionView.disableProperty().bind(Bindings.isNull(viewModel.backlogProperty()));
    }

    private void bindFields() {
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        estimationScaleComboBox.disableProperty().bind(Bindings.isNotNull(viewModel.backlogProperty()));
        estimationScaleComboBox.valueProperty().bindBidirectional(viewModel.scaleProperty());
        projectTextField.textProperty().bindBidirectional(viewModel.projectNameProperty());
        priorityTextField.textProperty().bindBidirectional(viewModel.priorityProperty());
        creatorTextField.textProperty().bindBidirectional(viewModel.creatorNameProperty());
        storySelectionView.getTargetListView().itemsProperty().bindBidirectional(viewModel.targetStoriesProperty());
        storySelectionView.getSourceListView().itemsProperty().bindBidirectional(viewModel.sourceStoriesProperty());

        creatorTextField.disableProperty().bind(viewModel.getCreatorEditable().not());
        okButton.disableProperty().bind(viewModel.formValidation().validProperty().not());
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this story.");

        // Populate Estimation Scale ComboBox
        estimationScaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        estimationScaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default
    }

    @Override
    public void populateFields(final Story story) {
        viewModel.setStory(story);
        okButton.setText("Done");
        bindFields();

        if (story != null) {
            // We are editing an existing story
            shortNameModified.set(true);
            projectTextField.setText(story.getProject().getShortName());
        }
    }

    private void setValidationSupport() {
        ValidationVisualizer visualizer = new ControlsFxVisualizer();
        visualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
        visualizer.initVisualization(viewModel.longNameValidation(), longNameTextField, true);
        visualizer.initVisualization(viewModel.creatorValidation(), creatorTextField, true);
        visualizer.initVisualization(viewModel.projectValidation(), projectTextField, true);
        visualizer.initVisualization(viewModel.priorityValidation(), priorityTextField, true);
        visualizer.initVisualization(viewModel.scaleValidation(), estimationScaleComboBox);
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     * @return all fields are valid
     */
    private boolean validate() {
        if (validationSupport.isInvalid()) {
            return false;
        } else {
            valid = true;
        }
        setCommand();
        return true;
    }

    private void setStoryCycleHyperLinkInfo() {
        Label label = new Label();
        label.setText("Only the stories that will not create a dependency loop are shown");
        label.setPadding(new Insets(10, 10, 10, 10));
        PopOver readyWhyPopOver = new PopOver(label);
        readyWhyPopOver.setDetachable(false);

        storyCycleHyperLink.setOnAction((e) -> {
            readyWhyPopOver.show(storyCycleHyperLink);
        });
        storyCycleHyperLink.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                readyWhyPopOver.hide(Duration.millis(0));
            }
        });
    }

    private void setupStoriesList() {
        storySelectionView.setSourceHeader(new Label("Stories Available:"));
        storySelectionView.setTargetHeader(new Label("Dependencies"));

        storySelectionView.setPadding(new Insets(0, 0, 0, 0));

        storySelectionView.setCellFactories(view -> new ListCell<Story>() {
            @Override
            public void updateItem(Story item, boolean empty) {
                super.updateItem(item, empty);
                setText(item != null ? item.getShortName() : null);
            }
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> stage.close());
    }


    /**
     * Sets the listener on the nameTextField so that the shortNameTextField is populated in real time
     * up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Auto populate short name text field
            if (!Objects.equals(newValue, longNameTextField.getText().substring(0,
                    Math.min(longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified.set(true);
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
    }

    @Override
    public boolean isValid() { return valid; }

    @Override
    public Command<?> getCommand() { return viewModel.getCommand(); }

    public void setCommand() {
        viewModel.setCommand();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        viewModel.setOrganisation(organisation);
        setupStoriesList();
        setTextFieldSuggester(creatorTextField, organisation.getPeople());
        setTextFieldSuggester(projectTextField, organisation.getProjects());
        setValidationSupport();
    }
}