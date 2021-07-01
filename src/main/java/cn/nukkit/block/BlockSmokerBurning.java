package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySmoker;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockSmokerBurning extends BlockFurnaceBurning {
    @PowerNukkitOnly
    public BlockSmokerBurning() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockSmokerBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_SMOKER;
    }

    @Override
    public String getName() {
        return "Burning Smoker";
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.SMOKER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntitySmoker> getBlockEntityClass() {
        return BlockEntitySmoker.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSmoker());
    }
}
