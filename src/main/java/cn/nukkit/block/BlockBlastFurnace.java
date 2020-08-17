package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockBlastFurnace extends BlockBlastFurnaceBurning {
    public BlockBlastFurnace() {
        this(0);
    }

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
