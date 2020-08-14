package cn.nukkit.block;

import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

/**
 * Created by CreeperFace on 10.4.2017.
 */
public class BlockRedstoneTorchUnlit extends BlockTorch {

    public BlockRedstoneTorchUnlit() {
        this(0);
    }

    public BlockRedstoneTorchUnlit(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Unlit Redstone Torch";
    }

    @Override
    public int getId() {
        return UNLIT_REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.REDSTONE_TORCH));
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return 0;
                }

                if (checkState()) {
                    return 1;
                }
            }
        }

        return 0;
    }

    protected boolean checkState() {
        BlockFace face = getBlockFace().getOpposite();
        Vector3 pos = getLocation();

        if (!this.level.isSidePowered(pos.getSide(face), face)) {
            this.level.setBlock(pos, Block.get(BlockID.REDSTONE_TORCH, getDamage()), false, true);

            for (BlockFace side : BlockFace.values()) {
                if (side == face) {
                    continue;
                }

                this.level.updateAroundRedstone(pos.getSide(side), null);
            }
            return true;
        }

        return false;
    }

    @Override
    public int tickRate() {
        return 2;
    }
}
