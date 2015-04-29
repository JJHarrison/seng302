package seng302.group4.undo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import seng302.group4.Person;
import seng302.group4.Organisation;
import seng302.group4.Skill;

/**
 * Command to delete a skill from a project.
 *
 */
public class DeleteSkillCommand extends Command<Skill> {

    private final Organisation organisation;
    private final Skill skill;
    // Hash map of people with skills, and the index at which the skill appears in their skills list
    private final Map<Person, Integer> peopleWithSkill = new HashMap<>();

    private int organisationIndex;

    /**
     * @param skill Skill to be deleted
     * @param organisation organisation to which the skill belongs
     */
    public DeleteSkillCommand(final Skill skill, final Organisation organisation) {
        this.skill = skill;
        this.organisation = organisation;

        setPeopleWithSkill();
    }

    /**
     * Loops through all the people in the project, and adds the people who have the skill
     * to the ArrayList that contains only people who have that skill
     */
    private void setPeopleWithSkill() {
        for (Person person : organisation.getPeople()) {
            if (person.getSkills().contains(skill)) {
                peopleWithSkill.put(person, person.observableSkills().indexOf(skill));
            }
        }
    }

    /**
     * @return all people in the project who have the skill that is marked for deletion
     */
    public ArrayList<Person> getPeopleWithSkill() {
        ArrayList<Person> arrayList = new ArrayList<>();
        for (Person person : peopleWithSkill.keySet()) {
            arrayList.add(person);
        }
        return arrayList;
    }

    @Override
    public Skill execute() {
        // Remove the skill from any people in the project who have the skill
        for (Person person : peopleWithSkill.keySet()) {
            person.observableSkills().remove(skill);
            System.out.println("removed skill: " + skill + " from: " + person);
        }

        organisationIndex = organisation.getSkills().indexOf(skill);
        organisation.getSkills().remove(skill);
        return skill;
    }

    @Override
    public void undo() {
        // Add the skill back to wherever it was
        organisation.getSkills().add(organisationIndex, skill);

        for (Map.Entry<Person, Integer> entry : peopleWithSkill.entrySet()) {
            entry.getKey().observableSkills().add(entry.getValue(), skill);
        }
    }

    @Override
    public String toString() {
        return "<Delete Skill: \"" + skill.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Skill";
    }
}