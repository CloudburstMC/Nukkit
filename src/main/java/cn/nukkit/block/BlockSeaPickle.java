package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
public class BlockSeaPickle extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BooleanBlockProperty DEAD = new BooleanBlockProperty("dead_bit", false);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final IntBlockProperty CLUSTER_COUNT = new IntBlockProperty("cluster_count", false, 3);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(CLUSTER_COUNT, DEAD);

    @PowerNukkitOnly
    public BlockSeaPickle() {
        this(0);
    }

    @PowerNukkitOnly
    protected BlockSeaPickle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SEA_PICKLE;
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
        return "Sea Pickle";
    }

    @PowerNukkitOnly
    public boolean isDead() {
        return (getDamage() & 0x4) == 0x4;
    }

    @PowerNukkitOnly
    public void setDead(boolean dead) {
        if (dead) {
            setDamage(getDamage() | 0x4);
        } else {
            setDamage(getDamage() ^ 0x4);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!down.isSolid() || down.getId() == ICE) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            Block layer1 = getLevelBlockAtLayer(1);
            if (layer1 instanceof BlockWater || layer1.getId() == ICE_FROSTED) {
                if (isDead() && (layer1.getId() == ICE_FROSTED || layer1.getDamage() == 0 || layer1.getDamage() == 8)) {
                    BlockFadeEvent event = new BlockFadeEvent(this, new BlockSeaPickle(getDamage() ^ 0x4));
                    if (!event.isCancelled()) {
                        this.getLevel().setBlock(this, event.getNewState(), true, true);
                    }
                    return type;
                }
            } else if (!isDead()) {
                BlockFadeEvent event = new BlockFadeEvent(this, new BlockSeaPickle(getDamage() ^ 0x4));
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }

            return type;
        }

        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId() == SEA_PICKLE && (target.getDamage() & 0b11) < 3) {
            target.setDamage(target.getDamage() + 1);
            this.getLevel().setBlock(target, target, true, true);
            return true;
        }

        Block down = block.down().getLevelBlockAtLayer(0);
        if (down.isSolid() && down.getId() != ICE) {
            if (down instanceof BlockSlab || down instanceof BlockStairs || block.getId() == BUBBLE_COLUMN) {
                return false;
            }
            Block layer1 = block.getLevelBlockAtLayer(1);
            if (layer1 instanceof BlockWater) {
                if (layer1.getDamage() != 0 && layer1.getDamage() != 8) {
                    return false;
                }

                if (layer1.getDamage() == 8) {
                    this.getLevel().setBlock(block, 1, new BlockWater(), true, false);
                }
            } else {
                setDead(true);
            }

            this.getLevel().setBlock(block, 0, this, true, true);

            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {

        //Bone meal
        if (item.isFertilizer() && down().getId() == CORAL_BLOCK && !isDead()) {
            BlockSeaPickle block = (BlockSeaPickle) clone();
            block.setDamage(3);

            BlockGrowEvent blockGrowEvent = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(blockGrowEvent);

            if (blockGrowEvent.isCancelled()) {
                return false;
            }

            this.getLevel().setBlock(this, blockGrowEvent.getNewState(), false, true);
            this.level.addParticle(new BoneMealParticle(this));

            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();
            Block[] blocksAround = this.getLevel().getCollisionBlocks(new SimpleAxisAlignedBB(x - 2, y - 2, z - 2, x + 3, y, z + 3));
            for (Block blockNearby : blocksAround) {
                if (blockNearby.getId() == CORAL_BLOCK) {
                    Block up = blockNearby.up();
                    if (up instanceof BlockWater && (up.getDamage() == 0 || up.getDamage() == 8) && random.nextInt(6) == 0 && new Vector2(up.x, up.z).distance(new Vector2(this.x, this.z)) <= 2) {
                        BlockSpreadEvent blockSpreadEvent = new BlockSpreadEvent(up, this, new BlockSeaPickle(random.nextInt(3)));
                        if (!blockSpreadEvent.isCancelled()) {
                            this.getLevel().setBlock(up, 1, new BlockWater(), true, false);
                            this.getLevel().setBlock(up, blockSpreadEvent.getNewState(), true, true);
                        }
                    }
                }
            }
        }

        return super.onActivate(item, player);
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        if (isDead()) {
            return 0;
        } else {
            return (getDamage() + 2) * 3;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSeaPickle());
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{ new ItemBlock(new BlockSeaPickle(), 0, (getDamage() & 0x3) + 1) };
    }
}
