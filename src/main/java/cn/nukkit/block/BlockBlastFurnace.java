package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockBlastFurnace extends BlockBlastFurnaceBurning {
    @PowerNukkitOnly
    public BlockBlastFurnace() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockBlastFurnace(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    @Override
    public int getId() {
        return BLAST_FURNACE;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
