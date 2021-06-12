package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.BlockColor;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockHyphaeCrimson extends BlockStem {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeCrimson() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeCrimson(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CRIMSON_HYPHAE;
    }
    
    @Override
    public String getName() {
        return "Crimson Hyphae";
    }

    @Override
    protected BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_CRIMSON_HYPHAE);
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR;
    }
}
