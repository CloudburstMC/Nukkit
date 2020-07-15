package cn.nukkit.blockproperty;

import cn.nukkit.block.Block;
import cn.nukkit.math.BlockFace;

public final class CommonBlockProperties {
    public static final String LEGACY_PROPERTY_NAME = "nukkit-legacy";
    
    public static final BlockProperties EMPTY_PROPERTIES = new BlockProperties();
    public static final BlockProperties LEGACY_PROPERTIES = new BlockProperties(new IntBlockProperty(LEGACY_PROPERTY_NAME, true, Block.DATA_MASK));
    public static final BlockProperties LEGACY_BIG_PROPERTIES = new BlockProperties(new UnsignedIntBlockProperty(LEGACY_PROPERTY_NAME, true, 0xFFFFFFFF));
    
    public static final BlockProperty<BlockFace> FACING_DIRECTION = new ArrayBlockProperty<>("facing_direction", false, new BlockFace[] {
            BlockFace.fromIndex(0), BlockFace.fromIndex(1),
            BlockFace.fromIndex(2), BlockFace.fromIndex(3),
            BlockFace.fromIndex(4), BlockFace.fromIndex(5),
    }).ordinal(true);
    
    public static final BlockProperty<BlockFace> DIRECTION = new ArrayBlockProperty<>("facing_direction", false, new BlockFace[]{
            BlockFace.fromHorizontalIndex(0), BlockFace.fromHorizontalIndex(1),
            BlockFace.fromHorizontalIndex(2), BlockFace.fromHorizontalIndex(3),
    }).ordinal(true);

    private CommonBlockProperties() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
