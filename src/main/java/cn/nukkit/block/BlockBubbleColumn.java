package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.block.BlockFormEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockBubbleColumn extends BlockTransparentMeta {

    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;

    public BlockBubbleColumn() {
        this(0);
    }

    public BlockBubbleColumn(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bubble Column";
    }

    @Override
    public int getId() {
        return BUBBLE_COLUMN;
    }

    @Override
    public double getResistance() {
        return 100;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getLevel().setBlock(this, this, true, true)) {
            this.getLevel().setBlock(this, Block.LAYER_WATERLOGGED, Block.get(Block.STILL_WATER), true, true);
            return true;
        }
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();

            if (down.getId() == BUBBLE_COLUMN) {
                if (down.getDamage() != this.getDamage()) {
                    this.getLevel().setBlock(this, down, false, true);
                }
            } else if (down.getId() == SOUL_SAND) {
                if (this.getDamage() != DIRECTION_UP) {
                    this.setDamage(DIRECTION_UP);
                    this.getLevel().setBlock(this, this, false, true);
                }
            } else if (down.getId() == MAGMA) {
                if (this.getDamage() != DIRECTION_DOWN) {
                    this.setDamage(DIRECTION_DOWN);
                    this.getLevel().setBlock(this, this, false, true);
                }
            } else {
                this.getLevel().setBlock(this, Block.get(WATER), false, true);
                return type;
            }

            Block up = this.up();
            if (up instanceof BlockWater && (up.getDamage() == 0 || up.getDamage() == 8)) {
                BlockFormEvent event = new BlockFormEvent(up, Block.get(BUBBLE_COLUMN, this.getDamage()));
                Server.getInstance().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.getLevel().setBlock(up, event.getNewState(), false, true);
                }
            }

            return type;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity.canBeMovedByCurrents()) {
            if (this.level.getBlockIdAt((int) this.x, (int) this.y + 1, (int) this.z) == AIR) {
                double motY = entity.motionY;

                if (this.getDamage() == 1) {
                    motY = Math.max(-0.9, motY - 0.03);
                } else {
                    if ((entity instanceof EntityCreature) && motY < -0.64f) {
                        motY = -0.16f;
                    }
                    motY = Math.min(1.8, motY + 0.1);
                }

                if (entity instanceof Player) {
                    ((Player) entity).setMotionLocally(entity.getMotion().setY(motY));
                } else {
                    entity.motionY = motY;
                }
            } else {
                double motY = entity.motionY;

                if (this.getDamage() == 1) {
                    motY = Math.max(-0.3, motY - 0.3);
                } else {
                    motY = Math.min(0.7, motY + 0.06);
                }

                if (entity instanceof Player) {
                    ((Player) entity).setMotionLocally(entity.getMotion().setY(motY));
                } else {
                    entity.motionY = motY;
                }
            }
            if (entity instanceof EntityItem) {
                entity.collisionBlocks = null;
            }
        }
    }
}
