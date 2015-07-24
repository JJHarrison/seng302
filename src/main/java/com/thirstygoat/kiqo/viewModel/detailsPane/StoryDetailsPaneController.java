package com.thirstygoat.kiqo.viewModel.detailsPane;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.AcceptanceCriteriaListCell;
import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.*;

public class StoryDetailsPaneController implements Initializable, IDetailsPaneController<Story> {

    private MainController mainController;
    private Story story;
    private Map<State, Image> images;
    private ChangeListener<Boolean> isReadyListener;
    
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label creatorLabel;
    @FXML
    private Label dependenciesLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label storyEstimateSliderLabel;
    @FXML
    private Label storyScaleLabel;
    @FXML
    private ListView<AcceptanceCriteria> acListView;
    @FXML
    private Slider storyEstimateSlider;
    @FXML
    private Button addACButton;
    @FXML
    private Button removeACButton;
    @FXML
    private Button editACButton;
    @FXML
    private CheckBox isReadyCheckBox;
    @FXML
    private Hyperlink readyWhy;
    @FXML
    private Hyperlink estimateWhy;


    @Override
    public void showDetails(final Story story) {
        this.story = story;
        if (story != null) {
            longNameLabel.textProperty().bind(story.longNameProperty());
            shortNameLabel.textProperty().bind(story.shortNameProperty());
            descriptionLabel.textProperty().bind(story.descriptionProperty());
            // This is some seriously cool binding
            // Binding to a property of a property
            creatorLabel.textProperty().bind(Bindings.select(story.creatorProperty(), "shortName"));
            dependenciesLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(story.observableDependencies()));
            priorityLabel.textProperty().bind(Bindings.convert(story.priorityProperty()));

            // need to unbind in case the selected story has changed and therefore we won't try and bind to a bound property
            storyScaleLabel.textProperty().unbind();
            storyScaleLabel.textProperty().bind(story.scaleProperty().asString());
            setScale();

            if (isReadyListener != null) {
                isReadyCheckBox.selectedProperty().removeListener(isReadyListener);
            }
            isReadyListener = (observable, oldValue, newValue) -> {
                if (story.getIsReady() != newValue) {
                    Command<?> command = new EditCommand<>(story, "isReady", newValue);
                    UndoManager.getUndoManager().doCommand(command);
                }
            };
            isReadyCheckBox.setSelected(story.getIsReady());
            isReadyCheckBox.selectedProperty().addListener(isReadyListener);
        } else {
            longNameLabel.textProperty().unbind();
            shortNameLabel.textProperty().unbind();
            descriptionLabel.textProperty().unbind();
            creatorLabel.textProperty().unbind();
            priorityLabel.textProperty().unbind();

            longNameLabel.setText(null);
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            creatorLabel.setText(null);
            priorityLabel.setText(null);
            storyEstimateSliderLabel.setText(null);
            isReadyCheckBox.setSelected(false); // May not be needed
            isReadyCheckBox.selectedProperty().removeListener(isReadyListener);
        }

        acListView.setCellFactory(param -> new AcceptanceCriteriaListCell(param, images));
        
        removeACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        acListView.setItems(story.getAcceptanceCriteria());

        addACButton.setOnAction(event -> mainController.createAC());
        removeACButton.setOnAction(event -> deleteAC());
        editACButton.setOnAction(event -> mainController.editAC(acListView.getSelectionModel().getSelectedItem()));

        // Story must have at least one AC
        // Story must have non-null estimate
        // Story must be in a backlog

        BooleanBinding nullBacklogBinding = Bindings.isNull(story.backlogProperty());
        BooleanBinding emptyACBinding = Bindings.size(story.getAcceptanceCriteria()).isEqualTo(0);
        BooleanBinding noEstimateBinding = Bindings.equal(story.estimateProperty(), 0);

        // Bind the disable property
        isReadyCheckBox.disableProperty().bind(nullBacklogBinding.or(emptyACBinding).or(noEstimateBinding));
        readyWhy.visibleProperty().bind(isReadyCheckBox.disabledProperty());

        setIsReadyCheckBoxInfo();
        setEstimateHyperlink();

        // Disable storyEstimateSlider if there are no acceptance criteria.
        storyEstimateSlider.disableProperty().bind(Bindings.isEmpty(acListView.getItems()));
    }

    private void setIsReadyCheckBoxInfo() {
        final Text bulletA = new Text("○ ");
        final Text bulletB = new Text("○ ");
        final Text bulletC = new Text("○ ");

        final Text belongToABacklog = new Text("belong to a backlog\n");
        final Text beEstimated = new Text("be estimated\n");
        final Text haveAcceptanceCriteria = new Text("have Acceptance Criteria");

        belongToABacklog.strikethroughProperty().bind(Bindings.isNotNull(story.backlogProperty()));
        beEstimated.strikethroughProperty().bind(Bindings.notEqual(0, story.estimateProperty()));
        haveAcceptanceCriteria.strikethroughProperty().bind(Bindings.isNotEmpty(story.getAcceptanceCriteria()));

        TextFlow tf = new TextFlow(
                new Text("To mark this Story as Ready, it must:\n\n"),
                bulletA, belongToABacklog,
                bulletB, beEstimated,
                bulletC, haveAcceptanceCriteria);

        tf.setPadding(new Insets(10));
        PopOver readyWhyPopOver = new PopOver(tf);
        readyWhyPopOver.setDetachable(false);

        readyWhy.setOnAction((e) -> readyWhyPopOver.show(readyWhy));
        readyWhy.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                readyWhyPopOver.hide();
            }
        });
    }

    private void deleteAC() {
        Command<?> command;
        if (acListView.getSelectionModel().getSelectedItems().size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command<?>> commands = new ArrayList<>();
            for (AcceptanceCriteria ac : acListView.getSelectionModel().getSelectedItems()) {
                commands.add(new DeleteAcceptanceCriteriaCommand(ac, story));
            }
            command = new CompoundCommand("Delete Acceptance Criteria", commands);
        } else {
            final AcceptanceCriteria acceptanceCriteria = acListView.getSelectionModel().getSelectedItem();
            command = new DeleteAcceptanceCriteriaCommand(acceptanceCriteria, story);
        }

        // Check if they are deleting all of the ACs, and if the story is marked as ready
        // If so, then the command executed must mark the story as no longer ready
        if (acListView.getSelectionModel().getSelectedItems().size() == acListView.getItems().size() &&
                story.getIsReady()) {
            List<Command<?>> changes = new ArrayList<>();
            changes.add(command);
            changes.add(new EditCommand<>(story, "isReady", false));
            command = new CompoundCommand("Delete AC", changes);
        }

        mainController.doCommand(command);
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        images = new HashMap<>();
        final int IMAGE_SIZE = 20;
        final ClassLoader classLoader = getClass().getClassLoader();
        images.put(State.ACCEPTED, new Image(classLoader.getResourceAsStream("images/acceptedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        images.put(State.REJECTED, new Image(classLoader.getResourceAsStream("images/rejectedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        images.put(State.NEITHER, new Image(classLoader.getResourceAsStream("images/noState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        initSlider();
        acListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initSlider() {
        storyEstimateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // set the sliders label to the estimate string if the value > 0
            if (newValue.intValue() > 0) {
                storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(newValue.intValue())-1]);
            } else {
                storyEstimateSliderLabel.setText("-");
            }
        });

        storyEstimateSlider.setOnMouseReleased(event -> {
            if (story.getEstimate() != storyEstimateSlider.getValue()) {
                List<Command<?>> commands = new ArrayList<>();
                if (((int)storyEstimateSlider.getValue()) == 0 && story.getIsReady()) {
                    commands.add(new EditCommand<>(story, "isReady", false));
                }
                commands.add(new EditCommand<>(story, "estimate", (int) storyEstimateSlider.getValue()));
                CompoundCommand command = new CompoundCommand("Edit Estimation", commands);
                UndoManager.getUndoManager().doCommand(command);
            }
        });
    }

    private void setEstimateHyperlink() {
        Text bulletA = new Text("○ ");
        Text haveAcceptanceCriteria = new Text("have Acceptance Criteria");
        haveAcceptanceCriteria.strikethroughProperty().bind(Bindings.isNotEmpty(story.getAcceptanceCriteria()));

        TextFlow tf = new TextFlow(
                new Text("To estimate this Story, it must:\n\n"),
                bulletA, haveAcceptanceCriteria);

        tf.setPadding(new Insets(10));
        PopOver estimateWhyPopOver = new PopOver(tf);
        estimateWhyPopOver.setDetachable(false);

        estimateWhy.setOnAction((e) -> estimateWhyPopOver.show(estimateWhy));
        estimateWhy.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                estimateWhyPopOver.hide();
            }
        });

        estimateWhy.visibleProperty().bind(storyEstimateSlider.disabledProperty());
        estimateWhy.managedProperty().bind(storyEstimateSlider.disabledProperty());

        storyEstimateSlider.visibleProperty().bind(Bindings.not(storyEstimateSlider.disabledProperty()));
        storyEstimateSlider.managedProperty().bind(Bindings.not(storyEstimateSlider.disabledProperty()));

        storyEstimateSliderLabel.visibleProperty().bind(Bindings.not(estimateWhy.visibleProperty()));
        storyEstimateSliderLabel.managedProperty().bind(Bindings.not(estimateWhy.visibleProperty()));
    }

    private void setScale() {
        if (story.getScale() == null) {
            // make slider look tidy
            storyEstimateSlider.setValue(0);
            storyEstimateSlider.setMax(1);
            // bind for if they return to null via revert/undo
            storyEstimateSlider.disableProperty().bind(Bindings.isNull(story.scaleProperty()));
        } else {
            // set initial slider value based off story model
            storyEstimateSlider.setValue(story.getEstimate());
            storyEstimateSlider.setMax(story.getScale().getEstimates().length);
            storyEstimateSlider.disableProperty().unbind();
            storyEstimateSlider.setDisable(false);
            // set initial label for slider, value of 0 is null
            if (storyEstimateSlider.getValue() > 0) {
                storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(int) storyEstimateSlider.getValue() - 1]);
            }
        }
        // listener for if the scale changes within the story
        story.scaleProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null) {
                if (storyEstimateSlider.getValue() > story.getScale().getEstimates().length) {
                    // set new value to be new max if slider val > new max
                    storyEstimateSlider.setValue(story.getScale().getEstimates().length);
                }
                if (storyEstimateSlider.getValue() > 0) {
                    // if new slider value is greater than 0 (null value for est) set label
                    storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(int) storyEstimateSlider.getValue() - 1]);
                }
                storyEstimateSlider.setMax(story.getScale().getEstimates().length);
            } else {
                storyEstimateSlider.setValue(0);
                storyEstimateSlider.setMax(1);
            }
        });
        // set the slider to match what the model has (for redo/undo stuff)
        story.estimateProperty().addListener((observable, oldValue, newValue) -> {
            storyEstimateSlider.setValue(newValue.intValue());
        });
    }
}