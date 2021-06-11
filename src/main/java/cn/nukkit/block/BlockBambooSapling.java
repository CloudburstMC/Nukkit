package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
public class BlockBambooSapling extends BlockFlowable {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = BlockSapling.PROPERTIES;

    @PowerNukkitOnly
    public BlockBambooSapling() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockBambooSapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_SAPLING;
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
        return "Bamboo Sapling";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                level.useBreakOn(this, null, null, true);
            } else {
                Block up = up();
                if (up.getId() == BAMBOO) {
                    BlockBamboo upperBamboo = (BlockBamboo) up;
                    BlockBamboo newState = new BlockBamboo();
                    newState.setThick(upperBamboo.isThick());
                    level.setBlock(this, newState, true, true);
                }
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up = up();
            if (getAge() == 0 && up.getId() == AIR && level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL && ThreadLocalRandom.current().nextInt(3) == 0) {
                BlockBamboo newState = new BlockBamboo();
                newState.setLeafSize(BlockBamboo.LEAF_SIZE_SMALL);
                BlockGrowEvent blockGrowEvent = new BlockGrowEvent(up, newState);
                level.getServer().getPluginManager().callEvent(blockGrowEvent);
                if (!blockGrowEvent.isCancelled()) {
                    Block newState1 = blockGrowEvent.getNewState();
                    newState1.y = up.y;
                    newState1.x = x;
                    newState1.z = z;
                    newState1.level = level;
                    newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportInvalid()) {
            return false;
        }

        if(this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }


    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        boolean isBoneMeal = item.isFertilizer(); //Bonemeal
        if (isBoneMeal || item.getBlock() != null && item.getBlockId() == BlockID.BAMBOO) {

            boolean success = false;
            Block block = this.up();
            if (block.getId() == AIR) {
                success = grow(block);
            }

            if (success) {
                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }

                if (isBoneMeal) {
                    level.addParticle(new BoneMealParticle(this));
                } else {
                    level.addSound(block, Sound.BLOCK_BAMBOO_PLACE, 0.8F, 1.0F);
                }
            }

            return true;
        }
        return false;
    }

    @PowerNukkitOnly
    public boolean grow(Block up) {
        BlockBamboo bamboo = new BlockBamboo();
        bamboo.x = x;
        bamboo.y = y;
        bamboo.z = z;
        bamboo.level = level;
        return bamboo.grow(up);
    }

    private boolean isSupportInvalid() {
        int downId = down().getId();
        return downId != DIRT && downId != GRASS && downId != SAND && downId != GRAVEL && downId != PODZOL;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @PowerNukkitOnly
    public int getAge() {
        return getDamage() & 0x1;
    }

    @PowerNukkitOnly
    public void setAge(int age) {
        age = MathHelper.clamp(age, 0, 1) & 0x1;
        setDamage(getDamage() & (DATA_MASK ^ 0x1) | age);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBamboo());
    }

    @Override
    public double getMinX() {
        return x + 0.125;
    }

    @Override
    public double getMaxX() {
        return x + 0.875;
    }

    @Override
    public double getMinZ() {
        return z + 0.125;
    }

    @Override
    public double getMaxZ() {
        return z + 0.875;
    }

    @Override
    public double getMaxY() {
        return y + 0.875;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
