package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author Nukkit Project Team
 */
public class BlockCactus extends BlockTransparent {

    public BlockCactus(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 0.4f;
    }

    @Override
    public float getResistance() {
        return 2;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public float getMinX() {
        return this.getX() + 0.0625f;
    }

    @Override
    public float getMinY() {
        return this.getY();
    }

    @Override
    public float getMinZ() {
        return this.getZ() + 0.0625f;
    }

    @Override
    public float getMaxX() {
        return this.getX() + 0.9375f;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.9375f;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + 0.9375f;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.getX(), this.getY(), this.getZ(), this.getX() + 1, this.getY() + 1, this.getZ() + 1);
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
                this.getLevel().useBreakOn(this.getPosition());
            } else {
                for (int side = 2; side <= 5; ++side) {
                    Block block = getSide(BlockFace.fromIndex(side));
                    if (!block.canBeFlooded()) {
                        this.getLevel().useBreakOn(this.getPosition());
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId() != CACTUS) {
                if (this.getMeta() == 0x0F) {
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(this.getX(), this.getY() + y, this.getZ());
                        if (b.getId() == AIR) {
                            BlockGrowEvent event = new BlockGrowEvent(b, Block.get(CACTUS));
                            Server.getInstance().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                this.getLevel().setBlock(b.getPosition(), event.getNewState(), true);
                            }
                        }
                    }
                    this.setMeta(0);
                    this.getLevel().setBlock(this.getPosition(), this);
                } else {
                    this.setMeta(this.getMeta() + 1);
                    this.getLevel().setBlock(this.getPosition(), this);
                }
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Block down = this.down();
        if (!block.isWaterlogged() && (down.getId() == SAND || down.getId() == CACTUS)) {
            Block block0 = north();
            Block block1 = south();
            Block block2 = west();
            Block block3 = east();
            if (block0.canBeFlooded() && block1.canBeFlooded() && block2.canBeFlooded() && block3.canBeFlooded()) {
                this.getLevel().setBlock(this.getPosition(), this, true);

                return true;
            }
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(CACTUS, 0, 1)
        };
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
