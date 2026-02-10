package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockTorchflower extends BlockFlowable {

    public BlockTorchflower() {
        this(0);
    }

    public BlockTorchflower(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TORCHFLOWER;
    }

    @Override
    public String getName() {
        return "Torchflower";
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
