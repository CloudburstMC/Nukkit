package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

/**
 * Event for Block being broken.
 * @author MagicDroidX
 */
public class BlockBreakEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Player player;

    protected final Item item;
    protected final BlockFace face;

    protected boolean instaBreak;
    protected Item[] blockDrops;
    protected int blockXP;

    protected boolean fastBreak;

    protected Vector3 dropPosition;

    /**
     * This event is called when a block is broken.
     * @param player Player who broke the block.
     * @param block Block that was broken.
     * @param item Item used to break the block.
     * @param drops Items dropped by the block.
     */
    public BlockBreakEvent(Player player, Block block, Item item, Item[] drops) {
        this(player, block, item, drops, false, false);
    }

    public BlockBreakEvent(Player player, Block block, Item item, Item[] drops, boolean instaBreak) {
        this(player, block, item, drops, instaBreak, false);
    }

    public BlockBreakEvent(Player player, Block block, Item item, Item[] drops, boolean instaBreak, boolean fastBreak) {
        this(player, block, null, item, drops, instaBreak, fastBreak, null);
    }

    public BlockBreakEvent(Player player, Block block, BlockFace face, Item item, Item[] drops, boolean instaBreak, boolean fastBreak, Vector3 dropPosition) {
        super(block);
        this.face = face;
        this.item = item;
        this.player = player;
        this.instaBreak = instaBreak;
        this.blockDrops = drops;
        this.fastBreak = fastBreak;
        this.blockXP = block.getDropExp();
    }

    public Player getPlayer() {
        return player;
    }

    public BlockFace getFace() {
        return face;
    }

    public Item getItem() {
        return item;
    }

    public boolean getInstaBreak() {
        return this.instaBreak;
    }

    public Item[] getDrops() {
        return blockDrops;
    }

    public void setDrops(Item[] drops) {
        if (drops == null) {
            drops = new Item[0];
        }

        this.blockDrops = drops;
    }

    public int getDropExp() {
        return this.blockXP;
    }

    public void setDropExp(int xp) {
        this.blockXP = xp;
    }

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }

    public boolean isFastBreak() {
        return this.fastBreak;
    }

    public void setDropPosition(Vector3 dropPosition) {
        this.dropPosition = dropPosition;
    }

    public Vector3 getDropPosition() {
        return this.dropPosition;
    }
}