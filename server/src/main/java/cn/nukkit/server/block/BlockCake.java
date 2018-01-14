package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemCake;
import cn.nukkit.server.item.food.Food;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockCake extends BlockTransparent {

    public BlockCake(int meta) {
        super(meta);
    }

    public BlockCake() {
        this(0);
    }

    @Override
    public String getName() {
        return "Cake BlockType";
    }

    @Override
    public int getId() {
        return CAKE_BLOCK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                x + (1 + getDamage() * 2) / 16,
                y,
                z + 0.0625,
                x - 0.0625 + 1,
                y + 0.5,
                z - 0.0625 + 1
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (down().getId() != Block.AIR) {
            getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == Block.AIR) {
                getLevel().setBlock(this, new BlockAir(), true);

                return NukkitLevel.BLOCK_UPDATE_NORMAL;
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
        return new ItemCake();
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null && player.getFoodData().getLevel() < player.getFoodData().getMaxLevel()) {
            if (meta <= 0x06) meta++;
            if (meta >= 0x06) {
                getLevel().setBlock(this, new BlockAir(), true);
            } else {
                Food.getByRelative(this).eatenBy(player);
                getLevel().setBlock(this, this, true);
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
        return (7 - this.meta) * 2;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }
}
