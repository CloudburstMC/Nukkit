package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

public class BlockCoralFanHang extends BlockCoralFan implements Faceable {
    public BlockCoralFanHang() {
        this(0);
    }
    
    public BlockCoralFanHang(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CORAL_FAN_HANG;
    }
    
    @Override
    public String getName() {
        String name = super.getName();
        name = name.substring(0, name.length() - 4);
        if (isDead()) {
            return "Dead " + name + " Wall Fan";
        } else {
            return name + " Wall Fan";
        }
    }
    
    @Override
    public boolean isDead() {
        return (getDamage() & 0b10) == 0b10;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            return type;
        } else {
            return super.onUpdate(type);
        }
    }
    
    @Override
    public int getType() {
        if ((getDamage() & 0b1) == 0) {
            return BlockCoral.TYPE_TUBE;
        } else {
            return BlockCoral.TYPE_BRAIN;
        }
    }
    
    @Override
    public BlockFace getBlockFace() {
        int face = getDamage() >> 2 & 0x3;
        switch (face) {
            case 0:
                return BlockFace.WEST;
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.NORTH;
            default:
            case 3:
                return BlockFace.SOUTH;
        }
    }
    
    @Override
    public BlockFace getRootsFace() {
        return getBlockFace().getOpposite();
    }
    
    @Override
    public Item toItem() {
        return new ItemBlock(isDead()? new BlockCoralFanDead() : new BlockCoralFan(), getType());
    }
}
