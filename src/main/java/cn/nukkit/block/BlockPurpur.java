package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

public class BlockPurpur extends BlockSolid {

    public static final int PURPUR_NORMAL = 0;
    public static final int PURPUR_PILLAR = 2;

    public BlockPurpur() {
        this(0);
    }

    public BlockPurpur(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Purpur Block",
                "",
                "Purpur Pillar",
                ""
        };

        return names[this.meta & 0x03];
    }

    @Override
    public int getId() {
        return PURPUR_BLOCK;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.meta != PURPUR_NORMAL) {
            short[] faces = new short[]{
                    0,
                    0,
                    0b1000,
                    0b1000,
                    0b0100,
                    0b0100
            };

            this.meta = ((this.meta & 0x03) | faces[face.getIndex()]);
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.PURPUR_BLOCK, this.meta & 0x03, 1}
            };
        } else {
            return new int[0][0];
        }
    }
}
