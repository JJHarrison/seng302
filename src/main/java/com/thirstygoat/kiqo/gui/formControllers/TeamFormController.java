package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreateTeamCommand;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by james on 27/03/15.
 */
public class TeamFormController extends FormController<Team> {

    private static final Logger LOGGER = Logger.getLogger(TeamFormController.class.getName());
    private final ArrayList<Person> devTeam = new ArrayList<>();
    private final ListProperty<Person> selectedTeamMembers = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Person> eligiblePeople = new SimpleListProperty<>(FXCollections.observableArrayList());
	private final ArrayList<RadioButton> poRadioButtons = new ArrayList<>();
    private final ArrayList<RadioButton> smRadioButtons = new ArrayList<>();
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Organisation organisation;
    private Team team;
    private Command command;
    private boolean valid = false;
    private Person scrumMaster;
    private Person productOwner;
    // Begin FXML Injections
    @FXML
    private Label heading;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private GoatFilteredListSelectionView<Person> peopleListSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peopleListSelectionView.bindAllItems(eligiblePeople);
        peopleListSelectionView.bindSelectedItems(selectedTeamMembers);
        peopleListSelectionView.setStringPropertyCallback(Person::shortNameProperty);
        
        setButtonHandlers();
        Utilities.initShortNameLengthLimiter(shortNameTextField.textProperty());
        setPrompts();

        Platform.runLater(() -> {
            // wait for textfields to exist
            setValidationSupport();
            shortNameTextField.requestFocus();
        });
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                Utilities.shortnameIsUnique(shortNameTextField.getText(), team, organisation.getTeams());

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Name must be unique and not empty"));

        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Then invalid, disable ok button
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under " + Utilities.SHORT_NAME_MAX_LENGTH + " characters and unique.");
        descriptionTextField.setPromptText("Describe this team.");
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

    private void setCommand() {
        final ArrayList<Person> teamMembers = new ArrayList<>();
        teamMembers.addAll(selectedTeamMembers);
        if (team == null) {
            // create command
            team = new Team(shortNameTextField.getText(), descriptionTextField.getText(), teamMembers);
            team.setProductOwner(productOwner);
            team.setScrumMaster(scrumMaster);
            team.setDevTeam(devTeam);
            command = new CreateTeamCommand(team, organisation);
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(team.getShortName())) {
                changes.add(new EditCommand<>(team, "shortName", shortNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(team.getDescription())) {
                changes.add(new EditCommand<>(team, "description", descriptionTextField.getText()));
            }
            if (!(teamMembers.containsAll(team.getTeamMembers()) && team.getTeamMembers().containsAll(teamMembers))) {
                changes.add(new EditCommand<>(team, "teamMembers", teamMembers));
            }

            if (productOwner != team.getProductOwner()) {
                changes.add(new EditCommand<>(team, "productOwner", productOwner));
            }

            if (scrumMaster != team.getScrumMaster()) {
                changes.add(new EditCommand<>(team, "scrumMaster", scrumMaster));
            }

            if (!(devTeam.containsAll(team.observableDevTeam()) && team.observableDevTeam().containsAll(devTeam))) {
                changes.add(new EditCommand<>(team, "devTeam", devTeam));
            }

            final ArrayList<Person> newMembers = new ArrayList<>(teamMembers);
            newMembers.removeAll(team.observableTeamMembers());
            final ArrayList<Person> oldMembers = new ArrayList<>(team.observableTeamMembers());
            oldMembers.removeAll(teamMembers);

            // Loop through all the new members and add a command to set their team
            // Set the person's team field to this team
            changes.addAll(newMembers.stream().map(person -> new EditCommand<>(person, "team", team))
                    .collect(Collectors.toList()));

            // Loop through all the old members and add a command to remove their team
            // Set the person's team field to null, since they're no longer in the team
            changes.addAll(oldMembers.stream().map(person -> new EditCommand<>(person, "team", null))
                    .collect(Collectors.toList()));

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Team", changes);
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    private void setListSelectionViewSettings() {
        final BorderPane footer = new BorderPane();
        footer.setPadding(new Insets(0, 15, 0, 0));
        HBox.setHgrow(footer, Priority.ALWAYS);

        final Text poText = new Text(organisation.getPoSkill().getShortName().substring(0, 2) + " ");
        poText.setFill(Color.BLUE);
        final Text smText = new Text(organisation.getSmSkill().getShortName().substring(0, 2) + " ");
        smText.setFill(Color.RED);
        final Text devText = new Text("Dev ");
        devText.setFill(Color.GREEN);
        final Text otherText = new Text("Other");
        final TextFlow legend = new TextFlow(poText, smText, devText, otherText);

        footer.setRight(legend);
        peopleListSelectionView.setFooter(footer);

        // Set the custom cell factory for the skills lists
        // Thank GoatListSelectionView for this fabulous method
        peopleListSelectionView.setTargetCellGraphicFactory(getTargetCell());

        // Set change listener on target list view
        selectedTeamMembers.addListener((ListChangeListener<Person>) c -> {
            c.next();
            for (final Person person : c.getRemoved()) {
                // Remove person from role of PO/SM/DevTeam if applicable
                if (productOwner == person) {
                    productOwner = null;
                    TeamFormController.LOGGER.log(Level.FINE, "removing %s from po role", person);
                } else if (scrumMaster == person) {
                    scrumMaster = null;
                    TeamFormController.LOGGER.log(Level.FINE, "removing %s from sm role", person);
                } else if (devTeam.contains(person)) {
                    devTeam.remove(person);
                    TeamFormController.LOGGER.log(Level.FINE, "removing %s from devteam role", person);
                }
            }
        });
    }

    private Callback<Person, Node> getTargetCell() {
        return person -> {
            final BorderPane borderPane = new BorderPane();
            final HBox hbox = new HBox();
            borderPane.setRight(hbox);
            final Label label = new Label(person.getShortName());
            borderPane.setLeft(label);

            final ToggleGroup radioGroup = new ToggleGroup();
            final RadioButton radioPo = new RadioButton();
            final RadioButton radioSm = new RadioButton();
            final RadioButton radioDev = new RadioButton();
            final RadioButton radioOther = new RadioButton();
            radioPo.setToggleGroup(radioGroup);
            radioSm.setToggleGroup(radioGroup);
            radioDev.setToggleGroup(radioGroup);
            radioOther.setToggleGroup(radioGroup);
            radioPo.setStyle("-fx-mark-color: blue;");
            radioSm.setStyle("-fx-mark-color: red;");
            radioDev.setStyle("-fx-mark-color: green;");

            // Disable PO/SM Radio Buttons if the person doesn't have
            // the skill
            if (!person.observableSkills().contains(organisation.getPoSkill())) {
                radioPo.setDisable(true);
            }
            if (!person.observableSkills().contains(organisation.getSmSkill())) {
                radioSm.setDisable(true);
            }

            // Select appropriate RadioButton
            if (productOwner == person || (team != null && team.getProductOwner() == person)) {
                radioPo.setSelected(true);
            } else if (scrumMaster == person || (team != null && team.getScrumMaster() == person)) {
                radioSm.setSelected(true);
            } else if ((devTeam != null && devTeam.contains(person)) ||
                    (team != null && team.observableDevTeam() != null && team.observableDevTeam().contains(person))) {
                radioDev.setSelected(true);
            } else {
                radioOther.setSelected(true);
            }

            hbox.getChildren().addAll(radioPo, radioSm, radioDev, radioOther);

            setupRadioPoListener(radioPo, radioOther, person);
            setupRadioSmListener(radioSm, radioOther, person);
            setupRadioDevListener(radioDev, person);

            return borderPane;
        };
    }

    private void setupRadioDevListener(RadioButton radioDev, Person person) {
        radioDev.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                devTeam.add(person);
                TeamFormController.LOGGER.log(Level.FINE, "assigned %s to devteam role", person);
            } else {
                devTeam.remove(person);
                TeamFormController.LOGGER.log(Level.FINE, "removed %s from devteam role", person);
            }
        });
    }

    private void setupRadioSmListener(RadioButton radioSm, RadioButton radioOther, Person person) {
        smRadioButtons.add(radioSm);

        radioSm.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If selected
                scrumMaster = person;
                smRadioButtons.stream().filter(rb -> rb != radioSm).forEach(rb -> rb.setSelected(false));
                TeamFormController.LOGGER.log(Level.FINE, "assigned %s to sm role", person);
            } else {
                if (radioSm.getToggleGroup().getSelectedToggle() == null) {
                    radioOther.setSelected(true);
                }
                if (scrumMaster == person) {
                    scrumMaster = null;
                    TeamFormController.LOGGER.log(Level.FINE, "removed %s from sm role", person);
                }
            }
        });
    }

    private void setupRadioPoListener(RadioButton radioPo, RadioButton radioOther, Person person) {
        poRadioButtons.add(radioPo);

        radioPo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If selected
                productOwner = person;
                poRadioButtons.stream().filter(rb -> rb != radioPo).forEach(rb -> rb.setSelected(false));
                TeamFormController.LOGGER.log(Level.FINE, "assigned %s to po role", person);
            } else {
                if (radioPo.getToggleGroup().getSelectedToggle() == null) {
                    radioOther.setSelected(true);
                }
                if (productOwner == person) {
                    productOwner = null;
                    TeamFormController.LOGGER.log(Level.FINE, "removed %s from po role", person);
                }
            }
        });
    }

    private void populatePeopleListView() {
        // Can't already be in a different team
    	eligiblePeople.clear();
        eligiblePeople.addAll(organisation.getPeople().stream()
        		.filter(person -> person.getTeam() == null || person.getTeam().equals(team))
        		.collect(Collectors.toList()));

        organisation.getPeople().addListener((ListChangeListener<Person>) c -> {
            c.next();
            // We remove people from the sourcePeople that were removed.
            // Note that this shouldn't actually be possible since undo/redo should be disabled
            eligiblePeople.removeAll(c.getRemoved());
            selectedTeamMembers.removeAll(c.getRemoved());
            for (final Person person : c.getAddedSubList()) {
                if (person.getTeam() == team) {
                    selectedTeamMembers.add(person);
                } else {
                    eligiblePeople.add(person);
                }
            }
        });
    }


    @Override
    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    /**
     * Sets the team to be edited and populates fields if applicable
     * @param team Team to be edited
     */
    @Override
    public void populateFields(Team team) {
        this.team = team;

        if (team != null) {
            // We are editing an existing team
            // Populate fields with existing data
            shortNameTextField.setText(team.getShortName());
            descriptionTextField.setText(team.getDescription());
            selectedTeamMembers.addAll(team.observableTeamMembers());
            okButton.setText("Done");

            productOwner = team.getProductOwner();
            scrumMaster = team.getScrumMaster();
            devTeam.addAll(team.observableDevTeam().stream().collect(Collectors.toList()));
        } else {
            okButton.setText("Create Team");
        }

        populatePeopleListView();
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        setListSelectionViewSettings();
    }

    @Override
    public StringProperty headingProperty() {
        return heading.textProperty();
    }
}
