package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockHyphaeStrippedCrimson extends BlockStemStripped {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeStrippedCrimson() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeStrippedCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CRIMSON_HYPHAE;
    }
    
    @Override
    public String getName() {
        return "Crimson Stripped Hyphae";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR;
    }
}
