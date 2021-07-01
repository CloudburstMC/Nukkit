package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.CoralType;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PERMANENTLY_DEAD;

@PowerNukkitOnly
public class BlockCoralFanHang3 extends BlockCoralFanHang {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<CoralType> HANG3_TYPE = new ArrayBlockProperty<>("coral_hang_type_bit", true,
            new CoralType[]{CoralType.YELLOW}
    ).ordinal(true);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(HANG3_TYPE, PERMANENTLY_DEAD, HANG_DIRECTION);

    @PowerNukkitOnly
    public BlockCoralFanHang3() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockCoralFanHang3(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CORAL_FAN_HANG3;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getType() {
        return BlockCoral.TYPE_HORN;
    }
}
