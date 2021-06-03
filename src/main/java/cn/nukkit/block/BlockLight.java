package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;

@PowerNukkitOnly
public class BlockLight extends BlockTransparentMeta {
    @PowerNukkitOnly
    public BlockLight() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockLight(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public int getId() {
        return LIGHT_BLOCK;
    }

    @Override
    public int getLightLevel() {
        return getDamage() & 0xF;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.AIR);
    }
}
