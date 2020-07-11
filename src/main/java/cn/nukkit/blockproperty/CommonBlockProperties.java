package cn.nukkit.blockproperty;

import cn.nukkit.block.Block;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonBlockProperties {
    public final String LEGACY_PROPERTY_NAME = "nukkit-legacy";
    
    public final BlockProperties EMPTY_PROPERTIES = new BlockProperties();
    public final BlockProperties LEGACY_PROPERTIES = new BlockProperties(new IntBlockProperty(LEGACY_PROPERTY_NAME, Block.DATA_MASK));
    public final BlockProperties LEGACY_BIG_PROPERTIES = new BlockProperties(new UnsignedIntBlockProperty(LEGACY_PROPERTY_NAME, 0xFFFFFFFF));
}
