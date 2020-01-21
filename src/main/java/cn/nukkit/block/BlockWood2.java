package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    private static final String[] NAMES = new String[]{
            "Acacia Wood",
            "Dark Oak Wood",
            ""
    };

    public BlockWood2() {
        this(0);
    }

    public BlockWood2(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD2;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 2 ? 0 : this.getDamage()];
    }
    
    @Override
    protected int getStrippedId() {
        int typeId = getDamage() & 0x3;
        if (typeId == 0) {
            return STRIPPED_ACACIA_LOG;
        } else {
            return STRIPPED_DARK_OAK_LOG;
        }
    }
    
    @Override
    public Item toItem() {
        if ((getDamage() & 0b1100) == 0b1100) {
            return new ItemBlock(new BlockWoodBark(), (this.getDamage() & 0x3) + 4);
        } else {
            return new ItemBlock(this, this.getDamage() & 0x03);
        }
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x07) {
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
            default:
                return BlockColor.WOOD_BLOCK_COLOR;
        }
    }
}
