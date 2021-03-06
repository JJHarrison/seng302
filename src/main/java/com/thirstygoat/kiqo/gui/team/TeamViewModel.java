package com.thirstygoat.kiqo.gui.team;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatCollectors;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.*;

public class TeamViewModel extends ModelViewModel<Team> {

    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private ObservableRuleBasedValidator productOwnerValidator;
    private ObservableRuleBasedValidator scrumMasterValidator;
    private ObservableRuleBasedValidator teamMembersValidator;
    private ObservableRuleBasedValidator devTeamValidator;
    private CompositeValidator allValidator;
	private final ListProperty<Person> eligibleTeamMembers;
	private final ListProperty<Person> eligibleDevs;

    public TeamViewModel() {
        createValidators();
        
        ListProperty<Person> peopleInOrganisation = new SimpleListProperty<>();
        peopleInOrganisation.bind(Bindings.createObjectBinding(() -> {
        	if (organisationProperty().get() != null) {
        		return organisationProperty().get().getPeople();
        	} else {
        		return FXCollections.observableArrayList();
        	}
        }, organisationProperty()));

        ListProperty<Team> teamsInOrganisation = new SimpleListProperty<>();
        teamsInOrganisation.bind(Bindings.createObjectBinding(() -> {
        	if (organisationProperty().get() != null) {
        		return organisationProperty().get().getTeams();
        	} else {
        		return FXCollections.observableArrayList();
        	}
        }, organisationProperty()));
        
        ListProperty<Person> peopleInTeams = new SimpleListProperty<>();
        peopleInTeams.bind(Bindings.createObjectBinding(() -> {
        	return teamsInOrganisation.stream()
					.flatMap(team -> team.observableTeamMembers().stream()) // TODO listen
					.collect(GoatCollectors.toObservableList());
        }, teamsInOrganisation));
        
        eligibleTeamMembers = new SimpleListProperty<>();
        eligibleTeamMembers.bind(Bindings.createObjectBinding(() -> {
    		return peopleInOrganisation.stream()
    				.filter(person -> !peopleInTeams.contains(person) // no team in model
    						|| person.getTeam() == modelWrapper.get()) // this team in model
		            .collect(GoatCollectors.toObservableList());
		}, peopleInOrganisation, peopleInTeams));

        eligibleDevs = new SimpleListProperty<>();
        eligibleDevs.bind(Bindings.createObjectBinding(() -> {
			return teamMembersProperty().get().stream() // is in team
		            .filter(person -> 
		            	!person.equals(productOwnerProperty().get()) // not PO
		            	&& !person.equals(scrumMasterProperty().get())) // not SM
		            .collect(GoatCollectors.toObservableList());
		}, teamMembersProperty(), productOwnerProperty(), scrumMasterProperty()));
        
        teamMembersProperty().addListener((ListChangeListener.Change<? extends Person> change) -> {
        	change.next();
        	List<? extends Person> removed = change.getRemoved();
        	devTeamProperty().removeAll(removed);
        	if (removed.contains(productOwnerProperty().get())) {
        		productOwnerProperty().set(null);
        	}
        	if (removed.contains(scrumMasterProperty().get())) {
        		scrumMasterProperty().set(null);
        	}
        });
    }

    @Override
    protected Supplier<Team> modelSupplier() {
        return Team::new;
    }

    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                if (organisationProperty().get() != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(),
                                    organisationProperty().get().getTeams());
                } else {
                    return true; // no project means this isn't for real yet.
                }
            }, 
            shortNameProperty(), organisationProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(Utilities.SHORT_NAME_MAX_LENGTH), ValidationMessage.error("Name must be less than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
       
        // the other validators are input-constrained so need not be validated
        productOwnerValidator = new ObservableRuleBasedValidator(); // always true
        scrumMasterValidator = new ObservableRuleBasedValidator(); // always true
        teamMembersValidator = new ObservableRuleBasedValidator(); // always true
        devTeamValidator = new ObservableRuleBasedValidator(); // always true

        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, productOwnerValidator, scrumMasterValidator, teamMembersValidator, devTeamValidator);
    }

    @Override
    public Command getCommand() {
        // edit command
        final ArrayList<Command> changes = new ArrayList<>();
        super.addEditCommands.accept(changes);
        
        if (productOwnerProperty().get() != modelWrapper.get().getProductOwner()) {
            changes.add(new EditCommand<>(modelWrapper.get(), "productOwner", productOwnerProperty().get()));
        }

        if (scrumMasterProperty().get() != modelWrapper.get().getScrumMaster()) {
            changes.add(new EditCommand<>(modelWrapper.get(), "scrumMaster", scrumMasterProperty().get()));
        }

        if (!(teamMembersProperty().get().containsAll(modelWrapper.get().getTeamMembers())
        		&& modelWrapper.get().getTeamMembers().containsAll(teamMembersProperty().get()))) {
        	// BEWARE: magic
        	ArrayList<Person> teamMembers = new ArrayList<>();
        	teamMembers.addAll(teamMembersProperty().get());
        	changes.add(new EditCommand<>(modelWrapper.get(), "teamMembers", teamMembers));
        }

        if (!(devTeamProperty().get().containsAll(modelWrapper.get().getDevTeam())
        		&& modelWrapper.get().getDevTeam().containsAll(devTeamProperty().get()))) {
        	// BEWARE: magic
        	ArrayList<Person> devTeam = new ArrayList<>();
        	devTeam.addAll(devTeamProperty().get());
        	changes.add(new EditCommand<>(modelWrapper.get(), "devTeam", devTeam));
        }

        final ArrayList<Person> newMembers = new ArrayList<>(teamMembersProperty().get());
        newMembers.removeAll(modelWrapper.get().observableTeamMembers());
        final ArrayList<Person> oldMembers = new ArrayList<>(modelWrapper.get().observableTeamMembers());
        oldMembers.removeAll(teamMembersProperty().get());

        // Loop through all the new members and add a command to set their team
        // Set the person's team field to this team
        changes.addAll(newMembers.stream().map(person -> new EditCommand<>(person, "team", modelWrapper.get()))
                .collect(Collectors.toList()));

        // Loop through all the old members and add a command to remove their team
        // Set the person's team field to null, since they're no longer in the team
        changes.addAll(oldMembers.stream().map(person -> new EditCommand<>(person, "team", null))
                .collect(Collectors.toList()));

        return new CompoundCommand("Edit Team", changes);
    }

    protected Supplier<List<Person>> productOwnerSupplier() {
        return () -> {
            Skill poSkill = organisationProperty().get().getPoSkill();
            Person currentScrumMaster = scrumMasterProperty().get();
            List<Person> currentDevTeam = devTeamProperty().get();
            // person has po skill and does not currently have any other role in the team
            return teamMembersProperty().get().stream()
                    .filter(person -> {
                        return person.observableSkills().contains(poSkill) 
                                && (currentScrumMaster == null || !person.equals(currentScrumMaster))
                                && !currentDevTeam.contains(person);
                    }).collect(Collectors.toList());
        };
    }
    
    protected Supplier<List<Person>> scrumMasterSupplier() {
        return () -> {
            Skill smSkill = organisationProperty().get().getSmSkill();
            Person currentProductOwner = productOwnerProperty().get();
            List<Person> currentDevTeam = devTeamProperty().get();
            // person has sm skill and does not currently have any other role in the team
            return teamMembersProperty().get().stream()
                    .filter(person -> person.observableSkills().contains(smSkill) 
                            && !person.equals(currentProductOwner)
                            && !currentDevTeam.contains(person))
                    .collect(Collectors.toList());
        };
    }
    
    protected StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Team::getShortName, Team::setShortName, "");
    }

    protected StringProperty descriptionProperty() {
        return modelWrapper.field("description", Team::getDescription, Team::setDescription, "");
    }
    
    protected ObjectProperty<Person> productOwnerProperty() {
        return modelWrapper.field("productOwner", Team::getProductOwner, Team::setProductOwner, null);
    }
    
    protected ObjectProperty<Person> scrumMasterProperty() {
        return modelWrapper.field("scrumMaster", Team::getScrumMaster, Team::setScrumMaster, null);
    }
    
    protected ListProperty<Person> teamMembersProperty() {
        return modelWrapper.field("teamMembers", Team::getTeamMembers, Team::setTeamMembers, new ArrayList<Person>());
    }
    
    protected ListProperty<Person> devTeamProperty() {
        return modelWrapper.field("devTeam", Team::getDevTeam, Team::setDevTeam, new ArrayList<Person>());
    }

    protected ListProperty<Allocation> allocations() {
        return modelWrapper.field("allocations", Team::getAllocations, Team::setAllocations, new ArrayList<Allocation>());
    }
    
    protected ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }
    
    protected ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }
    
    protected ValidationStatus productOwnerValidation() {
        return productOwnerValidator.getValidationStatus();
    }
    
    protected ValidationStatus scrumMasterValidation() {
        return scrumMasterValidator.getValidationStatus();
    }
    
    protected ValidationStatus teamMembersValidation() {
        return teamMembersValidator.getValidationStatus();
    }
    
    protected ValidationStatus devTeamValidation() {
        return devTeamValidator.getValidationStatus();
    }
    
    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }

    protected ListProperty<Person> eligibleTeamMembers() {
	    return eligibleTeamMembers;
	}
    
    protected ListProperty<Person> eligibleDevs() {
        return eligibleDevs;
    }
}
