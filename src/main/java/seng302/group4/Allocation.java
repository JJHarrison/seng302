package seng302.group4;

import java.time.LocalDate;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private final LocalDate startDate;  //remove this
    private final LocalDate endDate;  //remove this
    private final Team team;
    private final Project project;
//    private final StringProperty projectStringProperty;
//    private final ObjectProperty<LocalDate> startDateProperty;
//    private final ObjectProperty<LocalDate> endDateProperty;

    /**
     * Creates a new allocation, checks that the start date is before the end date
     * @param team the team for the allocation
     * @param startDate the start date for the allocation
     * @param endDate the end date for the allocation
     * @param project the project for the allocation
     */
    public Allocation(Team team, LocalDate startDate, LocalDate endDate, Project project) {
        this.team = team;
        this.startDate = startDate;  //remove this
        this.endDate = endDate;  //remove this
        this.project = project;  //remove this
//        this.projectStringProperty = new SimpleStringProperty(project.getShortName());  //DO NOT DELETE, USING THESE WHEN GSON IS FIXED
//        this.startDateProperty = new SimpleObjectProperty<>(startDate);  //DO NOT DELETE, USING THESE WHEN GSON IS FIXED
//        this.endDateProperty = new SimpleObjectProperty<>(endDate);  //DO NOT DELETE, USING THESE WHEN GSON IS FIXED

    }

//    ######################### DO NOT DELETE, USING THESE WHEN GSON IS FIXED #########################

//    public StringProperty getProjectStringProperty() {
//        return projectStringProperty;
//    }
//
//
//    public void setProjectStringProperty(String projectStringProperty) {
//        this.projectStringProperty.set(projectStringProperty);
//    }
//
//
//    public ObjectProperty<LocalDate> getStartDateProperty() {
//        return startDateProperty;
//    }
//
//    public void setStartDateProperty(LocalDate startDateProperty) {
//        this.startDateProperty.set(startDateProperty);
//    }
//
//
//    public ObjectProperty<LocalDate> getEndDateProperty() {
//        return endDateProperty;
//    }
//
//    public void setEndDateProperty(LocalDate endDateProperty) {
//        this.endDateProperty.set(endDateProperty);
//    }


    // ######################### remove these and fix references #########################


    /**
     *
     * @return startDate the start date for the allocation
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     *
     * @return endDate the end date for the allocation
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    public String getProjectShortName() {
        return project.getShortName();
    }

    // ###################################################################################

    /**
     *
     * @return the project the allocation belongs to
     */
    public Project getProject() {
        return project;
    }

    /**
     *
     * @return team the team for the allocation
     */
    public Team getTeam() {
        return team;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Allocation)) {
            return false;
        }
        final Allocation other = (Allocation) obj;
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (project == null) {
            if (other.project != null) {
                return false;
            }
        } else if (!project.equals(other.project)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (team == null) {
            if (other.team != null) {
                return false;
            }
        } else if (!team.equals(other.team)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Allocation{team=" + team.getShortName() + ", project=" + project.getShortName() + ", startDate=" + startDate + ", endDate="
                + endDate + "}";
    }
}
