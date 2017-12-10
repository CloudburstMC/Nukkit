package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;

/**
 * @author CreeperFace
 */
public class BlockBone extends BlockSolid {

    public BlockBone() {
        this(0);
    }

    public BlockBone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BONE_BLOCK;
    }

    @Override
    public String getName() {
        return "Bone BlockType";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{new ItemBlock(this)};
        }

        return new Item[0];
    }
}
