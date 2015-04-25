package seng302.group4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bradley on 27/03/15.
 */
public class Team extends Item {
    private final List<Person> devTeam = new ArrayList<>();
    private final ArrayList<Allocation> allocations = new ArrayList<>();
    private transient ObservableList<Allocation> allocationsObservable = FXCollections.observableList(allocations);
    private String shortName;
    private String description;
    private Person productOwner;
    private Person scrumMaster;
    private List<Person> teamMembers;

    public Team(String shortName, String description, List<Person> teamMembers) {
        this.shortName = shortName;
        this.description = description;
        this.teamMembers = teamMembers;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Team{" + shortName);
        sb.append(", description=" + description);
        sb.append(", productOwner=");
        if (productOwner != null) {
            sb.append(productOwner.getShortName());
        }
        sb.append(", scrumMaster=");
        if (scrumMaster != null) {
            sb.append(scrumMaster.getShortName());
        }
        sb.append(", teamMembers=" + teamMembers);
        sb.append(", devTeam=" + devTeam);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<Person> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<Person> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Person getProductOwner() {
        return productOwner;
    }

    public void setProductOwner(Person productOwner) {
        this.productOwner = productOwner;
    }

    public Person getScrumMaster() {
        return scrumMaster;
    }

    public void setScrumMaster(Person scrumMaster) {
        this.scrumMaster = scrumMaster;
    }

    public List<Person> getDevTeam() {
        return devTeam;
    }

    public void setDevTeam(List<Person> devTeam) {
        this.devTeam.clear();
        this.devTeam.addAll(devTeam.stream().collect(Collectors.toList()));
    }

    public ObservableList<Allocation> getAllocations() {
        return allocationsObservable;
    }

    public void setObservableLists() {
        allocationsObservable = FXCollections.observableList(allocations);
    }
}
