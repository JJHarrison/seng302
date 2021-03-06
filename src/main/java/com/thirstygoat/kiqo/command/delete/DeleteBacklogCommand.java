package com.thirstygoat.kiqo.command.delete;

import java.util.LinkedHashMap;
import java.util.Map;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by Carina on 20/05/2015.
 */
public class DeleteBacklogCommand extends DeleteCommand {
    private final Project project;
    private final Backlog backlog;
    private final Map<Integer, Story> stories = new LinkedHashMap<>();
    private boolean deleteStories = false;
    private int index;

    /**
     * Delete a backlog and either delete its stories or move them to being unallocated
     * @param backlog
     */
    public DeleteBacklogCommand(final Backlog backlog) {
        super(backlog);
        this.project = backlog.getProject();
        this.backlog = backlog;

        for (final Story story  : backlog.getStories()) {
            stories.put(backlog.getStories().indexOf(story), story);
        }
    }

    /**
     * Sets the stories of the backlog to be deleted
     */
    public void setDeleteMembers() {
        deleteStories = true;
    }

    @Override
    public void removeFromModel() {
        // Set stories backlog' field to null
        for (final Story story : backlog.getStories()) {
            story.setBacklog(null);
        }

        // delete each story member
        for (final Story story : stories.values()) {
            backlog.getStories().remove(story);
        }

        // delete the backlog
        index = project.getBacklogs().indexOf(backlog);
        project.observableBacklogs().remove(backlog);
    }

    @Override
    public void addToModel() {

        // Set team members team field to this team
        for (final Story story : backlog.getStories()) {
            story.setBacklog(backlog);
        }

        // if we deleted the stories put them back, otherwise they will be moved back with moveItemCommands in MC
        if(deleteStories) {
            for (final Map.Entry<Integer, Story> entry : stories.entrySet()) {
                backlog.getStories().add(entry.getKey(), entry.getValue());
            }
        }
        project.observableBacklogs().add(index, backlog);
    }

    @Override
    public String toString() {
        return "<Delete Backlog: \"" + backlog.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Backlog";
    }
}
