package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLeaves extends BlockTransparentMeta {
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final ArrayBlockProperty<WoodType> OLD_LEAF_TYPE = new ArrayBlockProperty<>("old_leaf_type", true, new WoodType[]{
            WoodType.OAK, WoodType.SPRUCE, WoodType.BIRCH, WoodType.JUNGLE
    });
    
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty PERSISTENT = new BooleanBlockProperty("persistent_bit", false);
    
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty UPDATE = new BooleanBlockProperty("update_bit", false);

    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final BlockProperties OLD_LEAF_PROPERTIES = new BlockProperties(OLD_LEAF_TYPE, PERSISTENT, UPDATE);
    
    private static final BlockFace[] VISIT_ORDER = new BlockFace[]{
            BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP
    };
    
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
    public static final int OAK = 0;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
    public static final int SPRUCE = 1;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
    public static final int BIRCH = 2;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
    public static final int JUNGLE = 3;

    public BlockLeaves() {
        this(0);
    }

    public BlockLeaves(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LEAVES;
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return OLD_LEAF_PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }
    
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public WoodType getType() {
        return getPropertyValue(OLD_LEAF_TYPE);
    }

    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public void setType(WoodType type) {
        setPropertyValue(OLD_LEAF_TYPE, type);
    }

    @Override
    public String getName() {
        return getType().getEnglishName() + " Leaves";
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPersistent(true);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            if (this.canDropApple() && ThreadLocalRandom.current().nextInt(200) == 0) {
                return new Item[]{
                        Item.get(ItemID.APPLE)
                };
            }
            if (ThreadLocalRandom.current().nextInt(20) == 0) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return new Item[]{
                            Item.get(ItemID.STICK, 0, ThreadLocalRandom.current().nextInt(1, 2))
                    };
                } else if (getType() != WoodType.JUNGLE || ThreadLocalRandom.current().nextInt(20) == 0) {
                    return new Item[]{
                            this.getSapling()
                    };
                }
            }
        }
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (isCheckDecay()) {
                if (isPersistent() || findLog(this, 7, null)) {
                    setCheckDecay(false);
                    getLevel().setBlock(this, this, false, false);
                } else {
                    LeavesDecayEvent ev = new LeavesDecayEvent(this);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        getLevel().useBreakOn(this);
                    }
                }
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isCheckDecay()) {
                setCheckDecay(true);
                getLevel().setBlock(this, this, false, false);
            }
            
            // Slowly propagates the need to update instead of peaking down the TPS for huge trees
            for (BlockFace side : BlockFace.values()) {
                Block other = getSide(side);
                if (other instanceof BlockLeaves) {
                    BlockLeaves otherLeave = (BlockLeaves) other;
                    if (!otherLeave.isCheckDecay()) {
                        getLevel().scheduleUpdate(otherLeave, 2);
                    }
                }
            }
            return type;
        }
        return type;
    }

    private Boolean findLog(Block current, int distance, Long2LongMap visited) {
        if (visited == null) {
            visited = new Long2LongOpenHashMap();
            visited.defaultReturnValue(-1);
        }
        if (current instanceof BlockWood) {
            return true;
        }
        if (distance == 0 || !(current instanceof BlockLeaves)) {
            return false;
        }
        long hash = Hash.hashBlock(current);
        if (visited.get(hash) >= distance) {
            return false;
        }
        visited.put(hash, distance);
        for (BlockFace face : VISIT_ORDER) {
            if(findLog(current.getSide(face), distance - 1, visited)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCheckDecay() {
        return getBooleanValue(UPDATE);
    }

    public void setCheckDecay(boolean checkDecay) {
        setBooleanValue(UPDATE, checkDecay);
    }

    public boolean isPersistent() {
        return getBooleanValue(PERSISTENT);
    }

    public void setPersistent(boolean persistent) {
        setBooleanValue(PERSISTENT, persistent);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected boolean canDropApple() {
        return getType() == WoodType.OAK;
    }

    protected Item getSapling() {
        return Item.get(BlockID.SAPLING, getIntValue(OLD_LEAF_TYPE));
    }

    @Override
    public boolean diffusesSkyLight() {
        return true;
    }


    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
