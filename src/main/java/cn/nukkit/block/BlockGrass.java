package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.DYE;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockGrass extends BlockDirt {

    public BlockGrass(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getDamage() == 0x0F) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.decrementCount();
            }
            this.level.addParticle(new BoneMealParticle(this));

            //porktodo: fix this
            //ObjectTallGrass.growGrass(this.getLevel(), this, new BedrockRandom());
            return true;
        } else if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(FARMLAND));
            return true;
        } else if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(GRASS_PATH));
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            NukkitRandom random = new NukkitRandom();
            x = random.nextRange(x - 1, x + 1);
            y = random.nextRange(y - 2, y + 2);
            z = random.nextRange(z - 1, z + 1);
            Block block = this.getLevel().getBlock(x, y, z);
            if (block.getId() == DIRT && block.getDamage() == 0) {
                if (block.up() instanceof BlockAir) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(GRASS));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState());
                    }
                }
            } else if (block.getId() == GRASS) {
                if (block.up() instanceof BlockSolid) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(DIRT));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState());
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
