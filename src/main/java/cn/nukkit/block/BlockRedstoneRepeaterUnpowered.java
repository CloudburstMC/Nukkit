package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.POWERED_REPEATER;

/**
 * Created by CreeperFace on 10.4.2017.
 */
public class BlockRedstoneRepeaterUnpowered extends BlockRedstoneDiode {

    public BlockRedstoneRepeaterUnpowered(Identifier id) {
        super(id);
        this.isPowered = false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.setMeta(this.getMeta() + 4);
        if (this.getMeta() > 15) this.setMeta(this.getMeta() % 4);

        this.level.setBlock(this.getPosition(), this, true, false);
        return true;
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(getMeta());
    }

    @Override
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.REPEATER);
    }

    @Override
    protected int getDelay() {
        return (1 + (getMeta() >> 2)) * 2;
    }

    @Override
    protected Block getPowered() {
        return Block.get(POWERED_REPEATER, this.getMeta());
    }

    @Override
    protected Block getUnpowered() {
        return this;
    }

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }
}