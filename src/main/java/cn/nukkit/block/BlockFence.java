package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static cn.nukkit.math.VectorMath.calculateFace;

/**
 * @author xtypr
 * @since 2015/12/7
 */
@PowerNukkitDifference(info = "Implements BlockConnectable only on PowerNukkit", since = "1.3.0.0-PN")
public class BlockFence extends BlockTransparentMeta implements BlockConnectable {
    public static final BlockProperties PROPERTIES = new BlockProperties(WoodType.PROPERTY);

    @Deprecated @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
    public static final int FENCE_OAK = 0;
    @Deprecated @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
    public static final int FENCE_SPRUCE = 1;
    @Deprecated @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
    public static final int FENCE_BIRCH = 2;
    @Deprecated @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
    public static final int FENCE_JUNGLE = 3;
    @Deprecated @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
    public static final int FENCE_ACACIA = 4;
    @Deprecated @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
    public static final int FENCE_DARK_OAK = 5;

    public BlockFence() {
        this(0);
    }

    public BlockFence(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setWoodType(@Nullable WoodType woodType) {
        setPropertyValue(WoodType.PROPERTY, woodType);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Optional<WoodType> getWoodType() {
        return Optional.of(getPropertyValue(WoodType.PROPERTY));
    }

    @Override
    public String getName() {
        return getPropertyValue(WoodType.PROPERTY).getEnglishName() + " Fence";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.north());
        boolean south = this.canConnect(this.south());
        boolean west = this.canConnect(this.west());
        boolean east = this.canConnect(this.east());
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public boolean canConnect(Block block) {
        if (block instanceof BlockFence) {
            if (block.getId() == NETHER_BRICK_FENCE || this.getId() == NETHER_BRICK_FENCE) {
                return block.getId() == this.getId();
            }
            return true;
        }
        if (block instanceof BlockTrapdoor) {
            BlockTrapdoor trapdoor = (BlockTrapdoor) block;
            return trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
        }
        return block instanceof BlockFenceGate || block.isSolid() && !block.isTransparent();
    }

    @Override
    public BlockColor getColor() {
        return getPropertyValue(WoodType.PROPERTY).getColor();
    }
}
