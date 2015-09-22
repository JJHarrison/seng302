package com.thirstygoat.kiqo.gui.scrumBoard;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.delete.DeleteEffortCommand;
import com.thirstygoat.kiqo.gui.customCells.EffortListCell;
import com.thirstygoat.kiqo.gui.customCells.ImpedimentListCell;
import com.thirstygoat.kiqo.gui.effort.EffortViewModel;
import com.thirstygoat.kiqo.gui.nodes.EffortLoggingPopover;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelFilteredListSelectionView;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Impediment;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;


/**
 * Created by james on 19/08/15.
 */
public class TaskCardExpandedView implements FxmlView<TaskCardViewModel>, Initializable {

    @InjectViewModel
    TaskCardViewModel viewModel;
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private GoatLabelFilteredListSelectionView<Person> assignedPeopleLabel;
    @FXML
    private GoatLabelTextField estimatedHoursLabel;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private TextField commentTextField;
    @FXML
    private VBox detailsVBox;
    @FXML
    private VBox loggingVBox;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Button loggingButton;
    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private ToggleButton loggingToggleButton;
    @FXML
    private HBox buttonsHBox;
    @FXML
    private CheckBox blockedCheckBox;
    @FXML
    private Button addImpedimentButton;
    @FXML
    private Button removeImpedimentButton;
    @FXML
    private TextField impedimentTextField;
    @FXML
    private ListView<Impediment> impedimentsListView;
    @FXML
    private ListView<Effort> loggedEffortListView;
    @FXML
    private TableColumn<Effort, Person> personTableColumn;
    @FXML
    private TableColumn<Effort, String> commentTableColumn;
    @FXML
    private TableColumn<Effort, Float> durationTableColumn;
    @FXML
    private TableColumn<Effort, LocalDateTime> endTimeTableColumn;
    @FXML
    private VBox buttonsView;
    @FXML
    private Button newEffortButton;
    @FXML
    private Button deleteEffortIcon;
    @FXML
    private Button editEffortButton;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSegmentedButton();
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        FxUtils.initGoatLabel(estimatedHoursLabel, viewModel, viewModel.estimateProperty(), viewModel.estimateValidation());  //TODO fix the parsing error when "-" is typed into the box
        initBlockedCheckBox();
        initImpediments();
        initEffortLogging();

        FxUtils.initGoatLabel(assignedPeopleLabel, viewModel, viewModel.assignees(), viewModel.eligibleAssignees());
        assignedPeopleLabel.getEditField().setStringPropertyCallback(Person::shortNameProperty);
        assignedPeopleLabel.getEditField().maxWidthProperty().set(mainAnchorPane.prefWidthProperty().get() - 50);
        assignedPeopleLabel.getDisplayLabel().maxWidthProperty().set(mainAnchorPane.prefWidthProperty().get() - 50);



        //TODO add the assigned people to form after creating a new GoatLabel for filtered selection thingy
        FxUtils.initGoatLabel(assignedPeopleLabel, viewModel, viewModel.assignees(), viewModel.eligableAssignedPeople());
//        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.getTask().get().getAssignedPeople(), viewModel.teamValidation());
    }


    private void initEffortLogging() {
//        EffortViewModel effortViewModel = new EffortViewModel();
//        effortViewModel.taskProperty().bind(viewModel.getTask());
//        effortViewModel.organisationProperty().bind(viewModel.organisationProperty());

        newEffortButton.setOnAction(event -> {
            EffortViewModel e = new EffortViewModel();
            e.load(null, viewModel.organisationProperty().get());
            e.taskProperty().setValue(viewModel.getTask().getValue());
            PopOver p = new EffortLoggingPopover(e);
            Platform.runLater(() -> p.show(buttonsView));
        });

        editEffortButton.setOnAction(event -> {
            EffortViewModel e = new EffortViewModel();
            e.load(loggedEffortListView.getSelectionModel().getSelectedItem(), viewModel.organisationProperty().get());
            e.taskProperty().setValue(viewModel.getTask().getValue());
            PopOver p = new EffortLoggingPopover(e);
            Platform.runLater(() -> p.show(buttonsView));
        });

        deleteEffortIcon.setOnAction(event -> {
            Command command = new DeleteEffortCommand(loggedEffortListView.getSelectionModel().getSelectedItem(), viewModel.getTask().get());
            viewModel.loggedEffort().remove(loggedEffortListView.getSelectionModel().getSelectedItem());
            UndoManager.getUndoManager().doCommand(command);
        });

        loggedEffortListView.itemsProperty().bind(viewModel.loggedEffort());
        loggedEffortListView.setCellFactory((lv) -> new EffortListCell());

        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());
        FxUtils.initGoatLabel(estimatedHoursLabel, viewModel, viewModel.estimateProperty(),
                viewModel.estimateValidation());  //TODO fix the parsing error when "-" is typed into the box
        blockedCheckBox.selectedProperty().bindBidirectional(viewModel.blockedProperty());
        blockedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> viewModel.commitEdit());
    }

    private void initImpediments() {
        impedimentsListView.setCellFactory(param -> new ImpedimentListCell());
        impedimentsListView.setItems(viewModel.impedimentsObservableList());
        impedimentTextField.textProperty().bindBidirectional(viewModel.textFieldString());
        impedimentTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                viewModel.addImpediment();
            }
        });

        addImpedimentButton.setOnAction(e -> viewModel.addImpediment());
        addImpedimentButton.disableProperty().bind(Bindings.isEmpty(impedimentTextField.textProperty()));
        removeImpedimentButton.setOnAction(e -> {
            int place = impedimentsListView.getSelectionModel().getSelectedIndex();
            viewModel.removeImpediment(impedimentsListView.getSelectionModel().getSelectedItem());
            impedimentsListView.getSelectionModel().select((place > 0) ? place - 1 : 0);

        });
        removeImpedimentButton.disableProperty().bind(
                Bindings.isNull(impedimentsListView.getSelectionModel().selectedItemProperty()));
    }

    private void initBlockedCheckBox() {
        blockedCheckBox.selectedProperty().bindBidirectional(viewModel.blockedProperty());
        blockedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.commitEdit();
        });
    }

    private void initSegmentedButton() {
        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else {
                if (newValue == detailsToggleButton) {
                    showNode(detailsVBox);
                } else if (newValue == loggingToggleButton) {
                    showNode(loggingVBox);
                }
            }
        });
    }


    private void showNode(Node node) {
        for (Node node1 : mainAnchorPane.getChildren()) {
            node1.setManaged(false);
            node1.setVisible(false);
        }
        node.setManaged(true);
        node.setVisible(true);
    }



}
