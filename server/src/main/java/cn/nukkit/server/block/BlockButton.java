package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockRedstoneEvent;
import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.sound.ButtonClickSound;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public abstract class BlockButton extends BlockFlowable {

    public BlockButton() {
        this(0);
    }

    public BlockButton(int meta) {
        super(meta);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.isTransparent()) {
            return false;
        }

        this.meta = face.getIndex();
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
        this.meta ^= 0x08;
        this.level.setBlock(this, this, true, false);
        this.level.addSound(new ButtonClickSound(this.add(0.5, 0.5, 0.5)));
        this.level.scheduleUpdate(this, 30);
        Vector3 pos = getLocation();

        level.updateAroundRedstone(pos, null);
        level.updateAroundRedstone(pos.getSide(getFacing().getOpposite()), null);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getFacing().getOpposite()).isTransparent()) {
                this.level.useBreakOn(this);
                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == NukkitLevel.BLOCK_UPDATE_SCHEDULED) {
            if (this.isActivated()) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                this.meta ^= 0x08;
                this.level.setBlock(this, this, true, false);
                this.level.addSound(new ButtonClickSound(this.add(0.5, 0.5, 0.5)));

                Vector3 pos = getLocation();
                level.updateAroundRedstone(pos, null);
                level.updateAroundRedstone(pos.getSide(getFacing().getOpposite()), null);
            }

            return NukkitLevel.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public boolean isActivated() {
        return ((this.meta & 0x08) == 0x08);
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
        int side = isActivated() ? meta ^ 0x08 : meta;
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
        return Item.get(this.getId(), 0, 1);
    }
}