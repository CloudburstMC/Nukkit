package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockGrowEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemNetherWart;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

import java.util.Random;

/**
 * Created by Leonidius20 on 22.03.17.
 */
public class BlockNetherWart extends BlockFlowable {

    public BlockNetherWart() {
        this(0);
    }

    public BlockNetherWart(int meta) {
        super(meta);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId() == SOUL_SAND) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != SOUL_SAND) {
                this.getLevel().useBreakOn(this);
                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == NukkitLevel.BLOCK_UPDATE_RANDOM) {
            if (new Random().nextInt(10) == 1) {
                if (this.meta < 0x03) {
                    BlockNetherWart block = (BlockNetherWart) this.clone();
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

    @Override
    public String getName() {
        return "Nether Wart BlockType";
    }

    @Override
    public int getId() {
        return NETHER_WART_BLOCK;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.meta == 0x03) {
            return new Item[]{
                    new ItemNetherWart(0, 2 + (int) (Math.random() * ((4 - 2) + 1)))
            };
        } else {
            return new Item[]{
                    new ItemNetherWart()
            };
        }
    }

    @Override
    public Item toItem() {
        return new ItemNetherWart();
    }
}


