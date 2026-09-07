package cn.nukkit.block;

import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.properties.VanillaProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public abstract class BlockWallIndependentID extends BlockWall {

    protected static final BlockProperties PROPERTIES = new BlockProperties(
            VanillaProperties.WALL_CONNECTION_TYPE_EAST,
            VanillaProperties.WALL_CONNECTION_TYPE_NORTH,
            VanillaProperties.WALL_CONNECTION_TYPE_SOUTH,
            VanillaProperties.WALL_CONNECTION_TYPE_WEST,
            VanillaProperties.WALL_POST
    );

    public BlockWallIndependentID() {
        super();
    }

    public BlockWallIndependentID(int meta) {
        super(meta);
    }

    @Override
    public BlockProperties getBlockProperties() {
        return PROPERTIES;
    }

    @Override
    public WallType getWallType() {
        return WallType.COBBLESTONE;
    }

    @Override
    public void setWallType(WallType wallType) {
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }
}
