package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
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
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoublePlant extends FloodableBlock {
    public static final int SUNFLOWER = 0;
    public static final int LILAC = 1;
    public static final int TALL_GRASS = 2;
    public static final int LARGE_FERN = 3;
    public static final int ROSE_BUSH = 4;
    public static final int PEONY = 5;
    public static final int TOP_HALF_BITMASK = 0x8;

    public BlockDoublePlant(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeReplaced() {
        return this.getMeta() == TALL_GRASS || this.getMeta() == LARGE_FERN;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((this.getMeta() & TOP_HALF_BITMASK) == TOP_HALF_BITMASK) {
                // Top
                if (!(this.down().getId() == DOUBLE_PLANT)) {
                    this.getLevel().setBlock(this.getPosition(), Block.get(AIR), false, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                // Bottom
                if (this.down().isTransparent() || !(this.up().getId() == DOUBLE_PLANT)) {
                    this.getLevel().useBreakOn(this.getPosition());
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Block down = down();
        Block up = up();

        if (up.getId() == AIR && (down.getId() == GRASS || down.getId() == DIRT)) {
            this.getLevel().setBlock(block.getPosition(), this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up.getPosition(), Block.get(DOUBLE_PLANT, getMeta() ^ TOP_HALF_BITMASK), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if ((this.getMeta() & TOP_HALF_BITMASK) == TOP_HALF_BITMASK) { // Top half
            this.getLevel().useBreakOn(down.getPosition());
        } else {
            this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if ((this.getMeta() & TOP_HALF_BITMASK) != TOP_HALF_BITMASK) {
            boolean dropSeeds = ThreadLocalRandom.current().nextDouble(100) > 87.5;
            switch (this.getMeta() & 0x07) {
                case TALL_GRASS:
                case LARGE_FERN:
                    if (item.isShears()) {
                        //todo enchantment
                        return new Item[] {
                                Item.get(BlockIds.TALL_GRASS, (this.getMeta() & 0x07) == TALL_GRASS ? 1 : 2, 2)
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

            return new Item[]{toItem()};
        }

        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getMeta() == 0x0f) { //Bone meal
            switch (this.getMeta() & 0x07) {
                case SUNFLOWER:
                case LILAC:
                case ROSE_BUSH:
                case PEONY:
                    if (player != null && player.getGamemode().isSurvival()) {
                        item.decrementCount();
                    }
                    this.level.addParticle(new BoneMealParticle(this.getPosition()));
                    this.level.dropItem(this.getPosition(), this.toItem());
            }

            return true;
        }

        return false;
    }
}