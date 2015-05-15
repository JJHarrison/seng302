package com.thirstygoat.kiqo.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by leroy on 15/05/15.
 */
public class Story extends Item {
    private static final int DEFAULT_PRIORITY = 0;

    private final StringProperty shortName;
    private final StringProperty longName;
    private final StringProperty description;
    private final ObjectProperty<Person> creator;
    private final ObjectProperty<Project> project;
    private final IntegerProperty priority;

    /**
     * no-arg constructor for JavaBeans compliance
     */
    public Story() {
        this("", "", "", null, null, Story.DEFAULT_PRIORITY);
    }

    public Story(String shortName, String longName, String description, Person creator, Project project,
                 Integer priority) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);
        this.creator = new SimpleObjectProperty<>(creator);
        this.project = new SimpleObjectProperty<>(project);
        this.priority = new SimpleIntegerProperty(priority);
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Person getCreator() {
        return creator.get();
    }

    public ObjectProperty<Person> creatorProperty() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator.set(creator);
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public ObjectProperty<Project> projectProperty() {
       return project;
    }

    public Project getProject() {
        return project.get();
    }

    public int getPriority() {
        return priority.get();
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Story story = (Story) o;

        if (!getShortName().equals(story.getShortName())) {
            return false;
        }
        return getProject().equals(story.getProject());

    }

    @Override
    public int hashCode() {
        int result = getShortName().hashCode();
        result = 31 * result + getProject().hashCode();
        return result;
    }
}
