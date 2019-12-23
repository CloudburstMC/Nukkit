package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class LecternPlaceBookEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BlockEntityLectern lectern;
    private Item book;

    public LecternPlaceBookEvent(Player player, BlockEntityLectern lectern, Item book) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.book = book;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockEntityLectern getLectern() {
        return lectern;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getBook() {
        return book.clone();
    }

    public void setBook(Item book) {
        this.book = book;
    }
}
