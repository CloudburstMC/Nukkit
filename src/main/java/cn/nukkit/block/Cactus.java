package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Color;

/**
 * @author Nukkit Project Team
 */
public class Cactus extends Transparent {

    public Cactus(int meta) {
        super(meta);
    }

    public Cactus() {
        this(0);
    }

    @Override
    public int getId() {
        return Block.CACTUS;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x + 0.0625,
                this.y + 0.0625,
                this.z + 0.0625,
                this.x + 0.9375,
                this.y + 0.9375,
                this.z + 0.9375
        );
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.CAUSE_CONTACT, 1));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = getSide(0);
            if (down.getId() != SAND && down.getId() != CACTUS) {
                this.getLevel().useBreakOn(this);
            } else {
                for (int side = 2; side <= 5; ++side) {
                    Block block = getSide(side);
                    if (!block.canBeFlowedInto()) {
                        this.getLevel().useBreakOn(this);
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getSide(0).getId() != CACTUS) {
                if (this.meta == 0x0F) {
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                        if (b.getId() == AIR) {
                            BlockGrowEvent event = new BlockGrowEvent(b, new Cactus());
                            Server.getInstance().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                this.getLevel().setBlock(b, event.getNewState(), true);
                            }
                        }
                    }
                    this.meta = 0;
                    this.getLevel().setBlock(this, this);
                } else {
                    ++this.meta;
                    this.getLevel().setBlock(this, this);
                }
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if (down.getId() == SAND || down.getId() == CACTUS) {
            Block block0 = getSide(2);
            Block block1 = getSide(3);
            Block block2 = getSide(4);
            Block block3 = getSide(5);
            if (block0.isTransparent() && block1.isTransparent() && block2.isTransparent() && block3.isTransparent()) {
                this.getLevel().setBlock(this, this, true);

                return true;
            }
        }
        return false;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{this.getId(), 0, 1}};
    }

    @Override
    public String getName() {
        return "Cactus";
    }

    @Override
    public Color getMapColor() {
        return Color.foliageColor;
    }
}
