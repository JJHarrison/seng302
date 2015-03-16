package seng302.group4;

/**
 * Created by samschofield on 12/03/15. Project class represents a software
 * project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Project {
    private String shortName;
    private String longName;
    private String description;
    private String saveLocation;

    /**
     * Create new Project
     *
     * @param shortName
     *            a unique short name for the project
     * @param longName
     *            long name for project
     * @param saveLocation
     *            save location for the project
     */
    public Project(final String shortName, final String longName, final String saveLocation) {
        this.shortName = shortName;
        this.longName = longName;
        this.saveLocation = saveLocation;
    }

    /**
     * Create a new project
     *
     * @param shortName
     *            a unique short name for the project
     * @param longName
     *            long name for project
     * @param saveLocation
     *            save location for the project
     * @param description
     *            description of the project
     */
    public Project(final String shortName, final String longName, final String saveLocation, final String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.saveLocation = saveLocation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Project project = (Project) o;

        if (!this.shortName.equals(project.shortName)) {
            return false;
        }

        return true;
    }

    /**
     *
     * @return the description of the project
     */
    public String getDescription() {
        return this.description;
    }

    /**
     *
     * @return longName the long name of the project
     */
    public String getLongName() {
        return this.longName;
    }

    /**
     *
     * @return save location of project
     */
    public String getSaveLocation() {
        return this.saveLocation;
    }

    /**
     *
     * @return the short name of project
     */
    public String getShortName() {
        return this.shortName;
    }

    @Override
    public int hashCode() {
        return this.shortName.hashCode();
    }

    public void prepareForDestruction() {
        // FIXME Auto-generated method stub
        // eg. remove people
    }

    /**
     *
     * @param description
     *            the description of the project
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @param longName
     *            the long name for the project
     */
    public void setLongName(final String longName) {
        this.longName = longName;
    }

    /**
     *
     * @param saveLocation
     *            save location of project
     */
    public void setSaveLocation(final String saveLocation) {
        this.saveLocation = saveLocation;
    }

    /**
     *
     * @param shortName
     *            the short name of the project
     */
    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return "Project{" + "shortName='" + this.shortName + '\'' + ", longName='" + this.longName + '\'' + ", description='"
                + this.description + '\'' + ", saveLocation='" + this.saveLocation + '\'' + '}';
    }
}