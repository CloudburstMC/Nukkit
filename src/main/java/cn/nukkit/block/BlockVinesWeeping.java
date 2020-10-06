package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * Properties and behaviour definitions of the {@link BlockID#WEEPING_VINES} block.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockVinesWeeping extends BlockVinesNether {
    /**
     * Increments for every block the weeping vine grows.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty WEEPING_VINES_AGE = new IntBlockProperty(
            "weeping_vines_age", false, 25);

    /**
     * Holds the {@code weeping_vines} block property definitions.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(WEEPING_VINES_AGE);

    /**
     * Creates a {@code weeping_vine} with age {@code 0}.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVinesWeeping() {
    }

    /**
     * Creates a {@code weeping_vine} from a meta compatible with {@link #getProperties()}.
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVinesWeeping(int meta) throws InvalidBlockPropertyMetaException {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getId() {
        return WEEPING_VINES;
    }

    @Override
    public String getName() {
        return "Weeping Vines";
    }

    @Nonnull
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockFace getGrowthDirection() {
        return BlockFace.DOWN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getVineAge() {
        return getIntValue(WEEPING_VINES_AGE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setVineAge(int vineAge) {
        setIntValue(WEEPING_VINES_AGE, vineAge);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getMaxVineAge() {
        return WEEPING_VINES_AGE.getMaxValue();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
