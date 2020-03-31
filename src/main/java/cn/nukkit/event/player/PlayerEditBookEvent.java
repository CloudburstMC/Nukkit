package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class PlayerEditBookEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item oldBook;
    private final int action;
    private Item newBook;

    public PlayerEditBookEvent(Player player, Item oldBook, Item newBook, int action) {
        this.player = player;
        this.oldBook = oldBook;
        this.newBook = newBook;
        this.action = action;
    }

    public int getAction() {
        return this.action;
    }

    public Item getOldBook() {
        return this.oldBook;
    }

    public Item getNewBook() {
        return this.newBook;
    }

    public void setNewBook(Item book) {
        this.newBook = book;
    }
}
