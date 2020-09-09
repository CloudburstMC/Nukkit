package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the main logic of all nether vines.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockVinesNether extends BlockTransparentMeta {
    /**
     * Creates a nether vine with age {@code 0}.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVinesNether() {
    }

    /**
     * Creates a nether vine from a meta compatible with {@link #getProperties()}.
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockVinesNether(int meta) {
        super(meta);
    }

    /**
     * The direction that the vine will grow, vertical direction is expected but future implementations
     * may also add horizontal directions.
     * @return Normally, up or down.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public abstract BlockFace getGrowthDirection();

    /**
     * The current age of this block.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getVineAge();

    /**
     * Changes the age of this block.
     * @param vineAge The new age
     * @throws InvalidBlockPropertyValueException If the value is outside the accepted range from {@code 0} to {@link #getMaxVineAge()}, both inclusive.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract void setVineAge(int vineAge) throws InvalidBlockPropertyValueException;

    /**
     * The maximum accepted age of this block.
     * @return Positive, inclusive value.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getMaxVineAge();

    /**
     * Changes the current vine age to a random new random age. 
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void randomizeVineAge() {
        double chance = 1.0D;
        int age;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(age = 0; random.nextDouble() < chance; ++age) {
            chance *= 0.826D;
        }
        
        setVineAge(age);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isSupportValid()) {
            return false;
        }
        randomizeVineAge();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        switch (type) {
            case Level.BLOCK_UPDATE_RANDOM:
                if (getVineAge() < getMaxVineAge() && ThreadLocalRandom.current().nextInt(10) == 0) {
                    grow();
                }
                return Level.BLOCK_UPDATE_RANDOM;
            case Level.BLOCK_UPDATE_SCHEDULED:
                getLevel().useBreakOn(this, null, null, true);
                return Level.BLOCK_UPDATE_SCHEDULED;
            case Level.BLOCK_UPDATE_NORMAL:
                if (!isSupportValid()) {
                    getLevel().scheduleUpdate(this, 1);
                }
                return Level.BLOCK_UPDATE_NORMAL;
            default:
                return 0;
        }
    }

    /**
     * Grow a single vine if possible. Calls {@link BlockGrowEvent} passing the positioned new state and the source block.
     * @return If the vine grew successfully.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean grow() {
        Block pos = getSide(getGrowthDirection());
        if (pos.getId() != AIR || pos.y < 0 || 255 < pos.y) {
            return false;
        }

        BlockVinesNether growing = clone();
        growing.x = pos.x;
        growing.y = pos.y;
        growing.z = pos.z;
        growing.setVineAge(Math.min(getVineAge() + 1, getMaxVineAge()));

        BlockGrowEvent ev = new BlockGrowEvent(this, growing);
        Server.getInstance().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }
        
        return level.setBlock(pos, growing);
    }

    /**
     * Grow a random amount of vines. 
     * Calls {@link BlockGrowEvent} passing the positioned new state and the source block for each new vine being added
     * to the world, if one of the events gets cancelled the growth gets interrupted.
     * @return How many vines grew 
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int growMultiple() {
        BlockFace growthDirection = getGrowthDirection();
        int age = getVineAge() + 1;
        int maxAge = getMaxVineAge();
        BlockVinesNether growing = clone();
        growing.randomizeVineAge();
        int blocksToGrow = growing.getVineAge();

        int grew = 0;
        for (int distance = 1; distance <= blocksToGrow; distance++) {
            Block pos = getSide(growthDirection, distance);
            if (pos.getId() != AIR || pos.y < 0 || 255 < pos.y) {
                break;
            }
            
            growing.setVineAge(Math.min(age++, maxAge));
            growing.x = pos.x;
            growing.y = pos.y;
            growing.z = pos.z;

            BlockGrowEvent ev = new BlockGrowEvent(this, growing.clone());
            Server.getInstance().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                break;
            }
            
            if (!level.setBlock(pos, ev.getNewState())) {
                break;
            }
            
            grew++;
        }
        
        return grew;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (item.getId() != ItemID.DYE || item.getDamage() != DyeColor.WHITE.getDyeData()) {
            return false;
        }

        getLevel().addParticle(new BoneMealParticle(this));
        growMultiple();
        
        if (player != null && !player.isCreative()) {
            item.count--;
        }
        
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        // They have a 33% (3/9) chance to drop a single weeping vine when broken, 
        // increased to 55% (5/9) with Fortune I, 
        // 77% (7/9) with Fortune II, 
        // and 100% with Fortune III. 
        // 
        // They always drop a single weeping vine when broken with shears or a tool enchanted with Silk Touch.

        int enchantmentLevel;
        if (item.isShears() || (enchantmentLevel = item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING)) >= 3) {
            return new Item[]{ toItem() };
        }
        
        int chance = 3 + enchantmentLevel * 2;
        if (ThreadLocalRandom.current().nextInt(9) < chance) {
            return new Item[]{ toItem() };
        }
        
        return new Item[0];
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean isSupportValid(@Nonnull Block support) {
        return !support.isTransparent();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSupportValid() {
        return isSupportValid(getSide(getGrowthDirection().getOpposite()));
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMinX() {
        return x+ (4/16.0);
    }

    @Override
    public double getMinZ() {
        return z+ (4/16.0);
    }

    @Override
    public double getMaxX() {
        return x+ (12/16.0);
    }

    @Override
    public double getMaxZ() {
        return z+ (12/16.0);
    }

    @Override
    public double getMaxY() {
        return y+ (15/16.0);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
    @Override
    public BlockVinesNether clone() {
        return (BlockVinesNether) super.clone();
    }
}
