package cn.nukkit.event.block;

import cn.nukkit.blockentity.Lectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;

public class LecternPageChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Lectern lectern;
    private int newRawPage;

    public LecternPageChangeEvent(Player player, Lectern lectern, int newPage) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.newRawPage = newPage;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Lectern getLectern() {
        return lectern;
    }

    public int getLeftPage() {
        return (newRawPage * 2) + 1;
    }

    public void setLeftPage(int newLeftPage) {
        this.newRawPage = (newLeftPage - 1) / 2;
    }

    public int getRightPage() {
        return getLeftPage() + 1;
    }

    public void setRightPage(int newRightPage) {
        this.setLeftPage(newRightPage - 1);
    }

    public int getNewRawPage() {
        return newRawPage;
    }

    public void setNewRawPage(int newRawPage) {
        this.newRawPage = newRawPage;
    }

    public int getMaxPage() {
        return lectern.getTotalPages();
    }

    public Player getPlayer() {
        return player;
    }

}