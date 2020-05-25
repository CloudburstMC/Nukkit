package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.DYE;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockTallGrass extends FloodableBlock {

    public BlockTallGrass(Identifier id) {
        super(id);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        // Prevents from placing the same plant block on itself
        if (item.getId() == target.getId() && item.getMeta() == block.getMeta()) {
            return false;
        }
        Block down = this.down();
        if (down.getId() == GRASS || down.getId() == DIRT || down.getId() == PODZOL) {
            this.getLevel().setBlock(block.getPosition(), this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this.getPosition());
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getMeta() == 0x0f) {
            Block up = this.up();

            if (up.getId() == AIR) {
                int meta;

                switch (this.getMeta()) {
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
                    if (player != null && player.getGamemode().isSurvival()) {
                        item.decrementCount();
                    }

                    this.level.addParticle(new BoneMealParticle(this.getPosition()));
                    this.level.setBlock(this.getPosition(), get(DOUBLE_PLANT, meta), true, false);
                    this.level.setBlock(up.getPosition(), get(DOUBLE_PLANT, meta ^ BlockDoublePlant.TOP_HALF_BITMASK), true);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        boolean dropSeeds = ThreadLocalRandom.current().nextDouble(100) > 87.5;
        if (item.isShears()) {
            //todo enchantment
            return new Item[]{
                        Item.get(BlockIds.TALL_GRASS, this.getMeta(), 1)
                };
        }

        if (dropSeeds) {
            return new Item[]{
                    Item.get(ItemIds.WHEAT_SEEDS)
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
