package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStemWarped extends BlockStem {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStemWarped() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStemWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STEM;
    }

    @Override
    public String getName() {
        return "Warped Stem";
    }

    @Override
    protected BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_WARPED_STEM);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }
}
