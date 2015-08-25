package com.thirstygoat.kiqo.gui.skill;

import static org.junit.Assert.fail;

import java.util.*;

import org.junit.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.skill.SkillViewModel;
import com.thirstygoat.kiqo.gui.viewModel.StoryFormViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

public class SkillViewModelTest {
    SkillViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new SkillViewModel();
        Skill skill = new Skill("name", "description");
        Organisation organisation = new Organisation();
        organisation.getSkills().add(skill);
        viewModel.load(skill, organisation);
    }

    @Test
    public final void testCreateCommand() {
        Command command;
        
        // no changes
        command = viewModel.createCommand();
        Assert.assertNull("command must be null if no changes made", command);

        // blue skies
        viewModel.nameProperty().set("Valid name");
        command = viewModel.createCommand();
        Assert.assertEquals("command must include all changes (1)", "1 change", command.toString());
        
        // failing validation
        viewModel.nameProperty().set("Invalid name because it is too long");
        command = viewModel.createCommand();
        Assert.assertFalse(viewModel.nameValidation().isValid()); // just to be sure
        Assert.assertEquals("command must include all changes (1) even when validation is not passing", "1 change", command.toString());
        
        // multiple changes
        viewModel.nameProperty().set("New name");
        viewModel.descriptionProperty().set("New description");
        command = viewModel.createCommand();
        Assert.assertEquals("command must include all changes (2)", "2 changes", command.toString());
    }

    @Test
    public final void testNameValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                viewModel.nameValidation().validProperty().get());
        
        viewModel.nameProperty().set(null);
        Assert.assertFalse("Must not be null.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set("This name is longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.");
        Assert.assertFalse("Must not be longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.",
                viewModel.nameValidation().validProperty().get());

        // validating uniqueness within project requires model data
        final String SHARED_NAME = "not unique";
        Skill secondSkill = new Skill(SHARED_NAME, "arbitrary description");
        viewModel.organisationProperty().get().getSkills().add(secondSkill);
        
        viewModel.nameProperty().set("unique");
        Assert.assertTrue("Unique name must be valid.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set(SHARED_NAME);
        Assert.assertFalse("Non-unique name must be invalid.",
                viewModel.nameValidation().validProperty().get());
    }

    @Test
    public final void testDescriptionValidation() {
        Assert.assertTrue("Description should be valid by default.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("");
        Assert.assertTrue("Empty string should be recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());
    }
}
