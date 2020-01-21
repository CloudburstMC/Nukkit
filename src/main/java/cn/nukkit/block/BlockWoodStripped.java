package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;

public abstract class BlockWoodStripped extends BlockWood {
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
        short[] faces = new short[]{
                0,
                0,
                0b10,
                0b10,
                0b01,
                0b01
        };
    
        this.setDamage(((this.getDamage() & 0x03) | faces[face.getIndex()]));
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }
    
    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }
    
    @Override
    public boolean canBeActivated() {
        return false;
    }
    
    @Override
    public boolean onActivate(Item item, Player player) {
        return false;
    }
}
