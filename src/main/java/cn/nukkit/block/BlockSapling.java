package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.tree.*;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockSapling extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<WoodType> SAPLING_TYPE = new ArrayBlockProperty<>("sapling_type", true, WoodType.class);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty AGED = new BooleanBlockProperty("age_bit", false);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SAPLING_TYPE, AGED);
    
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.OAK", by = "PowerNukkit", 
            reason = "Use the new BlockProperty system instead")
    public static final int OAK = 0;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.SPRUCE", by = "PowerNukkit",
            reason = "Use the new BlockProperty system instead")
    public static final int SPRUCE = 1;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.BIRCH", by = "PowerNukkit",
            reason = "Use the new BlockProperty system instead")
    public static final int BIRCH = 2;
    
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN",
            by = "PowerNukkit", replaceWith = "ObjectTree.growTree(ChunkManager level, int x, int y, int z, NukkitRandom random, WoodType.BIRCH, true)",
            reason = "Shouldn't even be here")
    public static final int BIRCH_TALL = 8 | BIRCH;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.JUNGLE", by = "PowerNukkit",
            reason = "Use the new BlockProperty system instead")
    public static final int JUNGLE = 3;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.ACACIA", by = "PowerNukkit",
            reason = "Use the new BlockProperty system instead")
    public static final int ACACIA = 4;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.DARK_OAK", by = "PowerNukkit",
            reason = "Use the new BlockProperty system instead")
    public static final int DARK_OAK = 5;

    public BlockSapling() {
        this(0);
    }

    public BlockSapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SAPLING;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public WoodType getWoodType() {
        return getPropertyValue(SAPLING_TYPE);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setWoodType(WoodType woodType) {
        setPropertyValue(SAPLING_TYPE, woodType);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isAged() {
        return getBooleanValue(AGED);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setAged(boolean aged) {
        setBooleanValue(AGED, aged);
    }

    @Override
    public String getName() {
        return getWoodType().getEnglishName() + " Sapling";
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockFlower.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
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
        if (item.getId() == ItemID.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) { // BoneMeal
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on block update if the supporting block is invalid")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockFlower.isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) == 1 && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                if (isAged()) {
                    this.grow();
                } else {
                    setAged(true);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    private void grow() {
        BasicGenerator generator = null;
        boolean bigTree = false;

        int x = 0;
        int z = 0;

        switch (getWoodType()) {
            case JUNGLE:
                loop:
                for (; x >= -1; --x) {
                    for (; z >= -1; --z) {
                        if (this.findSaplings(x, z, WoodType.JUNGLE)) {
                            generator = new ObjectJungleBigTree(10, 20, Block.get(BlockID.WOOD, BlockWood.JUNGLE), Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE));
                            bigTree = true;
                            break loop;
                        }
                    }
                }

                if (!bigTree) {
                    x = 0;
                    z = 0;
                    generator = new NewJungleTree(4, 7);
                }
                break;
            case ACACIA:
                generator = new ObjectSavannaTree();
                break;
            case DARK_OAK:
                loop:
                for (; x >= -1; --x) {
                    for (; z >= -1; --z) {
                        if (this.findSaplings(x, z, WoodType.DARK_OAK)) {
                            generator = new ObjectDarkOakTree();
                            bigTree = true;
                            break loop;
                        }
                    }
                }

                if (!bigTree) {
                    return;
                }
                break;
            //TODO: big spruce
            default:
                ObjectTree.growTree(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), new NukkitRandom(), getWoodType(), false);
                return;
        }

        if (bigTree) {
            this.level.setBlock(this.add(x, 0, z), get(AIR), true, false);
            this.level.setBlock(this.add(x + 1., 0, z), get(AIR), true, false);
            this.level.setBlock(this.add(x, 0, z + 1.), get(AIR), true, false);
            this.level.setBlock(this.add(x + 1., 0, z + 1.), get(AIR), true, false);
        } else {
            this.level.setBlock(this, get(AIR), true, false);
        }

        if (!generator.generate(this.level, new NukkitRandom(), this.add(x, 0, z))) {
            if (bigTree) {
                this.level.setBlock(this.add(x, 0, z), this, true, false);
                this.level.setBlock(this.add(x + 1., 0, z), this, true, false);
                this.level.setBlock(this.add(x, 0, z + 1.), this, true, false);
                this.level.setBlock(this.add(x + 1., 0, z + 1.), this, true, false);
            } else {
                this.level.setBlock(this, this, true, false);
            }
        }
    }

    private boolean findSaplings(int x, int z, WoodType type) {
        return this.isSameType(this.add(x, 0, z), type) && 
                this.isSameType(this.add(x + 1., 0, z), type) && 
                this.isSameType(this.add(x, 0, z + 1.), type) && 
                this.isSameType(this.add(x + 1., 0, z + 1.), type);
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Checking magic value directly is depreacated",
        replaceWith = "isSameType(Vector3,WoodType)")
    public boolean isSameType(Vector3 pos, int type) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId() && (block.getDamage() & 0x07) == (type & 0x07);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSameType(Vector3 pos, WoodType type) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId() && ((BlockSapling)block).getWoodType() == type;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
