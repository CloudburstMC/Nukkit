package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.Position;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Block blockTouched;

    protected final Vector3 touchVector;

    protected final BlockFace blockFace;

    protected final Item item;

    protected final Action action;

    public PlayerInteractEvent(Player player, Item item, Vector3 block, BlockFace face) {
        this(player, item, block, face, Action.RIGHT_CLICK_BLOCK);
    }

    public PlayerInteractEvent(Player player, Item item, Vector3 block, BlockFace face, Action action) {
        if (block instanceof Block) {
            this.blockTouched = (Block) block;
            this.touchVector = new Vector3(0, 0, 0);
        } else {
            this.touchVector = block;
            this.blockTouched = Block.get(Block.AIR, 0, new Position(0, 0, 0, player.level));
        }

        this.player = player;
        this.item = item;
        this.blockFace = face;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlock() {
        return blockTouched;
    }

    public Vector3 getTouchVector() {
        return touchVector;
    }

    public BlockFace getFace() {
        return blockFace;
    }

    public enum Action {
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_AIR,
        RIGHT_CLICK_AIR,
        PHYSICAL
    }
}
