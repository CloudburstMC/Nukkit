package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.value.WoodType;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

@PowerNukkitOnly
public class BlockWoodBark extends BlockWood {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final String STRIPPED_BIT = "stripped_bit";

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(
            WoodType.PROPERTY,
            new BooleanBlockProperty(STRIPPED_BIT, true),
            PILLAR_AXIS
    );

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWoodBark() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWoodBark(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return WOOD_BARK;
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
        return (isStripped()? "Stripped ": "") + super.getName();
    }

    @Override
    public WoodType getWoodType() {
        return getPropertyValue(WoodType.PROPERTY);
    }

    @Override
    public void setWoodType(WoodType woodType) {
        setPropertyValue(WoodType.PROPERTY, woodType);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isStripped() {
        return getBooleanValue(STRIPPED_BIT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStripped(boolean stripped) {
        setBooleanValue(STRIPPED_BIT, stripped);
    }
}
