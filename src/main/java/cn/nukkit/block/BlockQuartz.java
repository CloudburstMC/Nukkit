package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.QUARTZ_BLOCK;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockQuartz extends BlockSolid {

    public static final int QUARTZ_NORMAL = 0;
    public static final int QUARTZ_CHISELED = 1;
    public static final int QUARTZ_PILLAR = 2;
    public static final int QUARTZ_PILLAR2 = 3;


    public BlockQuartz(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getDamage() != QUARTZ_NORMAL) {
            short[] faces = new short[]{
                    0,
                    0,
                    0b1000,
                    0b1000,
                    0b0100,
                    0b0100
            };

            this.setDamage(((this.getDamage() & 0x03) | faces[face.getIndex()]));
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return Item.get(QUARTZ_BLOCK, this.getDamage() & 0x03, 1);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
