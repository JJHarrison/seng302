package com.thirstygoat.kiqo.model;

import java.io.File;
import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.util.Utilities;

/**
 * Project class represents a software project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Organisation implements Serializable {
    private final Skill poSkill = new Skill("PO", "Product Owner");
    private final Skill smSkill = new Skill("SM", "Scrum Master");
    private final ObservableList<Project> projects = FXCollections.observableArrayList(Item.getWatchStrategy());
    private final ObservableList<Person> people = FXCollections.observableArrayList(Item.getWatchStrategy());
    private final ObservableList<Skill> skills = FXCollections.observableArrayList(Item.getWatchStrategy());
    private final ObservableList<Team> teams = FXCollections.observableArrayList(Item.getWatchStrategy());
    private transient File saveLocation;
    private final StringProperty organisationName = new SimpleStringProperty("Untitled");

    public Organisation() {
        skills.add(poSkill);
        skills.add(smSkill);
    }

    public Organisation(final File saveLocation) {
        this();
        this.saveLocation = saveLocation;
    }

    public Skill getPoSkill() {
        return poSkill;
    }

    public Skill getSmSkill() {
        return smSkill;
    }


    /**
     *
     * @return ObservableList of people in project
     */
    public ObservableList<Person> getPeople() {
        return people;
    }

    /**
     *
     * @return ObservableList of skills in project
     */
    public ObservableList<Skill> getSkills() {
        return skills;
    }

    /**
     *
     * @return save location of project
     */
    public File getSaveLocation() {
        return saveLocation;
    }

    /**
     *
     * @param saveLocation Save location of project
     */
    public void setSaveLocation(final File saveLocation) {
        this.saveLocation = saveLocation;
        organisationName.set(Utilities.stripExtension(saveLocation.getName()));
    }

    public StringProperty organisationNameProperty() {
        return organisationName;
    }

    /**
     *
     * @return ObservableList of teams
     */
    public ObservableList<Team> getTeams() {
        return teams;
    }

    /**
     *
     * @return Observable list of projects
     */
    public ObservableList<Project> getProjects() {
        return projects;
    }
}
