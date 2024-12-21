package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the main logic of all nether vines.
 * @author joserobjr
 */
public abstract class BlockVinesNether extends BlockTransparentMeta {

    public BlockVinesNether() {
        this(0);
    }

    public BlockVinesNether(int meta) {
        super(meta);
    }

    /**
     * The direction that the vine will grow, vertical direction is expected but future implementations
     * may also add horizontal directions.
     * @return Normally, up or down.
     */
    public abstract BlockFace getGrowthDirection();

    /**
     * The current age of this block.
     */
    public abstract int getVineAge();

    /**
     * Changes the age of this block.
     * @param vineAge The new age
     */
    public abstract void setVineAge(int vineAge);

    /**
     * The maximum accepted age of this block.
     * @return Positive, inclusive value.
     */
    public abstract int getMaxVineAge();

    /**
     * Changes the current vine age to a random new random age.
     *
     * @param pseudorandom If the the randomization should be pseudorandom.
     */
    public void randomizeVineAge(boolean pseudorandom) {
        if (pseudorandom) {
            setVineAge(ThreadLocalRandom.current().nextInt(getMaxVineAge()));
            return;
        }

        double chance = 1.0D;
        int age;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (age = 0; random.nextDouble() < chance; ++age) {
            chance *= 0.826D;
        }

        setVineAge(age);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        Block support = getSide(getGrowthDirection().getOpposite());
        if (!isSupportValid(support)) {
            return false;
        }

        if (support.getId() == getId()) {
            setVineAge(Math.min(getMaxVineAge(), ((BlockVinesNether) support).getVineAge() + 1));
        } else {
            randomizeVineAge(true);
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        switch (type) {
            case Level.BLOCK_UPDATE_RANDOM:
                int maxVineAge = getMaxVineAge();
                if (getVineAge() < maxVineAge && ThreadLocalRandom.current().nextInt(10) == 0
                        && findVineAge(true).orElse(maxVineAge) < maxVineAge) {
                    grow();
                }
                return Level.BLOCK_UPDATE_RANDOM;
            case Level.BLOCK_UPDATE_NORMAL:
                if (!this.isSupportValid()) {
                    this.getLevel().useBreakOn(this);
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
    public boolean grow() {
        Block pos = getSide(getGrowthDirection());
        if (pos.getId() != AIR || pos.y < level.getMinBlockY() || pos.y > level.getMaxBlockY()) {
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

        if (level.setBlock(pos, growing)) {
            increaseRootAge();
            return true;
        }
        return false;
    }

    /**
     * Grow a random amount of vines.
     * Calls {@link BlockGrowEvent} passing the positioned new state and the source block for each new vine being added
     * to the world, if one of the events gets cancelled the growth gets interrupted.
     * @return How many vines grew
     */
    public int growMultiple() {
        BlockFace growthDirection = getGrowthDirection();
        int age = getVineAge() + 1;
        int maxAge = getMaxVineAge();
        BlockVinesNether growing = clone();
        growing.randomizeVineAge(false);
        int blocksToGrow = growing.getVineAge();

        int grew = 0;
        for (int distance = 1; distance <= blocksToGrow; distance++) {
            Block pos = getSide(growthDirection, distance);
            if (pos.getId() != AIR || pos.y < level.getMinBlockY() || pos.y > level.getMaxBlockY()) {
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

        if (grew > 0) {
            increaseRootAge();
        }

        return grew;
    }

    /**
     * Attempt to get the age of the root or the head of the vine.
     * @param base True to get the age of the base (oldest block), false to get the age of the head (newest block)
     * @return Empty if the target could not be reached. The age of the target if it was found.
     */
    @Nonnull
    public OptionalInt findVineAge(boolean base) {
        return findVineBlock(base)
                .map(vine-> OptionalInt.of(vine.getVineAge()))
                .orElse(OptionalInt.empty());
    }

    /**
     * Attempt to find the root or the head of the vine transversing the growth direction for up to 256 blocks.
     * @param base True to find the base (oldest block), false to find the head (newest block)
     * @return Empty if the target could not be reached or the block there isn't an instance of {@link BlockVinesNether}.
     *          The positioned block of the target if it was found.
     */
    @Nonnull
    public Optional<BlockVinesNether> findVineBlock(boolean base) {
        return findVine(base)
                .map(Position::getLevelBlock)
                .filter(BlockVinesNether.class::isInstance)
                .map(BlockVinesNether.class::cast);
    }

    /**
     * Attempt to find the root or the head of the vine transversing the growth direction for up to 256 blocks.
     * @param base True to find the base (oldest block), false to find the head (newest block)
     * @return Empty if the target could not be reached. The position of the target if it was found.
     */
    @Nonnull
    public Optional<Position> findVine(boolean base) {
        BlockFace supportFace = getGrowthDirection();
        if (base) {
            supportFace = supportFace.getOpposite();
        }
        Position result = getLocation();
        int id = getId();
        int limit = 256;
        while (--limit > 0) {
            Position next = result.getSide(supportFace);
            if (next.getLevelBlock().getId() == id) {
                result = next;
            } else {
                break;
            }
        }

        return Optional.of(result);
    }

    public Optional<Boolean> increaseRootAge() {
        Block base = findVine(true).map(Position::getLevelBlock).orElse(null);
        if (!(base instanceof BlockVinesNether)) {
            return Optional.empty();
        }

        BlockVinesNether baseVine = (BlockVinesNether) base;
        int vineAge = baseVine.getVineAge();
        if (vineAge < baseVine.getMaxVineAge()) {
            baseVine.setVineAge(vineAge + 1);
            if (getLevel().setBlock(baseVine, baseVine)) {
                return Optional.of(true);
            }
        }

        return Optional.of(false);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (!(item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL)) {
            return false;
        }

        this.getLevel().addParticle(new BoneMealParticle(this));
        this.findVineBlock(false).ifPresent(BlockVinesNether::growMultiple);

        if (player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
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

    protected boolean isSupportValid(@Nonnull Block support) {
        return support.getId() == getId() || !support.isTransparent();
    }

    public boolean isSupportValid() {
        return isSupportValid(getSide(getGrowthDirection().getOpposite()));
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
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
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
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
