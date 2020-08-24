package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockFireSoul extends BlockFire {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFireSoul(){
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFireSoul(int meta){
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_FIRE;
    }

    @Override
    public String getName() {
        return "Soul Fire Block";
    }

    private Block blockDown() {
        return this.down();
    }

    private int blockDownId() {
        return blockDown().getId();
    }

    @Override
    public int onUpdate(int type) {
        if ((blockDownId() == Block.SOUL_SAND ||  blockDownId() == Block.SOUL_SOIL) && this.getId() == BlockID.FIRE) {
            this.getLevel().setBlock(this, getCurrentState().withBlockId(BlockID.FIRE).getBlock());
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }
}
