package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.CoralType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PERMANENTLY_DEAD;

@PowerNukkitOnly
public class BlockCoralFanHang extends BlockCoralFan implements Faceable {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<CoralType> HANG1_TYPE = new ArrayBlockProperty<>("coral_hang_type_bit", true,
            new CoralType[]{CoralType.BLUE, CoralType.PINK}
    ).ordinal(true);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<BlockFace> HANG_DIRECTION = new ArrayBlockProperty<>("coral_direction", false,
            new BlockFace[]{BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH}
    ).ordinal(true);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(HANG1_TYPE, PERMANENTLY_DEAD, HANG_DIRECTION);

    @PowerNukkitOnly
    public BlockCoralFanHang() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockCoralFanHang(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CORAL_FAN_HANG;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
