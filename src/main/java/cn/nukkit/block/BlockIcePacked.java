package cn.nukkit.block;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockIcePacked extends BlockIce {

    public BlockIcePacked() {
    }

    @Override
    public int getId() {
        return PACKED_ICE;
    }

    @Override
    public String getName() {
        return "Packed Ice";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean onBreak(Item item) {
        return this.getLevel().setBlock(this, new BlockAir(), true);
    }

    @Override
    public Item[] getDrops(Item item) {
        if(item.hasEnchantment(Enchantment.ID_SILK_TOUCH)==true) {
            return new Item[]{
                    this.toItem()
            };
        }
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        return 0; //not being melted
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
