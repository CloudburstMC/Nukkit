package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;

public abstract class BlockWoodStripped extends BlockWood {

    private static final short[] FACES = {
            0,
            0,
            0b10,
            0b10,
            0b01,
            0b01
    };

    public BlockWoodStripped() {
        this(0);
    }

    public BlockWoodStripped(int meta) {
        super(meta);
    }

    @Override
    public abstract int getId();

    @Override
    public abstract String getName();

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(FACES[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
