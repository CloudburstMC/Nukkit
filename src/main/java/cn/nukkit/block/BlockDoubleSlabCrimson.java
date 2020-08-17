package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

public class BlockDoubleSlabCrimson extends BlockDoubleSlabBase {

    public BlockDoubleSlabCrimson() {
        super(0);
    }

    @Override
    public int getId() {
        return CRIMSON_DOUBLE_SLAB;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Crimson";
    }

    @Override
    public int getSingleSlabId() {
        return CRIMSON_SLAB;
    }

    //TODO Adjust or remove this when merging https://github.com/PowerNukkit/PowerNukkit/pull/370
    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }

}
