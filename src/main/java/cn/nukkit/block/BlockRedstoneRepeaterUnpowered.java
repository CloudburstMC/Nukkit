package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstoneRepeater;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;

/**
 * Created by CreeperFace on 10.4.2017.
 */
public class BlockRedstoneRepeaterUnpowered extends BlockRedstoneDiode {

    public BlockRedstoneRepeaterUnpowered() {
        this(0);
    }

    public BlockRedstoneRepeaterUnpowered(int meta) {
        super(meta);
        this.isPowered = false;
    }

    @Override
    public int getId() {
        return UNPOWERED_REPEATER;
    }

    @Override
    public String getName() {
        return "Unpowered Repeater";
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.setDamage(this.getDamage() + 4);
        if (this.getDamage() > 15) this.setDamage(this.getDamage() % 4);

        this.level.setBlock(this, this, true, false);
        return true;
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(getDamage());
    }

    @Override
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem() {
        return new ItemRedstoneRepeater();
    }

    @Override
    protected int getDelay() {
        return (1 + (getDamage() >> 2)) * 2;
    }

    @Override
    protected Block getPowered() {
        return new BlockRedstoneRepeaterPowered(this.getDamage());
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