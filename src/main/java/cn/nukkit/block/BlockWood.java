package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockWood extends BlockSolidMeta {

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    private static final short[] FACES = {
            0,
            0,
            0b1000,
            0b1000,
            0b0100, // full bark
            0b0100
    };

    private static final int[] STRIPPED_IDS = {
            STRIPPED_OAK_LOG,
            STRIPPED_SPRUCE_LOG,
            STRIPPED_BIRCH_LOG,
            STRIPPED_JUNGLE_LOG
    };

    public BlockWood() {
        this(0);
    }

    public BlockWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    private static final String[] NAMES = {
            "Oak Wood",
            "Spruce Wood",
            "Birch Wood",
            "Jungle Wood"
    };

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 10;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(((this.getDamage() & 0x03) | FACES[face.getIndex()]));
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        if (this.getDamage() > 11) {
            int variant = this.getDamage() & 0x03;
            return new ItemBlock(Block.get(WOOD_BARK, variant), variant);
        }
        return new ItemBlock(this, this.getDamage() & 0x03);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x03) {
            default:
            case OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case SPRUCE:
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isAxe() && player != null && (player.isSurvival() || player.isCreative()) && !(this instanceof BlockWoodStripped) && (!(this instanceof BlockWoodBark) || this.getDamage() < 8)) {
            Block strippedBlock = Block.get(getStrippedId(), getStrippedDamage());
            item.useOn(this);
            this.level.setBlock(this, strippedBlock, true, true);
            return true;
        }
        return false;
    }

    protected int getStrippedId() {
        int damage = getDamage();
        if ((damage & 0b1100) == 0b1100) { // Only bark
            return WOOD_BARK;
        }

        return STRIPPED_IDS[damage & 0x03];
    }

    protected int getStrippedDamage() {
        int damage = getDamage();
        if ((damage & 0b1100) == 0b1100) { // Only bark
            return damage & 0x03 | 0x8;
        }

        return damage >> 2;
    }
}
