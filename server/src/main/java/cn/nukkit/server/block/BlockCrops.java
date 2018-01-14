package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockGrowEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.particle.BoneMealParticle;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockCrops extends BlockFlowable {

    protected BlockCrops(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.down().getId() == FARMLAND) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        //Bone meal
        if (item.getId() == Item.DYE && item.getDamage() == 0x0f) {
            BlockCrops block = (BlockCrops) this.clone();
            if (this.meta < 7) {
                block.meta += new Random().nextInt(3) + 2;
                if (block.meta > 7) {
                    block.meta = 7;
                }
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                NukkitServer.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }

                this.getLevel().setBlock(this, ev.getNewState(), true, true);
            }

            this.level.addParticle(new BoneMealParticle(this.add(0.5, 0.5, 0.5)));
            item.count--;
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == NukkitLevel.BLOCK_UPDATE_RANDOM) {
            if (new Random().nextInt(2) == 1) {
                if (this.meta < 0x07) {
                    BlockCrops block = (BlockCrops) this.clone();
                    ++block.meta;
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    NukkitServer.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true);
                    } else {
                        return NukkitLevel.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return NukkitLevel.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
