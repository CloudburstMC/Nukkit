package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.math.NukkitMath;
import cn.nukkit.server.utils.BlockColor;

/**
 * @author CreeperFace
 */
public class BlockWeightedPressurePlateHeavy extends BlockPressurePlateBase {

    public BlockWeightedPressurePlateHeavy() {
        this(0);
    }

    public BlockWeightedPressurePlateHeavy(int meta) {
        super(meta);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    public int getId() {
        return HEAVY_WEIGHTED_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Weighted Pressure Plate (Heavy)";
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
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    protected int computeRedstoneStrength() {
        int count = Math.min(this.level.getCollidingEntities(getCollisionBoundingBox()).length, this.getMaxWeight());

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
