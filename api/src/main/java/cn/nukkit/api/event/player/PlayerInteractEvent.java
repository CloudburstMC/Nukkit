package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.util.data.BlockFace;
import com.flowpowered.math.vector.Vector3f;
import lombok.Getter;
import lombok.Setter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

@Getter
@Setter
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

    protected final Block blockTouched;
    protected final Vector3f touchVector;
    protected final BlockFace blockFace;
    protected final ItemStack item;
    protected final Action action;
    private boolean cancelled;

    public PlayerInteractEvent(Player player, ItemStack item, Vector3f block, BlockFace face) {
        this(player, item, block, face, Action.RIGHT_CLICK_BLOCK);
    }

    public PlayerInteractEvent(Player player, ItemStack item, Vector3f block, BlockFace face, Action action) {
        super(player);

        if (block instanceof Block) {
            this.blockTouched = (Block) block;
            this.touchVector = new Vector3f(0, 0, 0);
        } else {
            this.touchVector = block;
            this.blockTouched = Block.get(Block.AIR, 0, new Location(player.getLevel(), 0, 0, 0));
        }

        this.item = item;
        this.blockFace = face;
        this.action = action;
    }

    public enum Action {
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_AIR,
        RIGHT_CLICK_AIR,
        PHYSICAL
    }
}
