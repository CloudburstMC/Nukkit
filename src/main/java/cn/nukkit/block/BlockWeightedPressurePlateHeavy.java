package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * @author CreeperFace
 */
public class BlockWeightedPressurePlateHeavy extends BlockPressurePlateBase {

    public BlockWeightedPressurePlateHeavy(Identifier id) {
        super(id);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 2.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    protected int computeRedstoneStrength() {
        int count = Math.min(this.level.getCollidingEntities(getCollisionBoundingBox()).size(), this.getMaxWeight());

        if (count > 0) {
            float f = (float) Math.min(this.getMaxWeight(), count) / (float) this.getMaxWeight();
            return Math.max(1, NukkitMath.ceilFloat(f * 15.0F));
        } else {
            return 0;
        }
    }

    public int getMaxWeight() {
        return 150;
    }
}
