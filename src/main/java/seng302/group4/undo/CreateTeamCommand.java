package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Team;

import java.util.ArrayList;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreateTeamCommand extends Command<Team> {
    private final String shortName;
    private final String description;
    private final ArrayList<Person> teamMembers = new ArrayList<>();
    private final Person productOwner;
    private final Person scrumMaster;
    private ArrayList<Person> devTeam;

    private Team team = null;

    /**
     * Constructor for CreateTeamCommand
     * @param shortName Short name of the team
     * @param description Description of the team
     * @param teamMembers ArrayList of people to be in the team
     */
    public CreateTeamCommand(final String shortName, final String description, final ArrayList<Person> teamMembers,
                             final Person productOwner, final Person scrumMaster, final ArrayList<Person> devTeam) {
        this.shortName = shortName;
        this.description = description;
        this.teamMembers.addAll(teamMembers);
        this.productOwner = productOwner;
        this.scrumMaster = scrumMaster;
        this.devTeam = devTeam;
    }

    @Override
    public Team execute() {
        if (team == null) {
            team = new Team(shortName, description, teamMembers);
            team.setProductOwner(productOwner);
            team.setScrumMaster(scrumMaster);
            team.setDevTeam(devTeam);
        }
        return team;
    }

    @Override
    public void undo() {

    }

    @Override
    public String toString() {
        return "<Create Team: \"" + shortName + "\">";
    }

    public Team getTeam() {
        return team;
    }

    public String getType() {
        return "Create Team";
    }

}