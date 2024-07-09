package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockMoss extends BlockDirt {

    public BlockMoss() {
    }

    public BlockMoss(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Moss Block";
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() != Item.DYE || item.getDamage() != ItemDye.BONE_MEAL || this.up().getId() != AIR) {
            return false;
        }

        int random = ThreadLocalRandom.current().nextInt(13);
        Block block;
        Block block2 = null;
        if (random < 5) {
            block = Block.get(TALL_GRASS);
        } else if (random < 8) {
            block = Block.get(MOSS_CARPET);
        } else if (random < 9) {
            if (this.up(2).getId() != AIR) {
                return false;
            }

            block = Block.get(DOUBLE_PLANT, BlockDoublePlant.TALL_GRASS);
            block2 = Block.get(DOUBLE_PLANT, BlockDoublePlant.TALL_GRASS ^ BlockDoublePlant.TOP_HALF_BITMASK);
        } else if (random < 11) {
            block = Block.get(AZALEA);
        } else {
            block = Block.get(FLOWERING_AZALEA);
        }

        this.getLevel().setBlock(this.up(), block, false, true);
        if (block2 != null) {
            this.getLevel().setBlock(this.up(2), block2, false, true);
        }

        this.level.addParticle(new BoneMealParticle(this));

        if (player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getFullId() {
        return this.getId() << Block.DATA_BITS;
    }

    @Override
    public void setDamage(int meta) {
        // Noop
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.canHarvestWithHand() || this.canHarvest(item)) {
            return new Item[]{this.toItem()};
        }
        return new Item[0];
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }
}
