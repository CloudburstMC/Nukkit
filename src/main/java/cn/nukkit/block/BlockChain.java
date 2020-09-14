package cn.nukkit.block;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemChain;
import cn.nukkit.item.ItemTool;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockChain extends BlockTransparent {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockChain() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Chain";
    }

    @Override
    public int getId() {
        return CHAIN_BLOCK;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return x + 7/16.0;
    }

    @Override
    public double getMaxX() {
        return x + 9/16.0;
    }

    @Override
    public double getMinZ() {
        return z + 7/16.0;
    }

    @Override
    public double getMaxZ() {
        return z + 9/16.0;
    }

    @Override
    public Item toItem() {
        return new ItemChain();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
}
