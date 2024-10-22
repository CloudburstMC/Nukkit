package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoralFan extends BlockCoral implements Faceable {

    private static final String[] NAMES = {
            "Tube Coral Fan",
            "Brain Coral Fan",
            "Bubble Coral Fan",
            "Fire Coral Fan",
            "Horn Coral Fan"
    };

    public BlockCoralFan() {
        this(0);
    }

    public BlockCoralFan(int meta) {
        super(meta);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block side = this.getSide(this.getRootsFace());
            if (!side.isSolid() || side.getId() == MAGMA || side.getId() == SOUL_SAND) {
                this.getLevel().useBreakOn(this);
            } else {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            Block side = this.getSide(this.getRootsFace());
            if (side.getId() == ICE) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            if (!this.isDead() && !(this.getLevelBlock(BlockLayer.WATERLOGGED) instanceof BlockWater) && !(this.getLevelBlock(BlockLayer.WATERLOGGED) instanceof BlockIceFrosted)) {
                BlockFadeEvent event = new BlockFadeEvent(this, Block.get(CORAL_FAN_DEAD, this.getDamage()));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if ((this.getDamage() & 0x8) == 0) {
                this.setDamage(this.getDamage() | 0x8);
            } else {
                this.setDamage(this.getDamage() ^ 0x8);
            }
            this.getLevel().setBlock(this, this, true, true);
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        Block layer1 = block.getLevelBlock(BlockLayer.WATERLOGGED);
        boolean hasWater = layer1 instanceof BlockWater;
        if (layer1.getId() != Block.AIR && (!hasWater || layer1.getDamage() != 0 && layer1.getDamage() != 8)) {
            return false;
        }

        if (hasWater && layer1.getDamage() == 8) {
            this.getLevel().setBlock(this, BlockLayer.WATERLOGGED, Block.get(WATER), true, false);
        }

        if (!target.isSolid() || target.getId() == MAGMA || target.getId() == SOUL_SAND) {
            return false;
        }

        if (face == BlockFace.UP) {
            double rotation = player.yaw % 360;
            if (rotation < 0) {
                rotation += 360.0;
            }
            int axisBit = rotation >= 0 && rotation < 12 || (342 <= rotation && rotation < 360)? 0x0 : 0x8;
            this.setDamage(this.getDamage() & 0x7 | axisBit);
            this.getLevel().setBlock(this, BlockLayer.NORMAL, hasWater ? new BlockCoralFan(this.getDamage()) : new BlockCoralFanDead(this.getDamage()), true, true);
        } else {
            int type = this.getType();
            int typeBit = type % 2;
            int deadBit = this.isDead()? 0x1 : 0;
            int faceBit;
            switch (face) {
                case WEST:
                    faceBit = 0;
                    break;
                case EAST:
                    faceBit = 1;
                    break;
                case NORTH:
                    faceBit = 2;
                    break;
                default:
                case SOUTH:
                    faceBit = 3;
                    break;
            }
            int deadData = faceBit << 2 | deadBit << 1 | typeBit;
            int deadBlockId;
            switch (type) {
                default:
                case BlockCoral.TYPE_TUBE:
                case BlockCoral.TYPE_BRAIN:
                    deadBlockId = CORAL_FAN_HANG;
                    break;
                case BlockCoral.TYPE_BUBBLE:
                case BlockCoral.TYPE_FIRE:
                    deadBlockId = CORAL_FAN_HANG2;
                    break;
                case BlockCoral.TYPE_HORN:
                    deadBlockId = CORAL_FAN_HANG3;
                    break;
            }
            this.getLevel().setBlock(this, BlockLayer.NORMAL, Block.get(deadBlockId, deadData), true, true);
        }
        return true;
    }


    @Override
    public String getName() {
        int variant = this.getType();
        String name;
        if (variant >= NAMES.length) {
            name = NAMES[0];
        } else {
            name = NAMES[variant];
        }
        return name;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public int getId() {
        return CORAL_FAN;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(this.getItemId(), this.getDamage() ^ 0x8);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return new Item[0];
        }
    }

    public boolean isDead() {
        return false;
    }

    public int getType() {
        return this.getDamage() & 0x7;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    public BlockFace getRootsFace() {
        return BlockFace.DOWN;
    }
}
