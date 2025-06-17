package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockMudBrickWall extends BlockWall {

    public BlockMudBrickWall() {
        this(0);
    }

    public BlockMudBrickWall(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mud Brick Wall";
    }

    @Override
    public int getId() {
        return MUD_BRICK_WALL;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
