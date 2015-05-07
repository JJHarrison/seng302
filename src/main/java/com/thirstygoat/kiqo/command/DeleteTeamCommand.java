package com.thirstygoat.kiqo.command;

import java.util.LinkedHashMap;
import java.util.Map;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;


/**
 * Created by james on 11/04/15.
 */
public class DeleteTeamCommand extends Command<Team> {
    private final Organisation organisation;
    private final Team team;
    // Hash map of people in the team to be deleted, and the index at which the person appears in people list of
    // the organisation
    private final Map<Integer, Person> teamMembers = new LinkedHashMap<>();

    private int organisationIndex;

    /**
     *
     * @param team the team to be deleted
     * @param organisation the project to delete the team from
     */
    public DeleteTeamCommand(final Team team, final Organisation organisation) {
        this.organisation = organisation;
        this.team = team;
    }

    /**
     * Sets the members of the team to be deleted
     */
    public void setDeleteMembers() {
        for (final Person person : team.getTeamMembers()) {
            teamMembers.put(organisation.getPeople().indexOf(person), person);
        }
    }

    @Override
    public Team execute() {
        // Set team members' team field to null
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(null);
        }

        // if setDeleteMembers was called, delete each team member
        for (final Person person : teamMembers.values()) {
            organisation.getPeople().remove(person);
        }

        // delete the team
        organisationIndex = organisation.getTeams().indexOf(team);
        organisation.getTeams().remove(team);
        return team;
    }


    @Override
    public void undo() {
        // Set team members team field to this team
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(team);
        }

        for (final Map.Entry<Integer, Person> entry : teamMembers.entrySet()) {
            organisation.getPeople().add(entry.getKey(), entry.getValue());
        }

        organisation.getTeams().add(organisationIndex, team);

    }

    @Override
    public String toString() {
        return "<Delete Team: \"" + team.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Team";
    }
}