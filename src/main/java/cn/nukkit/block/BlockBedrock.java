package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

/**
 * @author Angelic47 (Nukkit Project)
 * @apiNote Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit")
public class BlockBedrock extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty INFINIBURN = new BooleanBlockProperty("infiniburn_bit", true);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(INFINIBURN);

    public BlockBedrock() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBedrock(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEDROCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBurnIndefinitely() {
        return getBooleanValue(INFINIBURN);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setBurnIndefinitely(boolean infiniburn) {
        setBooleanValue(INFINIBURN, infiniburn);
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public String getName() {
        return "Bedrock";
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
