package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockGrowEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemSugarcane;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.particle.BoneMealParticle;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created by Pub4Game on 09.01.2016.
 */
public class BlockSugarcane extends BlockFlowable {

    public BlockSugarcane() {
        this(0);
    }

    public BlockSugarcane(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Sugarcane";
    }

    @Override
    public int getId() {
        return SUGARCANE_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemSugarcane();
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == 0x0F) { //Bonemeal
            int count = 1;

            for (int i = 1; i <= 2; i++) {
                int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());

                if (id == SUGARCANE_BLOCK) {
                    count++;
                }
            }

            if (count < 3) {
                int toGrow = 3 - count;

                for (int i = 1; i <= toGrow; i++) {
                    Block block = this.up(i);
                    if (block.getId() == 0) {
                        BlockGrowEvent ev = new BlockGrowEvent(block, new BlockSugarcane());
                        NukkitServer.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState(), true);
                        }
                    } else if (block.getId() != SUGARCANE_BLOCK) {
                        break;
                    }
                }
            }

            if ((player.gamemode & 0x01) == 0) {
                item.count--;
            }
            this.level.addParticle(new BoneMealParticle(this.add(0.5, 0.5, 0.5)));
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            if (down.isTransparent() && down.getId() != SUGARCANE_BLOCK) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.down().getId() != SUGARCANE_BLOCK) {
                if (this.meta == 0x0F) {
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                        if (b.getId() == AIR) {
                            this.getLevel().setBlock(b, new BlockSugarcane(), true);
                            break;
                        }
                    }
                    this.meta = 0;
                    this.getLevel().setBlock(this, this, true);
                } else {
                    ++this.meta;
                    this.getLevel().setBlock(this, this, true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.getId() != AIR) {
            return false;
        }
        Block down = this.down();
        if (down.getId() == SUGARCANE_BLOCK) {
            this.getLevel().setBlock(block, new BlockSugarcane(), true);
            return true;
        } else if (down.getId() == GRASS || down.getId() == DIRT || down.getId() == SAND) {
            Block block0 = down.north();
            Block block1 = down.south();
            Block block2 = down.west();
            Block block3 = down.east();
            if ((block0 instanceof BlockWater) || (block1 instanceof BlockWater) || (block2 instanceof BlockWater) || (block3 instanceof BlockWater)) {
                this.getLevel().setBlock(block, new BlockSugarcane(), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
