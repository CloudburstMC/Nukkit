package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * @author Nukkit Project Team
 */
public class BlockCake extends BlockTransparent {

    public BlockCake(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public float getMinX() {
        return this.getX() + (1 + getMeta() * 2) / 16f;
    }

    @Override
    public float getMinY() {
        return this.getY();
    }

    @Override
    public float getMinZ() {
        return this.getZ() + 0.0625f;
    }

    @Override
    public float getMaxX() {
        return this.getX() - 0.0625f + 1;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.5f;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() - 0.0625f + 1;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (down().getId() != AIR) {
            getLevel().setBlock(block.getPosition(), this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == AIR) {
                getLevel().setBlock(this.getPosition(), Block.get(AIR), true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.CAKE);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null && player.getFoodData().getLevel() < player.getFoodData().getMaxLevel()) {
            if (getMeta() <= 0x06) setDamage(getMeta() + 1);
            if (getMeta() >= 0x06) {
                getLevel().setBlock(this.getPosition(), Block.get(AIR), true);
            } else {
                Food.getByRelative(this).eatenBy(player);
                getLevel().setBlock(this.getPosition(), this, true);
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    public int getComparatorInputOverride() {
        return (7 - this.getMeta()) * 2;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }
}
