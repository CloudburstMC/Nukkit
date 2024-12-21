package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47
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

    private static final String[] NAMES = {
            "Grass",
            "Grass",
            "Fern",
            "Fern"
    };

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
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
        int id = down.getId();
        if (id == Block.GRASS || id == Block.DIRT || id == Block.PODZOL || id == FARMLAND || id == MYCELIUM || id == MOSS_BLOCK) {
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
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            Block up = this.up();

            if (up.getId() == AIR) {
                int meta;

                switch (this.getDamage()) {
                    case 0:
                    case 1:
                        meta = BlockDoublePlant.TALL_GRASS;
                        break;
                    case 2:
                    case 3:
                        meta = BlockDoublePlant.LARGE_FERN;
                        break;
                    default:
                        meta = -1;
                }

                if (meta != -1) {
                    if (player != null && !player.isCreative()) {
                        item.count--;
                    }

                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.setBlock(this, get(DOUBLE_PLANT, meta), true, false);
                    this.level.setBlock(up, get(DOUBLE_PLANT, meta ^ BlockDoublePlant.TOP_HALF_BITMASK), true);
                }
            }

            return true;
        }

        return false;
    }


    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    Item.get(Item.TALL_GRASS, this.getDamage() & 2, 1)
            };
        }

        if (ThreadLocalRandom.current().nextInt(10) == 0) {
            return new Item[]{
                    Item.get(Item.WHEAT_SEEDS)
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

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
