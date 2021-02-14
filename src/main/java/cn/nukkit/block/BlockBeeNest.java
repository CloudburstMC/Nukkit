package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
public class BlockBeeNest extends BlockBeehive {
    public BlockBeeNest() {
        this(0);
    }

    protected BlockBeeNest(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEE_NEST;
    }

    @Override
    public String getName() {
        return "Bee Nest";
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
