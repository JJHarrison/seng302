package com.thirstygoat.kiqo.viewModel.formControllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreatePersonCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.util.Utilities;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

/**
 * Created by james on 20/03/15.
 */
public class PersonFormController implements Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ObservableList<Skill> targetSkills = FXCollections.observableArrayList();
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Organisation organisation;
    private Person person;
    private boolean valid = false;
    private boolean shortNameModified = false;
    private Command<?> command;
    // FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField userIDTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField departmentTextField;
    @FXML
    private GoatListSelectionView<Skill> skillsSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        setShortNameSuggester();
        Platform.runLater(longNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setValidationSupport() {
        // Validation for short name
        Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                Utilities.shortnameIsUnique(shortNameTextField.getText(), person, organisation.getPeople());

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty."));

        validationSupport.registerValidator(longNameTextField,
                Validator.createEmptyValidator("Long name must not be empty", Severity.ERROR));

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
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Bill Goat");
        descriptionTextField.setPromptText("Describe this awesome person");
        userIDTextField.setPromptText("Identify this person!");
        emailTextField.setPromptText("hello@example.com");
        phoneTextField.setPromptText("A phone number would be good too.");
        departmentTextField.setPromptText("What department do they work for?");
    }

    /**
     * Sets the skills list data and formatting
     */
    private void setUpSkillsList() {
        skillsSelectionView.setSourceHeader(new Label("Skills Available:"));
        skillsSelectionView.setTargetHeader(new Label("Skills Selected:"));

        skillsSelectionView.setPadding(new Insets(0, 0, 0, 0));

        // Set the custom cell factory for the skills lists
        // Thank GoatListSelectionView for this fabulous method
        skillsSelectionView.setCellFactories(view -> {
            final ListCell<Skill> cell = new ListCell<Skill>() {
                @Override
                public void updateItem(Skill item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getShortName() : null);
                }
            };
            return cell;
        });
    }

    private void setSkillsListSelectionViewData() {
        final ObservableList<Skill> sourceSkills = FXCollections.observableArrayList();

        sourceSkills.addAll(organisation.getSkills());
        if (person != null) {
            sourceSkills.removeAll(person.getSkills());
            targetSkills.addAll(person.getSkills());
        }

        skillsSelectionView.getSourceListView().setItems(sourceSkills);
        skillsSelectionView.getTargetListView().setItems(targetSkills);
    }

    /**
     * Sets the TextFields displayed in the dialog to the Person that will be edited.
     * @param person the Person that is loaded
     */
    public void setPerson(final Person person) {
        this.person = person;

        if (person == null) {
            // Then we are creating a new one
            stage.setTitle("Create Person");
            okButton.setText("Done");
        } else {
            // We are editing an existing Person
            stage.setTitle("Edit Person");
            okButton.setText("Done");
            shortNameModified = true;

            longNameTextField.setText(person.getLongName());
            shortNameTextField.setText(person.getShortName());
            descriptionTextField.setText(person.getDescription());
            userIDTextField.setText(person.getUserID());
            emailTextField.setText(person.getEmailAddress());
            phoneTextField.setText(person.getPhoneNumber());
            departmentTextField.setText(person.getDepartment());
        }

        // Load skills into skill lists
        setSkillsListSelectionViewData();
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            stage.close();
        });
    }

    private void setCommand() {
        final ArrayList<Skill> skills = new ArrayList<>();
        skills.addAll(targetSkills);

        if (person == null) {
            final Person p = new Person(shortNameTextField.getText(), longNameTextField.getText(),
                    descriptionTextField.getText(), userIDTextField.getText(), emailTextField.getText(),
                    phoneTextField.getText(), departmentTextField.getText(), skills);
            command = new CreatePersonCommand(p, organisation);
        } else {
            final ArrayList<Command<?>> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(person.getShortName())) {
                changes.add(new EditCommand<>(person, "shortName", shortNameTextField.getText()));
            }
            if (!longNameTextField.getText().equals(person.getLongName())) {
                changes.add(new EditCommand<>(person, "longName", longNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(person.getDescription())) {
                changes.add(new EditCommand<>(person, "description", descriptionTextField.getText()));
            }
            if (!userIDTextField.getText().equals(person.getUserID())) {
                changes.add(new EditCommand<>(person, "userID", userIDTextField.getText()));
            }
            if (!emailTextField.getText().equals(person.getEmailAddress())) {
                changes.add(new EditCommand<>(person, "emailAddress", emailTextField.getText()));
            }
            if (!phoneTextField.getText().equals(person.getPhoneNumber())) {
                changes.add(new EditCommand<>(person, "phoneNumber", phoneTextField.getText()));
            }
            if (!departmentTextField.getText().equals(person.getDepartment())) {
                changes.add(new EditCommand<>(person, "department", departmentTextField.getText()));
            }
            if (!(skills.containsAll(person.getSkills())
                    && person.getSkills().containsAll(skills))) {
                changes.add(new EditCommand<>(person, "skills", skills));
            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Person", changes);
        }
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

    /**
     * Sets the listener on the nameTextField so that the shortNameTextField is populated in real time
     * up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Auto populate short name text field
            if (!Objects.equals(newValue, longNameTextField.getText().substring(0,
                    Math.min(longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified = true;
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name can be populated with a suggestion
     */
    public void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name character by character up to specified characters
        longNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
            }
        });
    }

    public boolean isValid() {
        return valid;
    }

    public Command<?> getCommand() { return command; }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        setUpSkillsList();
    }
}