package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsWheat;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockTallGrass extends BlockFlowable {

    public BlockTallGrass() {
        this(1);
    }

    public BlockTallGrass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TALL_GRASS;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Dead Shrub",
                "Tall Grass",
                "Fern",
                ""
        };
        return names[this.getDamage() & 0x03];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId() == Block.GRASS || down.getId() == Block.DIRT || down.getId() == Block.PODZOL) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        //todo bonemeal

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        boolean dropSeeds = new Random().nextInt(10) == 0;
        if (item.isShears()) {
            //todo enchantment
            if (dropSeeds) {
                return new Item[]{
                        new ItemSeedsWheat(),
                        Item.get(Item.TALL_GRASS, this.getDamage(), 1)
                };
            } else {
                return new Item[]{
                        Item.get(Item.TALL_GRASS, this.getDamage(), 1)
                };
            }
        }

        if (dropSeeds) {
            return new Item[]{
                    new ItemSeedsWheat()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
