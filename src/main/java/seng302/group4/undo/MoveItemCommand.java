package seng302.group4.undo;

import seng302.group4.Item;

import java.util.Collection;

/**
 * Created by samschofield on 25/04/15.
 * Command for moving an item from one collection to another
 */
public class MoveItemCommand extends Command<Void> {
    private final Item item;
    private final Collection<Item> position;
    private final Collection<Item> destination;


    /**
     *
     * @param item The item to move
     * @param position The current position of the item
     * @param destination The desired destination for the item
     */
    public MoveItemCommand(final Item item, final Collection<Item> position, final Collection<Item> destination) {
        this.item = item;
        this.position = position;
        this.destination = destination;
    }

    @Override
    public Void execute() {
        position.remove(item);
        destination.add(item);
        return null;
    }

    @Override
    public void undo() {
        destination.remove(item);
        position.add(item);
    }

    @Override
    public String getType() {
        return "Move Item";
    }

}
