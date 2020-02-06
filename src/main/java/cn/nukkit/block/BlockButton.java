package cn.nukkit.block;

import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public abstract class BlockButton extends FloodableBlock implements Faceable {

    public BlockButton(Identifier id) {
        super(id);
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (target.isTransparent()) {
            return false;
        }

        this.setDamage(face.getIndex());
        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (this.isActivated()) {
            return false;
        }

        this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        this.setDamage(this.getDamage() ^ 0x08);
        this.level.setBlock(this, this, true, false);
        this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.RANDOM_CLICK);
        this.level.scheduleUpdate(this, 30);

        level.updateAroundRedstone(this, null);
        level.updateAroundRedstone(this.getSide(getFacing().getOpposite()), null);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getFacing().getOpposite()).isTransparent()) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.isActivated()) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                this.setDamage(this.getDamage() ^ 0x08);
                this.level.setBlock(this, this, true, false);
                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.RANDOM_CLICK);

                level.updateAroundRedstone(this, null);
                level.updateAroundRedstone(this.getSide(getFacing().getOpposite()), null);
            }

            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public boolean isActivated() {
        return ((this.getDamage() & 0x08) == 0x08);
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public int getWeakPower(BlockFace side) {
        return isActivated() ? 15 : 0;
    }

    public int getStrongPower(BlockFace side) {
        return !isActivated() ? 0 : (getFacing() == side ? 15 : 0);
    }

    public BlockFace getFacing() {
        int side = isActivated() ? getDamage() ^ 0x08 : getDamage();
        return BlockFace.fromIndex(side);
    }

    @Override
    public boolean onBreak(Item item) {
        if (isActivated()) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        }

        return super.onBreak(item);
    }

    @Override
    public Item toItem() {
        return Item.get(this.getId(), 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }
}
