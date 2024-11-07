package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Event for sign text change.
 * @author MagicDroidX
 */
public class SignChangeEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;

    private final String[] lines;

    /**
     * This event is called when a sign is being edited.
     * @param block Sign block.
     * @param player Player that edited the sign.
     * @param lines Sign text (String[4]) after the edit.
     */
    public SignChangeEvent(Block block, Player player, String[] lines) {
        super(block);
        this.player = player;
        this.lines = lines;
    }

    public Player getPlayer() {
        return player;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) {
        return this.lines[index];
    }

    public void setLine(int index, String line) {
        this.lines[index] = line;
    }
}
