package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.ObjectTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;

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
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getMeta() == 0x0F) {
            if (player != null && (player.getGamemode() & 0x01) == 0) {
                item.decrementCount();
            }
            this.level.addParticle(new BoneMealParticle(this.getPosition()));
            ObjectTallGrass.growGrass(this.getLevel(), this.getPosition(), new BedrockRandom());
            return true;
        } else if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this.getPosition(), Block.get(FARMLAND));
            return true;
        } else if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this.getPosition(), Block.get(GRASS_PATH));
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            NukkitRandom random = new NukkitRandom();
            Vector3i pos = this.getPosition();
            int x = random.nextRange(pos.getX() - 1, pos.getX() + 1);
            int y = random.nextRange(pos.getY() - 2, pos.getY() + 2);
            int z = random.nextRange(pos.getZ() - 1, pos.getZ() + 1);
            Block block = this.getLevel().getBlock(x, y, z);
            if (block.getId() == DIRT && block.getMeta() == 0) {
                if (block.up() instanceof BlockAir) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(GRASS));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block.getPosition(), ev.getNewState());
                    }
                }
            } else if (block.getId() == GRASS) {
                if (block.up() instanceof BlockSolid) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(DIRT));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block.getPosition(), ev.getNewState());
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
