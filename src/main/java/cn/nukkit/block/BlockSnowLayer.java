package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.BlockColor;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author xtypr, joserobjr
 * @since 2015/12/6
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
public class BlockSnowLayer extends BlockFallableMeta {
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final IntBlockProperty SNOW_HEIGHT = new IntBlockProperty("height", true, 7);
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty COVERED = new BooleanBlockProperty("covered_bit", false);
    @PowerNukkitOnly @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SNOW_HEIGHT, COVERED);

    public BlockSnowLayer() {
        // Does nothing
    }

    public BlockSnowLayer(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Top Snow";
    }

    @Override
    public int getId() {
        return SNOW_LAYER;
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
    public int getSnowHeight() {
        return getIntValue(SNOW_HEIGHT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSnowHeight(int snowHeight) {
        setIntValue(SNOW_HEIGHT, snowHeight);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isCovered() {
        return getBooleanValue(COVERED);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setCovered(boolean covered) {
        setBooleanValue(COVERED, covered);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns the max Y based on the snow height")
    @Override
    public double getMaxY() {
        return y + (Math.min(16, getSnowHeight() + 1) * 2) / 16.0;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Renders a bounding box that the entities stands on top")
    @Override
    @Nullable
    protected AxisAlignedBB recalculateBoundingBox() {
        int snowHeight = getSnowHeight();
        if (snowHeight < 3) {
            return null;
        }
        if (snowHeight == 3 || snowHeight == SNOW_HEIGHT.getMaxValue()) {
            return this;
        }
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 8/16.0, z + 1);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Renders a bounding box with the actual snow_layer height")
    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "0.1 instead of 0.5")
    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns false if it has all the 8 layers")
    @Override
    public boolean canBeReplaced() {
        return getSnowHeight() < SNOW_HEIGHT.getMaxValue();
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will increase the layers and behave as expected in vanilla and will cover grass blocks")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Optional<BlockSnowLayer> increment = Stream.of(target, block)
                .filter(b -> b.getId() == SNOW_LAYER).map(BlockSnowLayer.class::cast)
                .filter(b -> b.getSnowHeight() < SNOW_HEIGHT.getMaxValue())
                .findFirst();
        
        if (increment.isPresent()) {
            BlockSnowLayer other = increment.get();
            if (Arrays.stream(level.getCollidingEntities(new SimpleAxisAlignedBB(
                    other.x, other.y, other.z, 
                    other.x + 1, other.y + 1, other.z + 1
                    ))).anyMatch(e-> e instanceof EntityLiving)) {
                return false;
            }
            other.setSnowHeight(other.getSnowHeight() + 1);
            return level.setBlock(other, other, true);
        }

        Block down = down();
        if (!down.isSolid()) {
            return false;
        }
        
        switch (down.getId()) {
            case BARRIER:
            case STRUCTURE_VOID:
                return false;
            case GRASS:
                setCovered(true);
                break;
            case TALL_GRASS:
                if (!level.setBlock(this, 0, this, true)) {
                    return false;
                }
                level.setBlock(block, 1, block, true, false);
                return true;
            default:
        }
        
        return this.getLevel().setBlock(block, this, true);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will move the block in layer 1 to layer 0 when breaking in layer 0")
    @Override
    public boolean onBreak(Item item) {
        if (layer != 0) {
            return super.onBreak(item);
        }
        return this.getLevel().setBlock(this, 0, getLevelBlockAtLayer(1), true, true);
    }


    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        if (layer != 0 || newBlock.getId() == getId()) {
            return;
        }

        Block layer1 = getLevelBlockAtLayer(1);
        if (layer1.getId() != TALL_GRASS) {
            return;
        }
        
        // Clear the layer1 block and do a small hack as workaround a vanilla client rendering bug
        Level level = getLevel();
        level.setBlock(this, 0, layer1, true, false);
        level.setBlock(this, 1, get(AIR), true, false);
        level.setBlock(this, 0, newBlock, true, false);
        Server.getInstance().getScheduler().scheduleDelayedTask(()-> {
            Player[] target = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
            Vector3[] blocks = {getLocation()};
            level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0, false);
            level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1, false);
        }, 10);
        
        Player[] target = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
        Vector3[] blocks = {getLocation()};
        level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0, false);
        level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1, false);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will melt on dry biomes and will melt gradually and will cover grass blocks")
    @Override
    public int onUpdate(int type) {
        super.onUpdate(type);
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Biome biome = Biome.getBiome(getLevel().getBiomeId(getFloorX(), getFloorZ()));
            if (biome.isDry() || this.getLevel().getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) >= 10) {
                melt();
                return Level.BLOCK_UPDATE_RANDOM;
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean covered = down().getId() == GRASS;
            if (isCovered() != covered) {
                setCovered(covered);
                level.setBlock(this, this, true);
                return type;
            }
        }
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean melt() {
        return melt(2);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean melt(int layers) {
        Preconditions.checkArgument(layers > 0, "Layers must be positive, got {}", layers);
        Block toMelt = this;
        while (toMelt.getIntValue(SNOW_HEIGHT) == SNOW_HEIGHT.getMaxValue()) {
            Block up = toMelt.up();
            if (up.getId() != SNOW_LAYER) {
                break;
            }
            
            toMelt = up;
        }
        
        int snowHeight = toMelt.getIntValue(SNOW_HEIGHT) - layers;
        Block newState = snowHeight < 0? get(AIR) : getCurrentState().withProperty(SNOW_HEIGHT, snowHeight).getBlock(toMelt);
        BlockFadeEvent event = new BlockFadeEvent(toMelt, newState);
        level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        
        return level.setBlock(toMelt, event.getNewState(), true);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns the snow_layer but with 0 height")
    @Override
    public Item toItem() {
        return Item.getBlock(BlockID.SNOW_LAYER);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the amount of snowballs that are dropped")
    @Override
    public Item[] getDrops(Item item) {
        if (!item.isShovel() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }
        
        int amount;
        switch (getSnowHeight()) {
            case 0:
            case 1:
            case 2:
                amount = 1;
                break;
            case 3:
            case 4:
                amount = 2;
                break;
            case 5:
            case 6:
                amount = 3;
                break;
            default:
            case 7:
                amount = 4;
        }
        return new Item[]{ Item.get(ItemID.SNOWBALL, 0, amount) };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns false when the height is 3+")
    @Override
    public boolean canPassThrough() {
        return getSnowHeight() < 3;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP && getSnowHeight() == SNOW_HEIGHT.getMaxValue();
    }
}
