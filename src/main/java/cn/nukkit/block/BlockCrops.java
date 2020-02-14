package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.FARMLAND;
import static cn.nukkit.item.ItemIds.DYE;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockCrops extends FloodableBlock {

    protected BlockCrops(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }


    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (block.down().getId() == FARMLAND) {
            this.getLevel().setBlock(block.getPosition(), this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        //Bone meal
        if (item.getId() == DYE && item.getMeta() == 0x0f) {
            if (this.getMeta() < 7) {
                BlockCrops block = (BlockCrops) this.clone();
                block.setDamage(block.getMeta() + ThreadLocalRandom.current().nextInt(3) + 2);
                if (block.getMeta() > 7) {
                    block.setDamage(7);
                }
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }

                this.getLevel().setBlock(this.getPosition(), ev.getNewState(), false, true);
                this.level.addParticle(new BoneMealParticle(this.getPosition()));

                if (player != null && (player.getGamemode() & 0x01) == 0) {
                    item.decrementCount();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this.getPosition());
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1) {
                if (this.getMeta() < 0x07) {
                    BlockCrops block = (BlockCrops) this.clone();
                    block.setDamage(block.getMeta() + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this.getPosition(), ev.getNewState(), false, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
