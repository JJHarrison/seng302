package com.thirstygoat.kiqo.gui.model;

import java.time.LocalDate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;


/**
 * Model ViewModel
 * @author amy
 *
 */
public class AllocationViewModel implements ViewModel {
    private final ObjectProperty<Organisation> organisationProperty;

    private final ObjectProperty<Team> teamProperty;
    private final ObjectProperty<Project> projectProperty;
    private final ObjectProperty<LocalDate> startDateProperty;
    private final ObjectProperty<LocalDate> endDateProperty;
    
    private final ObservableRuleBasedValidator startDateValidator;
    private final ObservableRuleBasedValidator endDateValidator;

    public AllocationViewModel() {
        organisationProperty = new SimpleObjectProperty<>();

        teamProperty = new SimpleObjectProperty<>();
        projectProperty = new SimpleObjectProperty<>();
        startDateProperty = new SimpleObjectProperty<>();
        endDateProperty = new SimpleObjectProperty<>();
        
        startDateValidator = new ObservableRuleBasedValidator();
        endDateValidator = new ObservableRuleBasedValidator();
        
        startDateValidator.addRule(startDateProperty.isNotNull().and(startDateProperty.isNotEqualTo(LocalDate.MAX)), ValidationMessage.error("Start date must not be empty"));

        endDateValidator.addRule(
            Bindings.createBooleanBinding(
                () -> { return startDateProperty.get().isBefore(endDateProperty.get()); }, 
                startDateProperty, endDateProperty),
            ValidationMessage.error("Start date must precede end date"));        
    }
    
    public void load(Allocation allocation, Organisation organisation) {
        this.organisationProperty.set(organisation);
        if (allocation != null) {
            teamProperty.set(allocation.getTeam());
            projectProperty.set(allocation.getProject());
            startDateProperty.set(allocation.getStartDateProperty().get());
            endDateProperty.set(allocation.getEndDateProperty().get());
        } else {
            teamProperty.set(null);
            projectProperty.set(null);
            startDateProperty.set(null);
            endDateProperty.set(null);
        }
    }
    
    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }

    public ObjectProperty<Team> teamProperty() {
        return teamProperty;
    }

    public ObjectProperty<Project> projectProperty() {
        return projectProperty;
    }
    
    public ObjectProperty<LocalDate> startDateProperty() {
        return startDateProperty;
    }
    
    public ObjectProperty<LocalDate> endDateProperty() {
        return endDateProperty;
    }
    
    public ValidationStatus startDateValidation() {
        return startDateValidator.getValidationStatus();
    }
    
    public ValidationStatus endDateValidation() {
        return endDateValidator.getValidationStatus();
    }
}
