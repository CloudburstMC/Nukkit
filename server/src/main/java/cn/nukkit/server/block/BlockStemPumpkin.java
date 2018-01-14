package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockGrowEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemSeedsPumpkin;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.BlockFace.Plane;
import cn.nukkit.server.math.NukkitRandom;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockStemPumpkin extends BlockCrops {

    public BlockStemPumpkin() {
        this(0);
    }

    public BlockStemPumpkin(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PUMPKIN_STEM;
    }

    @Override
    public String getName() {
        return "Pumpkin Stem";
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == NukkitLevel.BLOCK_UPDATE_RANDOM) {
            NukkitRandom random = new NukkitRandom();
            if (random.nextRange(1, 2) == 1) {
                if (this.meta < 0x07) {
                    Block block = this.clone();
                    ++block.meta;
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    NukkitServer.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true);
                    }
                    return NukkitLevel.BLOCK_UPDATE_RANDOM;
                } else {
                    for (BlockFace face : Plane.HORIZONTAL) {
                        Block b = this.getSide(face);
                        if (b.getId() == PUMPKIN) {
                            return NukkitLevel.BLOCK_UPDATE_RANDOM;
                        }
                    }
                    Block side = this.getSide(Plane.HORIZONTAL.random(random));
                    Block d = side.down();
                    if (side.getId() == AIR && (d.getId() == FARMLAND || d.getId() == GRASS || d.getId() == DIRT)) {
                        BlockGrowEvent ev = new BlockGrowEvent(side, new BlockPumpkin());
                        NukkitServer.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(side, ev.getNewState(), true);
                        }
                    }
                }
            }
            return NukkitLevel.BLOCK_UPDATE_RANDOM;
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        NukkitRandom random = new NukkitRandom();
        return new Item[]{
                new ItemSeedsPumpkin(0, random.nextRange(0, 3))
        };
    }
}
