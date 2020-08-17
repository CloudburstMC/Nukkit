package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBlastFurnace;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockBlastFurnaceBurning extends BlockFurnaceBurning {
    public BlockBlastFurnaceBurning() {
        this(0);
    }

    public BlockBlastFurnaceBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_BLAST_FURNACE;
    }

    @Override
    public String getName() {
        return "Burning Blast Furnace";
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.BLAST_FURNACE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityBlastFurnace> getBlockEntityClass() {
        return BlockEntityBlastFurnace.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBlastFurnace());
    }
}
