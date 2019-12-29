package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoralFan extends BlockFlowable implements Faceable {
    public BlockCoralFan() {
        this(0);
    }
    
    public BlockCoralFan(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CORAL_FAN;
    }
    
    @Override
    public String getName() {
        String[] names = new String[] {
                "Tube Coral Fan",
                "Brain Coral Fan",
                "Bubble Coral Fan",
                "Fire Coral Fan",
                "Horn Coral Fan"
        };
        return names[getType()];
    }
    
    @Override
    public BlockColor getColor() {
        BlockColor[] colors = new BlockColor[] {
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.PINK_BLOCK_COLOR,
                BlockColor.PURPLE_BLOCK_COLOR,
                BlockColor.RED_BLOCK_COLOR,
                BlockColor.YELLOW_BLOCK_COLOR
        };
        return colors[getType()];
    }
    
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }
    
    public boolean isDead() {
        return false;
    }
    
    public int getType() {
        return getDamage() & 0x7;
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(((getDamage() & 0x8) >> 3) + 1);
    }
    
    public BlockFace getRootsFace() {
        return BlockFace.DOWN;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!getSide(getRootsFace()).isSolid()) {
                this.getLevel().useBreakOn(this);
            } else {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead() && !(getLevelBlockAtLayer(1) instanceof BlockWater)) {
                this.getLevel().setBlock(this, new BlockCoralFanDead(getDamage()), true, true);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if ((getDamage() & 0x8) == 0) {
                setDamage(getDamage() | 0x8);
            } else {
                setDamage(getDamage() ^ 0x8);
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
        
        Block layer1 = block.getLevelBlockAtLayer(1);
        boolean hasWater = layer1 instanceof BlockWater;
        if (layer1.getId() != Block.AIR && (!hasWater || layer1.getDamage() != 0)) {
            return false;
        }
        
        if (face == BlockFace.UP) {
            if (target.isSolid()) {
                double rotation = player.yaw % 360;
                if (rotation < 0) {
                    rotation += 360.0;
                }
                int axisBit = rotation >= 0 && rotation < 12 || (342 <= rotation && rotation < 360)? 0x0 : 0x8;
                setDamage(getDamage() & 0x7 | axisBit);
                this.getLevel().setBlock(this, 0, hasWater? new BlockCoralFan(getDamage()) : new BlockCoralFanDead(getDamage()), true, true);
                return true;
            }
        } else if (target.isSolid()) {
            int type = getType();
            int typeBit = type % 2;
            int deadBit = isDead()? 0x1 : 0;
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
            this.getLevel().setBlock(this, 0, Block.get(deadBlockId, deadData), true, true);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }
    
    @Override
    public Item toItem() {
        return Item.get(getItemId(), getDamage() ^ 0x8);
    }
    
    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return new Item[0];
        }
    }
}
