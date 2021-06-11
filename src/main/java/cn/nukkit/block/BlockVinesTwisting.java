package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * Properties and behaviour definitions of the {@link BlockID#TWISTING_VINES} block.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockVinesTwisting extends BlockVinesNether {
    /**
     * Increments for every block the twisting vine grows.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty TWISTING_VINES_AGE = new IntBlockProperty(
            "twisting_vines_age", false, 25);

    /**
     * Holds the {@code twisting_vines} block property definitions.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(TWISTING_VINES_AGE);

    /**
     * Creates a {@code twisting_vine} with age {@code 0}.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVinesTwisting() {
        // Does nothing
    }

    /**
     * Creates a {@code twisting_vine} from a meta compatible with {@link #getProperties()}.
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVinesTwisting(int meta) throws InvalidBlockPropertyMetaException {
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
        return TWISTING_VINES;
    }

    @Override
    public String getName() {
        return "Twisting Vines";
    }

    @Nonnull
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockFace getGrowthDirection() {
        return BlockFace.UP;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getVineAge() {
        return getIntValue(TWISTING_VINES_AGE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setVineAge(int vineAge) throws InvalidBlockPropertyValueException {
        setIntValue(TWISTING_VINES_AGE, vineAge);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getMaxVineAge() {
        return TWISTING_VINES_AGE.getMaxValue();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
