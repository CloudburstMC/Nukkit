package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;

public class BlockCherryLeaves extends BlockLeaves {

    public BlockCherryLeaves() {
        this(0);
    }

    public BlockCherryLeaves(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_LEAVES;
    }

    @Override
    public String getName() {
        return "Cherry Leaves";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPersistent(true);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    protected boolean canDropApple() {
        return false;
    }

    @Override
    protected Item getSapling() {
        return new ItemBlock(Block.get(Block.CHERRY_SAPLING, 0));
    }

    @Override
    public boolean isPersistent() {
        return (this.getDamage() & 2) >>> 1 == 1;
    }

    @Override
    public void setPersistent(boolean persistent) {
        int value = (persistent ? 1 : 0) << 1;
        this.setDamage(this.getDamage() & ~1 | (value & 2));
    }

    @Override
    public boolean isCheckDecay() {
        return (this.getDamage() & 1) == 1;
    }

    @Override
    public void setCheckDecay(boolean updateBit) {
        this.setDamage(this.getDamage() & ~1 | ((updateBit ? 1 : 0) & 1));
    }
}
