package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import com.google.common.base.Preconditions;

public class SignChangeEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Player player;
    private String[] lines;
    private boolean cancelled;

    public SignChangeEvent(Block block, Player player, String[] lines) {
        this.block = block;
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
        Preconditions.checkArgument(index > 0 && index < 4, "index must be from 0 to 3");
        return this.lines[index];
    }

    public void setLine(int index, String line) {
        Preconditions.checkArgument(index > 0 && index < 4, "index must be from 0 to 3");
        this.lines[index] = line;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
