package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockCactus extends BlockTransparentMeta {

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
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
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
                for (BlockFace side : BlockFace.Plane.HORIZONTAL) {
                    if (!getSide(side).canBeFlowedInto()) {
                        this.getLevel().useBreakOn(this);
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId() != CACTUS) {
                if (this.getDamage() == 0x0F) {
                    FullChunk chunk = this.level.getChunk((int) x >> 4, (int) z >> 4);
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(chunk, (int) this.x, (int) this.y + y, (int) this.z, true);
                        if (b.getId() == AIR) {
                            BlockGrowEvent event = new BlockGrowEvent(b, Block.get(CACTUS));
                            Server.getInstance().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                this.getLevel().setBlock(b, event.getNewState(), true, true);
                            }
                            break;
                        }
                    }
                    this.setDamage(0);
                } else {
                    this.setDamage(this.getDamage() + 1);
                }
                this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, true, false); // No need to send this to client
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
            if (block0.canBeFlowedInto() && block1.canBeFlowedInto() && block2.canBeFlowedInto() && block3.canBeFlowedInto()) {
                this.getLevel().setBlock(this, this, true, true);
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

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.CACTUS, 0, 1)
        };
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
