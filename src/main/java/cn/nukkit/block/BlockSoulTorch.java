package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockSoulTorch extends BlockTorch {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSoulTorch() {
        this(0);
    }

    public BlockSoulTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Soul Torch";
    }

    @Override
    public int getId() {
        return SOUL_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 10;
    }
    
}
