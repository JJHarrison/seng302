package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by bradley on 14/04/15.
 */
public class CreateAcceptanceCriteriaCommandTest {
    private Project project;
    private Backlog backlog;
    private Person person;
    private Story story;
    private CreateAcceptanceCriteriaCommand command;
    private AcceptanceCriteria acceptanceCriteria;

    @Before
    public void setup() {
        project = new Project("proj", "Project");
        person = new Person("pers1", "Person","descr", "id", "email", "phone", "dept", new ArrayList<Skill>());
        story = new Story("story1", "Story One", "descr", person, project, backlog, 9);
        acceptanceCriteria = new AcceptanceCriteria("Creating new acceptance criteria will add it to the list of AC's in the story");
        command = new CreateAcceptanceCriteriaCommand(acceptanceCriteria, story);
    }

    /**
     * Create an AC and check that it is added to the ac's of the story
     */
    @Test
    public void createAC() {
        Assert.assertFalse(story.getAcceptanceCriteria().contains(acceptanceCriteria));

        command.execute();

        Assert.assertTrue(story.getAcceptanceCriteria().contains(acceptanceCriteria));
    }

    @Test
    public void undoCreateAC() {
        command.execute();
        command.undo();

        Assert.assertFalse(story.getAcceptanceCriteria().contains(acceptanceCriteria));
    }
}