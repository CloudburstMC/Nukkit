package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    private static final String[] NAMES = {
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

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public Item toItem() {
        if (this.getDamage() > 11) {
            int variant = this.getDamage() & 0x07;
            return new ItemBlock(Block.get(WOOD_BARK, variant), variant);
        }
        return new ItemBlock(this, this.getDamage() & 0x03);
    }

    @Override
    protected int getStrippedId() {
        int damage = getDamage();
        if ((damage & 0b1100) == 0b1100) { // Only bark
            return WOOD_BARK;
        }

        int typeId = damage & 0x3;
        if (typeId == 0) {
            return STRIPPED_ACACIA_LOG;
        } else {
            return STRIPPED_DARK_OAK_LOG;
        }
    }

    @Override
    protected int getStrippedDamage() {
        int damage = getDamage();
        if ((damage & 0b1100) == 0b1100) { // Only bark
            int typeId = damage & 0x3;
            if (typeId == 0) {
                return 0x4 | 0x8;
            } else {
                return 0x5 | 0x8;
            }
        }

        return super.getStrippedDamage();
    }
}
