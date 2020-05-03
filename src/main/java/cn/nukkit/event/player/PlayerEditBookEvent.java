package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.BookEditPacket;

public class PlayerEditBookEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item oldBook;
    private final BookEditPacket.Action action;
    private Item newBook;

    public PlayerEditBookEvent(Player player, Item oldBook, Item newBook, BookEditPacket.Action action) {
        this.player = player;
        this.oldBook = oldBook;
        this.newBook = newBook;
        this.action = action;
    }

    public BookEditPacket.Action getAction() {
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
