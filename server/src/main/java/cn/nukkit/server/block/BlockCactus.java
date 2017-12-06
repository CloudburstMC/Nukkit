package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockGrowEvent;
import cn.nukkit.api.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.api.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockCactus extends BlockTransparent {

    public BlockCactus(int meta) {
        super(meta);
    }

    public BlockCactus() {
        this(0);
    }

    @Override
    public int getId() {
        return CACTUS;
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
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new AxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (down.getId() != SAND && down.getId() != CACTUS) {
                this.getLevel().useBreakOn(this);
            } else {
                for (int side = 2; side <= 5; ++side) {
                    Block block = getSide(BlockFace.fromIndex(side));
                    if (!block.canBeFlowedInto()) {
                        this.getLevel().useBreakOn(this);
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId() != CACTUS) {
                if (this.meta == 0x0F) {
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                        if (b.getId() == AIR) {
                            BlockGrowEvent event = new BlockGrowEvent(b, new BlockCactus());
                            NukkitServer.getInstance().getPluginManager().callEvent(event);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId() == SAND || down.getId() == CACTUS) {
            Block block0 = north();
            Block block1 = south();
            Block block2 = west();
            Block block3 = east();
            if (block0.isTransparent() && block1.isTransparent() && block2.isTransparent() && block3.isTransparent()) {
                this.getLevel().setBlock(this, this, true);

                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Cactus";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
