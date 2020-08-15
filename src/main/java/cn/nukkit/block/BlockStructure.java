package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
public class BlockStructure extends BlockSolidMeta {

    public static final int INVENTORY_MODEL = 0;
    public static final int DATA = 1;
    public static final int SAVE = 2;
    public static final int LOAD = 3;
    public static final int CORNER = 4;
    public static final int EXPORT = 5;
    
    public BlockStructure() {
        this(0);
    }
    
    public BlockStructure(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRUCTURE_BLOCK;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                // TODO: Add UI
            }
        }
        return true;
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        this.setDamage(SAVE);
        this.getLevel().setBlock(block, this, true);
        // TODO: Add Block Entity
        return true;
    }
    
    @Override
    public String getName() {
        String[] names = new String[]{
            "Structure Block",
            "Data Structure Block",
            "Save Structure Block",
            "Load Structure Block",
            "Corner Structure Block",
            "Export Structure Block",
            ""
        };
        return names[this.getDamage() & 0x05];
    }
    
    @Override
    public double getHardness() {
        return -1;
    }
    
    @Override
    public double getResistance() {
        return 18000000;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean isBreakable(Item item) {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }
}
