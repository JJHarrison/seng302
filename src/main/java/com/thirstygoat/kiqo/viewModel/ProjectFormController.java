package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class ProjectFormController implements Initializable {
    public final PopOver errorPopOver = new PopOver();
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public String longName;
    public String shortName;
    public String description;
    // FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private Button openButton;
    @FXML
    private TextField descriptionTextField;
    private boolean shortNameModified = false;
    private boolean valid = false;
    private Window stage;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setShortNameHandler();
        setErrorPopOvers();
        setPrompts();
        Platform.runLater(ProjectFormController.this.longNameTextField::requestFocus);
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Goats");
        descriptionTextField.setPromptText("Describe this project.");

    }

    public void loadProject(final Project project) {
        longNameTextField.setText(project.getLongName());
        shortNameTextField.setText(project.getShortName());
        descriptionTextField.setText(project.getDescription());
    }

    /**
     * Sets focus listeners on text fields so PopOvers are hidden upon focus
     */
    private void setErrorPopOvers() {
        // Set PopOvers as not detachable so we don't have floating PopOvers
        errorPopOver.setDetachable(false);

        // Set handlers so that popovers are hidden on field focus
        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
            }
        });
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
            }
        });
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     */
    public void validate() {
        // Hide existing error message if there is one
        errorPopOver.hide();

        // Perform validity checks and create project
        if (checkName() && checkShortName()) {
            // Set project properties
            longName = longNameTextField.getText();
            shortName = shortNameTextField.getText();
            description = descriptionTextField.getText();

            valid = true;
        }
    }

//    /**
//     * Checks to make sure that the save location has been set, and it is
//     * writable by the user
//     *
//     * @return Whether or not the save location is valid/readable/writable
//     */
//    private boolean checkSaveLocation() {
//        if (this.projectLocation == null) {
//            // Then the user hasn't selected a project directory, alert them!
//            this.errorPopOver.setContentNode(new Label("Please select a Project Location"));
//            this.errorPopOver.show(this.projectLocationLabel);
//            return false;
//        }
//        // Confirm read/write access
//        final File equalPermissionsFile = this.projectLocation.exists() ? this.projectLocation : this.projectLocation.getParentFile();
//        if (!equalPermissionsFile.canRead()) {
//            // Then we can't read from the directory, what's the point!
//            this.errorPopOver.setContentNode(new Label("Can't read from the specified directory"));
//            this.errorPopOver.show(this.projectLocationLabel);
//            return false;
//        }
//        if (!equalPermissionsFile.canWrite()) {
//            // Then we can't write to the directory
//            this.errorPopOver.setContentNode(new Label("Can't write to the specified directory"));
//            this.errorPopOver.show(this.projectLocationLabel);
//            return false;
//        }
//
//        return true;
//    }

    /**
     * Checks to make sure the short name is valid
     *
     * @return Whether or not the short name is valid
     */
    private boolean checkShortName() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }
        // TODO Check for uniqueness
        // if (!UNIQUE CHECKER) {
        // shortNamePopOver.setContentNode(new
        // Label("Short name must be unique"));
        // shortNamePopOver.show(shortNameTextField);
        // shortNameTextField.requestFocus();
        // }
        return true;
    }

    /**
     * Checks to make sure the long name is valid
     *
     * @return Whether or not the long name is valid
     */
    private boolean checkName() {
        if (longNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Name must not be empty"));
            errorPopOver.show(longNameTextField);
            return false;
        }
        return true;
    }

    /**
     * Sets the listener on the nameTextField so that the shortNameTextField is
     * populated in real time up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // Set up short name suggester
                    if (!Objects.equals(newValue, longNameTextField.getText().substring(0, Math.min(
                            longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                        shortNameModified = true;
                    }

                    // Restrict length of short name text field
                    if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                        shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
                    }
                });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name
     * can be populated with a suggestion
     */
    public void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name
        // character by character up to specified characters
        longNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
            }
        });
    }

//    /**
//     * Sets the open dialog functionality including getting the path chosen by
//     * the user.
//     */
//    private void setOpenButton() {
//        final String EXTENSION = ".json";
//        this.openButton.setOnAction(event -> {
//            // Hide existing error message if there is one
//                this.errorPopOver.hide();
//
//                final FileChooser fileChooser = new FileChooser();
//                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*" + EXTENSION));
//
//                if (this.projectLocation != null) {
//                    // Then this is an edit dialog, we need to make sure that
//                    // the
//                    // user opens to the project directory
//                    fileChooser.setInitialDirectory(this.projectLocation.getParentFile());
//                    fileChooser.setInitialFileName(this.projectLocation.getName());
//                } else {
//                    fileChooser.setInitialFileName(shortNameTextField.getText());
//                }
//
//                File selectedFile = fileChooser.showSaveDialog(this.stage);
//                if (selectedFile != null) {
//                    // ensure file has .json extension
//                    final String selectedFilename = selectedFile.getName();
//                    if (!selectedFilename.endsWith(EXTENSION)) {
//                        // append extension
//                        selectedFile = new File(selectedFile.getParentFile(), selectedFilename + EXTENSION);
//                    }
//                    // store selected file
//                    this.projectLocationLabel.setText(selectedFile.getPath());
//                    this.projectLocation = selectedFile.getAbsoluteFile();
//                }
//        });
//    }


    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public boolean isValid() {
        return valid;
    }
}