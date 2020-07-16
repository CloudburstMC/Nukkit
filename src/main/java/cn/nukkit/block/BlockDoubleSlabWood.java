package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase only in PowerNukkit")
public class BlockDoubleSlabWood extends BlockDoubleSlabBase {

    public BlockDoubleSlabWood() {
        this(0);
    }

    public BlockDoubleSlabWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_WOOD_SLAB;
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockSlabWood.PROPERTIES;
    }

    public WoodType getWoodType() {
        return getPropertyValue(WoodType.PROPERTY);
    }

    public void setWoodType(WoodType type) {
        setPropertyValue(WoodType.PROPERTY, type);
    }

    @Override
    public String getSlabName() {
        return getWoodType().getEnglishName();
    }

    @Override
    public String getName() {
        return "Double "+getSlabName()+" Wood Slab";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return getCurrentState().forItem().withBlockId(BlockID.WOOD_SLAB).asItemBlock();
    }

    @Override
    public Item[] getDrops(Item item) {
        Item slab = toItem();
        slab.setCount(2);
        return new Item[]{ slab };
    }

    @Override
    public BlockColor getColor() {
        return getWoodType().getColor();
    }
}
