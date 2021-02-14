package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemString;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockCobweb extends BlockFlowable {
    public BlockCobweb() {
        this(0);
    }

    public BlockCobweb(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Cobweb";
    }

    @Override
    public int getId() {
        return COBWEB;
    }

    @Override
    public double getHardness() {
        return 4;
    }

    @Override
    public double getResistance() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SWORD;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    this.toItem()
            };
        } else if (item.isSword()) {
            return new Item[]{
                    new ItemString()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean diffusesSkyLight() {
        return true;
    }

}
