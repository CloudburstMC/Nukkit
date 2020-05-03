package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.concurrent.ThreadLocalRandom;

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

            PRandom random = new FastPRandom();

            for (int i = 0; i < 64; i++) {
                int blockY = this.getY() + random.nextInt(4) - random.nextInt(4);
                if (blockY < 0 || blockY >= 255) {
                    continue;
                }
                int blockX = this.getX() + random.nextInt(8) - random.nextInt(8);
                int blockZ = this.getZ() + random.nextInt(8) - random.nextInt(8);

                Block tallGrass = Block.get(BlockIds.TALL_GRASS, 0, blockX, blockY + 1, blockZ, this.level);
                Block toReplace = this.level.getBlock(blockX, blockY + 1, blockZ);
                if (toReplace.getId() == BlockIds.AIR)  {
                    tallGrass.place(null, toReplace, null, null, null, null);
                }
            }
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
            Vector3i pos = this.getPosition();
            int x = ThreadLocalRandom.current().nextInt(pos.getX() - 1, pos.getX() + 1);
            int y = ThreadLocalRandom.current().nextInt(pos.getY() - 2, pos.getY() + 2);
            int z = ThreadLocalRandom.current().nextInt(pos.getZ() - 1, pos.getZ() + 1);
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
