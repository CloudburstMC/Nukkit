package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.DYE;

/**
 * Created by Pub4Game on 09.01.2016.
 */
public class ReedsBlock extends FloodableBlock {

    public ReedsBlock(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.REEDS);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getMeta() == 0x0F) { //Bonemeal
            int count = 1;

            for (int i = 1; i <= 2; i++) {
                Identifier id = this.level.getBlockId(this.getX(), this.getY() - i, this.getZ());

                if (id == REEDS) {
                    count++;
                }
            }

            if (count < 3) {
                boolean success = false;
                int toGrow = 3 - count;

                for (int i = 1; i <= toGrow; i++) {
                    Block block = this.up(i);
                    if (block.getId() == AIR) {
                        BlockGrowEvent ev = new BlockGrowEvent(block, Block.get(REEDS));
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block.getPosition(), ev.getNewState(), true);
                            success = true;
                        }
                    } else if (block.getId() != REEDS) {
                        break;
                    }
                }

                if (success) {
                    if (player != null && (player.getGamemode() & 0x01) == 0) {
                        item.decrementCount();
                    }

                    this.level.addParticle(new BoneMealParticle(this.getPosition()));
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            if (down.isTransparent() && down.getId() != REEDS) {
                this.getLevel().useBreakOn(this.getPosition());
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.down().getId() != REEDS) {
                if (this.getMeta() == 0x0F) {
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(this.getX(), this.getY() + y, this.getZ());
                        if (b.getId() == AIR) {
                            this.getLevel().setBlock(b.getPosition(), Block.get(REEDS), false);
                            break;
                        }
                    }
                    this.setDamage(0);
                    this.getLevel().setBlock(this.getPosition(), this, false);
                } else {
                    this.setDamage(this.getMeta() + 1);
                    this.getLevel().setBlock(this.getPosition(), this, false);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (block.getId() != AIR) {
            return false;
        }
        Block down = this.down();
        if (down.getId() == REEDS) {
            this.getLevel().setBlock(block.getPosition(), Block.get(REEDS), true);
            return true;
        } else if (down.getId() == GRASS || down.getId() == DIRT || down.getId() == SAND) {
            Block block0 = down.north();
            Block block1 = down.south();
            Block block2 = down.west();
            Block block3 = down.east();
            if ((block0 instanceof BlockWater) || (block1 instanceof BlockWater) || (block2 instanceof BlockWater) || (block3 instanceof BlockWater)) {
                this.getLevel().setBlock(block.getPosition(), Block.get(REEDS), true);
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
