package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class LecternPageChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BlockEntityLectern lectern;
    private int newPage;

    public LecternPageChangeEvent(Player player, BlockEntityLectern lectern, int newPage) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.newPage = newPage;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockEntityLectern getLectern() {
        return lectern;
    }

    public int getNewPage() {
        return newPage;
    }

    public void setNewPage(int newPage) {
        this.newPage = newPage;
    }

    public Player getPlayer() {
        return player;
    }
}
