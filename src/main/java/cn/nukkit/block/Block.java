package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.math.vector.Vector4i;

import javax.annotation.Nonnegative;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class Block implements Metadatable, Cloneable, AxisAlignedBB {

    protected final Identifier id;
    protected int meta;
    protected Vector3i position;
    protected int layer;
    protected Level level;

    public Block(Identifier id) {
        this.id = id;
    }

    public static long key(Vector3i position) {
        return key(position.getX(), position.getY(), position.getZ());
    }

    public static long key(int x, int y, int z) {
        if (y < 0 || y >= 256) {
            throw new IllegalArgumentException("Y coordinate y is out of range!");
        }
        return (((long) x & (long) 0xFFFFFFF) << 36) | (((long) y & (long) 0xFF) << 28) | ((long) z & (long) 0xFFFFFFF);
    }

    public static Vector3i fromKey(long key) {
        int x = (int) ((key >>> 36) & 0xFFFFFFF);
        int y = (int) ((key >> 27) & 0xFF);
        int z = (int) (key & 0xFFFFFFF);
        return Vector3i.from(x, y, z);
    }

    public static Block get(Identifier identifier) {
        return get(identifier, 0, 0, 0, 0, null);
    }

    public static Block get(Identifier identifier, int meta) {
        return get(identifier, meta, 0, 0, 0, null);
    }

    public static Block get(Identifier identifier, int meta, Vector3i pos) {
        return pos != null ? get(identifier, meta, pos.getX(), pos.getY(), pos.getZ(), null) :
                get(identifier, meta, 0, 0, 0, 0, null);
    }

    public static Block get(Identifier identifier, int meta, Vector3i pos, Level level) {
        return get(identifier, meta, pos.getX(), pos.getY(), pos.getZ(), 0, level);
    }

    public static Block get(Identifier identifier, int meta, Vector3i pos, int layer) {
        return pos != null ? get(identifier, meta, pos.getX(), pos.getY(), pos.getZ(), layer, null) :
                get(identifier, meta, 0, 0, 0, 0, null);
    }

    public static Block get(Identifier identifier, int meta, int x, int y, int z, Level level) {
        return get(identifier, meta, x, y, z, 0, level);
    }

    public static Block get(Identifier identifier, int meta, int x, int y, int z, int layer, Level level) {
        Block block = BlockRegistry.get().getBlock(identifier, meta).clone();
        block.position = Vector3i.from(x, y, z);
        block.layer = layer;
        block.level = level;
        return block;
    }

    @Deprecated
    public static Block get(int id) {
        return get(id, 0, 0, 0, 0, null);
    }

    @Deprecated
    public static Block get(int id, int meta) {
        return get(id, meta, 0, 0, 0, null);
    }

    @Deprecated
    public static Block get(int id, int meta, Vector4i pos) {
        return pos != null ? get(id, meta, pos.getX(), pos.getY(), pos.getZ(), pos.getW(), null) :
                get(id, meta, 0, 0, 0, null);
    }

    @Deprecated
    public static Block get(int id, int meta, int x, int y, int z, Level level) {
        return get(id, meta, x, y, z, 0, level);
    }

    @Deprecated
    public static Block get(int id, int meta, int x, int y, int z, int layer, Level level) {
        Block block = BlockRegistry.get().getBlock(id, meta).clone();
        block.position = Vector3i.from(x, y, z);
        block.layer = layer;
        block.level = level;
        return block;
    }

    //http://minecraft.gamepedia.com/Breaking
    private static float breakTime0(float blockHardness, boolean correctTool, boolean canHarvestWithHand,
                                    Identifier id, int toolType, int toolTier, int efficiencyLoreLevel, int hasteEffectLevel,
                                    boolean insideOfWaterWithoutAquaAffinity, boolean outOfWaterButNotOnGround) {
        float baseTime = ((correctTool || canHarvestWithHand) ? 1.5f : 5.0f) * blockHardness;
        float speed = 1.0f / baseTime;
        boolean isWoolBlock = id == WOOL, isCobweb = id == WEB;
        if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, isWoolBlock, isCobweb);
        speed += speedBonusByEfficiencyLore0(efficiencyLoreLevel);
        speed *= speedRateByHasteLore0(hasteEffectLevel);
        if (insideOfWaterWithoutAquaAffinity) speed *= 0.2f;
        if (outOfWaterButNotOnGround) speed *= 0.2f;
        return 1.0f / speed;
    }

    private static float toolBreakTimeBonus0(
            int toolType, int toolTier, boolean isWoolBlock, boolean isCobweb) {
        if (toolType == ItemTool.TYPE_SWORD) return isCobweb ? 15.0f : 1.0f;
        if (toolType == ItemTool.TYPE_SHEARS) return isWoolBlock ? 5.0f : 15.0f;
        if (toolType == ItemTool.TYPE_NONE) return 1.0f;
        switch (toolTier) {
            case ItemTool.TIER_WOODEN:
                return 2.0f;
            case ItemTool.TIER_STONE:
                return 4.0f;
            case ItemTool.TIER_IRON:
                return 6.0f;
            case ItemTool.TIER_DIAMOND:
                return 8.0f;
            case ItemTool.TIER_GOLD:
                return 12.0f;
            default:
                return 1.0f;
        }
    }

    private static float speedBonusByEfficiencyLore0(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0) return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private static float speedRateByHasteLore0(int hasteLoreLevel) {
        return 1.0f + (0.2f * hasteLoreLevel);
    }

    public static boolean equals(Block b1, Block b2, boolean checkDamage) {
        return b1 != null && b2 != null && b1.getId() == b2.getId() && (!checkDamage || b1.getMeta() == b2.getMeta());
    }

    public Vector3i getPosition() {
        return position;
    }

    public final void setPosition(Vector3i v) {
        this.position = v;
    }

    public int getLayer() {
        return layer;
    }

    public final void setLayer(int layer) {
        this.layer = layer;
    }

    //http://minecraft.gamepedia.com/Breaking
    public boolean canHarvestWithHand() {  //used for calculating breaking time
        return true;
    }

    public boolean isBreakable(Item item) {
        return true;
    }

    public int tickRate() {
        return 10;
    }

    public Level getLevel() {
        return level;
    }

    public int onUpdate(int type) {
        return 0;
    }

    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(Item item, Player player) {
        return false;
    }

    public final void setLevel(Level level) {
        this.level = level;
    }

    public Chunk getChunk() {
        return level.getChunk(this.position);
    }

    public int getBurnChance() {
        return 0;
    }

    public int getBurnAbility() {
        return 0;
    }

    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    public int getX() {
        return this.position.getX();
    }

    public int getLightLevel() {
        return 0;
    }

    public int getY() {
        return this.position.getY();
    }

    public boolean canBePlaced() {
        return true;
    }

    public boolean canBeReplaced() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public int getFilterLevel() {
        if (isSolid()) {
            if (isTransparent()) {
                if (this instanceof BlockLiquid || this instanceof BlockIce) {
                    return 2;
                }
            } else {
                return 15;
            }
        }
        return 1;
    }

    public boolean canBeActivated() {
        return false;
    }

    public boolean hasEntityCollision() {
        return false;
    }

    public boolean canPassThrough() {
        return false;
    }

    public boolean canBePushed() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return false;
    }

    public int getComparatorInputOverride() {
        return 0;
    }

    public boolean canBeClimbed() {
        return false;
    }

    public BlockColor getColor() {
        return BlockColor.VOID_BLOCK_COLOR;
    }

    public boolean canBeFlooded() {
        return false;
    }

    public final Identifier getId() {
        return id;
    }

    public int getZ() {
        return this.position.getZ();
    }

    public boolean isValid() {
        return level != null && position != null;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        return this.getLevel().setBlock(this.getPosition(), this, true, true);
    }

    public boolean onBreak(Item item) {
        return removeBlock(true);
    }

    final protected boolean removeBlock(boolean update) {
        if (this.isWaterlogged()) {
            Block water = getLevel().getBlock(getX(), getY(), getZ(), 1);
            getLevel().setBlock(this.getPosition(), water, true, false);
            return getLevel().setBlock(getX(), getY(), getZ(), 1, Block.get(AIR), true, update);
        }
        return this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, update);
    }

    public boolean onBreak(Item item, Player player) {
        return onBreak(item);
    }

    public float getHardness() {
        return 10;
    }

    public String getDescriptionId() {
        return "tile." + id.getName() + ".name";
    }

    public float getResistance() {
        return 1;
    }

    public float getFrictionFactor() {
        return 0.6f;
    }

    public Vector3f addVelocityToEntity(Entity entity, Vector3f vector) {
        return vector;
    }

    private static int toolType0(Item item) {
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    private static boolean correctTool0(int blockToolType, Item item) {
        return (blockToolType == ItemTool.TYPE_SWORD && item.isSword()) ||
                (blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                (blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                (blockToolType == ItemTool.TYPE_AXE && item.isAxe()) ||
                (blockToolType == ItemTool.TYPE_SHEARS && item.isShears()) ||
                blockToolType == ItemTool.TYPE_NONE;
    }

    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    public final int getMeta() {
        return meta;
    }

    public float getBreakTime(Item item, Player player) {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null");
        Objects.requireNonNull(player, "getBreakTime: Player can not be null");
        float blockHardness = getHardness();
        boolean correctTool = correctTool0(getToolType(), item);
        boolean canHarvestWithHand = canHarvestWithHand();
        Identifier id = getId();
        int itemToolType = toolType0(item);
        int itemTier = item.getTier();
        int efficiencyLoreLevel = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                .map(Enchantment::getLevel).orElse(0);
        int hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                .map(Effect::getAmplifier).orElse((byte) 0);
        boolean insideOfWaterWithoutAquaAffinity = player.isInsideOfWater() &&
                Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                        .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
        boolean outOfWaterButNotOnGround = (!player.isInsideOfWater()) && (!player.isOnGround());
        return breakTime0(blockHardness, correctTool, canHarvestWithHand, id, itemToolType, itemTier,
                efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround);
    }

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
    }

    public Block getSide(BlockFace face) {
        return this.getSide(face, true);
    }

    /**
     * @param item item used
     * @return break time
     * @deprecated This function is lack of Player class and is not accurate enough, use #getBreakTime(Item, Player)
     */
    @Deprecated
    public float getBreakTime(Item item) {
        float base = this.getHardness() * 1.5f;
        if (this.canBeBrokenWith(item)) {
            if (this.getToolType() == ItemTool.TYPE_SHEARS && item.isShears()) {
                base /= 15;
            } else if (
                    (this.getToolType() == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                            (this.getToolType() == ItemTool.TYPE_AXE && item.isAxe()) ||
                            (this.getToolType() == ItemTool.TYPE_SHOVEL && item.isShovel())
            ) {
                int tier = item.getTier();
                switch (tier) {
                    case ItemTool.TIER_WOODEN:
                        base /= 2;
                        break;
                    case ItemTool.TIER_STONE:
                        base /= 4;
                        break;
                    case ItemTool.TIER_IRON:
                        base /= 6;
                        break;
                    case ItemTool.TIER_DIAMOND:
                        base /= 8;
                        break;
                    case ItemTool.TIER_GOLD:
                        base /= 12;
                        break;
                }
            }
        } else {
            base *= 3.33f;
        }

        if (item.isSword()) {
            base *= 0.5f;
        }

        return base;
    }

    public Block getSide(BlockFace face, int step) {
        return this.getSide(face, step, true);
    }

    public Block getSide(BlockFace face, boolean load) {
        if (this.isValid()) {
            return this.getLevel().getBlock(this.getX() + face.getXOffset(), this.getY() + face.getYOffset(), this.getZ() + face.getZOffset());
        }
        return this.getSide(face, 1, load);
    }

    public Block up() {
        return up(1);
    }

    public Block up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Block down() {
        return down(1);
    }

    public Block down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Block north() {
        return north(1);
    }

    public Block north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Block south() {
        return south(1);
    }

    public Block south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Block east() {
        return east(1);
    }

    public Block east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Block west() {
        return west(1);
    }

    public Block west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public Block getSide(BlockFace face, int step, boolean load) {
        if (this.isValid()) {
            if (step == 1) {
                if (load) {
                    return this.getLevel().getBlock(this.getX() + face.getXOffset(), this.getY() + face.getYOffset(),
                            this.getZ() + face.getZOffset());
                } else {
                    return this.getLevel().getLoadedBlock(this.getX() + face.getXOffset(), this.getY() + face.getYOffset(),
                            this.getZ() + face.getZOffset());
                }
            } else {
                if (load) {
                    return this.getLevel().getBlock(this.getX() + face.getXOffset() * step,
                            this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
                } else {
                    return this.getLevel().getLoadedBlock(this.getX() + face.getXOffset() * step,
                            this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
                }
            }
        }
        Block block = Block.get(AIR, 0);
        block.position = this.position.add(face.getUnitVector().mul(step));
        return block;
    }

    public Block getExtra() {
        return layer(1);
    }

    public Block layer(@Nonnegative int layer) {
        if (this.isValid()) {
            return this.getLevel().getBlock(position, layer);
        }
        Block block = Block.get(AIR);
        block.layer = layer;
        return block;
    }

    public boolean collidesWithBB(AxisAlignedBB bb) {
        return collidesWithBB(bb, false);
    }

    public boolean collidesWithBB(AxisAlignedBB bb, boolean collisionBB) {
        AxisAlignedBB bb1 = collisionBB ? this.getCollisionBoundingBox() : this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }

    public void onEntityCollide(Entity entity) {

    }

    public AxisAlignedBB getBoundingBox() {
        return this.recalculateBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.recalculateCollisionBoundingBox();
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("Block(id=%s, data=%s, position=%s)", this.id, this.meta, this.position);
    }

    @Override
    public float getMinX() {
        return this.position.getX();
    }

    @Override
    public float getMinY() {
        return this.position.getY();
    }

    @Override
    public float getMinZ() {
        return this.position.getZ();
    }

    @Override
    public float getMaxX() {
        return this.position.getX() + 1;
    }

    @Override
    public float getMaxY() {
        return this.position.getY() + 1;
    }

    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return getBoundingBox();
    }

    @Override
    public float getMaxZ() {
        return this.position.getZ() + 1;
    }

    public String getSaveId() {
        String name = getClass().getName();
        return name.substring(16);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);

        }
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
        }
    }

    public MovingObjectPosition calculateIntercept(Vector3f pos1, Vector3f pos2) {
        AxisAlignedBB bb = this.getBoundingBox();
        if (bb == null) {
            return null;
        }

        Vector3f v1 = NukkitMath.getIntermediateWithXValue(pos1, pos2, bb.getMinX());
        Vector3f v2 = NukkitMath.getIntermediateWithXValue(pos1, pos2, bb.getMaxX());
        Vector3f v3 = NukkitMath.getIntermediateWithYValue(pos1, pos2, bb.getMinY());
        Vector3f v4 = NukkitMath.getIntermediateWithYValue(pos1, pos2, bb.getMaxY());
        Vector3f v5 = NukkitMath.getIntermediateWithZValue(pos1, pos2, bb.getMinZ());
        Vector3f v6 = NukkitMath.getIntermediateWithZValue(pos1, pos2, bb.getMaxZ());

        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3f vector = v1;

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        int f = -1;

        if (vector == v1) {
            f = 4;
        } else if (vector == v2) {
            f = 5;
        } else if (vector == v3) {
            f = 0;
        } else if (vector == v4) {
            f = 1;
        } else if (vector == v5) {
            f = 2;
        } else if (vector == v6) {
            f = 3;
        }

        return MovingObjectPosition.fromBlock(this.position, f, vector.add(this.position.toFloat()));
    }

    public int getWeakPower(BlockFace face) {
        return 0;
    }

    public int getStrongPower(BlockFace side) {
        return 0;
    }

    public boolean isPowerSource() {
        return false;
    }

    public String getLocationHash() {
        return this.getX() + ":" + this.getY() + ":" + this.getZ() + ":" + this.getLayer();
    }

    public int getDropExp() {
        return 0;
    }

    public boolean isNormalBlock() {
        return !isTransparent() && isSolid() && !isPowerSource();
    }

    public static boolean equals(Block b1, Block b2) {
        return equals(b1, b2, true);
    }

    public Block clone() {
        try {
            return (Block) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public Item toItem() {
        return Item.get(this.id, this.meta);
    }

    public boolean canSilkTouch() {
        return false;
    }

    public boolean canWaterlogSource() {
        return false;
    }

    public boolean canWaterlogFlowing() {
        return false;
    }

    public boolean isWaterlogged() {
        Block b = getLevel().getBlock(this.getX(), this.getY(), this.getZ(), 1);
        return (b.getId() == WATER || b.getId() == FLOWING_WATER);
    }

    /**
     * Returns the data of the water which is waterlogging this block.
     * @return -1 if the block is not waterlogged, the water meta otherwise.
     */
    public int getWaterloggingWaterDamage() {
        Block block = getLevel().getBlock(this.getX(), this.getY(), this.getZ(), 1);
        return (block.getId() != WATER && block.getId() != FLOWING_WATER) ? -1 : block.getMeta();
    }

}
