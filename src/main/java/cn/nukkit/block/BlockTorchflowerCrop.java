package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockTorchflowerCrop extends BlockFlowable {

    public BlockTorchflowerCrop() {
        this(0);
    }

    public BlockTorchflowerCrop(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TORCHFLOWER_CROP;
    }

    @Override
    public String getName() {
        return "Torchflower Crop";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        int id = down.getId();
        if (id == Block.GRASS || id == Block.DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS) {
            this.getLevel().setBlock(this, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == Item.AIR) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }
}
