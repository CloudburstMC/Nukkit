package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class BlockIceFrosted extends BlockTransparentMeta {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final IntBlockProperty AGE = new IntBlockProperty("age", false, 3);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(AGE);

    public BlockIceFrosted() {
        this(0);
    }

    public BlockIceFrosted(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ICE_FROSTED;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean success = super.place(item, block, target, face, fx, fy, fz, player);
        if (success) {
            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
        }
        return success;
    }

    @Override
    public boolean onBreak(Item item) {
        level.setBlock(this, get(WATER), true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) > 11 && (ThreadLocalRandom.current().nextInt(3) == 0 || countNeighbors() < 4)) {
                slightlyMelt(true);
            } else {
                level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (countNeighbors() < 2) {
                level.setBlock(this, layer, get(WATER), true);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public Item toItem() {
        return Item.get(AIR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    protected void slightlyMelt(boolean isSource) {
        int age = getDamage();
        if (age < 3) {
            setDamage(age + 1);
            level.setBlock(this, layer, this, true);
            level.scheduleUpdate(level.getBlock(this), ThreadLocalRandom.current().nextInt(20, 40));
        } else {
            level.setBlock(this, layer, get(WATER), true);
            if (isSource) {
                for (BlockFace face : BlockFace.values()) {
                    Block block = getSide(face);
                    if (block instanceof BlockIceFrosted) {
                        ((BlockIceFrosted) block).slightlyMelt(false);
                    }
                }
            }
        }
    }

    private int countNeighbors() {
        int neighbors = 0;
        for (BlockFace face : BlockFace.values()) {
            if (getSide(face).getId() == ICE_FROSTED && ++neighbors >= 4) {
                return neighbors;
            }
        }
        return neighbors;
    }
}
