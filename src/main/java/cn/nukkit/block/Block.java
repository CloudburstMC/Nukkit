package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.material.BlockType;
import cn.nukkit.utils.material.MaterialType;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class Block extends Position implements Metadatable, Cloneable, AxisAlignedBB, BlockID {

    @SuppressWarnings("UnnecessaryBoxing")
    public static final int MAX_BLOCK_ID = Integer.valueOf("2048");
    public static final int DATA_BITS = 6;
    public static final int DATA_SIZE = 1 << DATA_BITS;
    public static final int DATA_MASK = DATA_SIZE - 1;

    public static final BlockLayer LAYER_NORMAL = BlockLayer.NORMAL;
    public static final BlockLayer LAYER_WATERLOGGED = BlockLayer.WATERLOGGED;

    @SuppressWarnings("rawtypes")
    public static Class[] list;
    public static Block[] fullList;
    public static int[] light;
    public static int[] lightFilter;
    public static boolean[] solid;
    public static double[] hardness;
    public static boolean[] transparent;
    public static boolean[] hasMeta;

    private BlockLayer layer = LAYER_NORMAL;

    private BlockType materialType;

    /**
     * A commonly used block face pattern
     */
    protected static final int[] FACES2534 = {2, 5, 3, 4};

    protected Block() {

    }

    public static void init() {
        if (list == null) {
            list = new Class[MAX_BLOCK_ID];
            fullList = new Block[MAX_BLOCK_ID * DATA_SIZE];
            light = new int[MAX_BLOCK_ID];
            lightFilter = new int[MAX_BLOCK_ID];
            solid = new boolean[MAX_BLOCK_ID];
            hardness = new double[MAX_BLOCK_ID];
            transparent = new boolean[MAX_BLOCK_ID];
            hasMeta = new boolean[MAX_BLOCK_ID];

            Blocks.init();

            for (int id = 0; id < MAX_BLOCK_ID; id++) {
                Class<?> c = list[id];
                if (c != null) {
                    Block block;
                    try {
                        block = (Block) c.newInstance();
                        try {
                            @SuppressWarnings("rawtypes")
                            Constructor constructor = c.getDeclaredConstructor(int.class);
                            constructor.setAccessible(true);
                            for (int data = 0; data < (1 << DATA_BITS); ++data) {
                                int fullId = (id << DATA_BITS) | data;
                                Block blockState;
                                try {
                                    blockState = (Block) constructor.newInstance(data);
                                    if (blockState.getDamage() != data) {
                                        blockState = new BlockUnknown(id, data);
                                    }
                                } catch (Exception e) {
                                    Server.getInstance().getLogger().error("Error while registering " + c.getName(), e);
                                    blockState = new BlockUnknown(id, data);
                                }
                                fullList[fullId] = blockState;
                            }
                            hasMeta[id] = true;
                        } catch (NoSuchMethodException ignore) {
                            for (int data = 0; data < DATA_SIZE; ++data) {
                                int fullId = (id << DATA_BITS) | data;
                                fullList[fullId] = block;
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error while registering " + c.getName(), e);
                    }

                    solid[id] = block.isSolid();
                    transparent[id] = block.isTransparent();
                    hardness[id] = block.getHardness();
                    light[id] = block.getLightLevel();

                    if (block.isSolid()) {
                        if (block.isTransparent()) {
                            if (block instanceof BlockLiquid || block instanceof BlockIce) {
                                lightFilter[id] = 2;
                            } else {
                                lightFilter[id] = 1;
                            }
                        } else if (block instanceof BlockSlime) {
                            lightFilter[id] = 1;
                        } else if (id == CAULDRON_BLOCK) {
                            lightFilter[id] = 3;
                        }else {
                            lightFilter[id] = 15;
                        }
                    } else {
                        lightFilter[id] = 1;
                    }
                } else {
                    lightFilter[id] = 1;
                    for (int data = 0; data < DATA_SIZE; ++data) {
                        fullList[(id << DATA_BITS) | data] = new BlockUnknown(id, data);
                    }
                }
            }
        }
    }

    public static Block get(MaterialType type) {
        return get(type, 0);
    }

    public static Block get(MaterialType type, Integer meta) {
        if (!(type instanceof BlockType)) {
            throw new IllegalArgumentException("Expected BlockType, got " + type.getClass().getSimpleName());
        }
        return get(type.getLegacyId(), meta);
    }

    public static Block get(int id) {
        if (id < 0) {
            id = 255 - id;
        }

        return fullList[id << DATA_BITS].clone();
    }

    public static Block get(int id, Integer meta) {
        if (id < 0) {
            id = 255 - id;
        }

        int fullId = meta == null ? (id << DATA_BITS ) : ((id << DATA_BITS) | meta);

        return fullList[fullId].clone();
    }

    public static Block get(int id, Integer meta, Position pos) {
        return get(id, meta, pos, LAYER_NORMAL);
    }

    public static Block get(int id, Integer meta, Position pos, BlockLayer layer) {
        if (id < 0) {
            id = 255 - id;
        }

        Block block;
        if (meta != null && meta > DATA_SIZE) {
            block = fullList[id << DATA_BITS].clone();
            block.setDamage(meta);
        } else {
            block = fullList[(id << DATA_BITS) | (meta == null ? 0 : meta)].clone();
        }

        if (pos != null) {
            block.x = pos.x;
            block.y = pos.y;
            block.z = pos.z;
            block.level = pos.level;
            block.layer = layer;
        }
        return block;
    }

    public static Block get(int id, int data) {
        if (id < 0) {
            id = 255 - id;
        }

        int fullId = (id << DATA_BITS ) | data;

        return fullList[fullId].clone();
    }

    public static Block get(int fullId, Level level, int x, int y, int z) {
        return get(fullId, level, x, y, z, LAYER_NORMAL);
    }

    public static Block get(int fullId, Level level, int x, int y, int z, BlockLayer layer) {
        Block block = fullList[fullId].clone();

        block.x = x;
        block.y = y;
        block.z = z;
        block.level = level;
        block.layer = layer;
        return block;
    }

    public static int getBlockLight(int blockId) {
        return light[blockId];
    }

    public static int getBlockLightFilter(int blockId) {
        return lightFilter[blockId];
    }

    public static boolean isBlockSolidById(int blockId) {
        return solid[blockId];
    }

    public static boolean isBlockTransparentById(int blockId) {
        return transparent[blockId];
    }

    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        return this.canPlaceOn(block.down(), target) && this.getLevel().setBlock(this, this, true, true);
    }

    public boolean canPlaceOn(Block floor, Position pos) {
        return this.canBePlaced();
    }

    public boolean canHarvest(Item item) {
        return this.canHarvestWithHand() || this.getToolTier() == 0 || this.getToolType() == ItemTool.TYPE_NONE || correctTool0(this.getToolType(), item, this.getId()) && item.getTier() >= this.getToolType();
    }

    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.NO_WATERLOGGING;
    }

    public enum WaterloggingType {
        /**
         * Block is not waterloggable
         */
        NO_WATERLOGGING,
        /**
         * If possible, water will be set to second layer when the block is placed into water
         */
        WHEN_PLACED_IN_WATER,
        /**
         * Water will flow into the block and water will be set to second layer
         */
        FLOW_INTO_BLOCK
    }

    public final boolean canWaterloggingFlowInto() {
        return this.canBeFlowedInto() || this.getWaterloggingType() == WaterloggingType.FLOW_INTO_BLOCK;
    }

    public boolean canHarvestWithHand() {
        return true;
    }

    public boolean isBreakable(Item item) {
        return true;
    }

    public int tickRate() {
        return 10;
    }

    public boolean onBreak(Item item, Player player) {
        return this.onBreak(item);
    }

    public boolean onBreak(Item item) {
        return this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
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

    public double getHardness() {
        return 10;
    }

    public double getResistance() {
        return 1;
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

    public int getToolTier() {
        return 0;
    }

    public double getFrictionFactor() {
        return 0.6;
    }

    public int getLightLevel() {
        return 0;
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

    public boolean canBeFlowedInto() {
        return false;
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

    public boolean breakWhenPushed() {
        return false;
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

    public abstract String getName();

    public abstract int getId();

    public BlockType getBlockType() {
        if (this.materialType == null) {
            this.materialType = BlockTypes.getFromLegacy(this.getId());
        }
        return this.materialType;
    }

    /**
     * The full id is a combination of the id and data.
     * @return full id
     */
    public int getFullId() {
        return getId() << DATA_BITS;
    }

    public int getItemId() {
        int id = this.getId();
        if (id > 255) {
            return 255 - id;
        }
        return id;
    }


    public void addVelocityToEntity(Entity entity, Vector3 vector) {

    }

    public int getDamage() {
        return 0;
    }

    public void setDamage(int meta) {
    }

    public final void setDamage(Integer meta) {
        setDamage((meta == null ? 0 : meta & 0x0f));
    }

    final public void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
    }

    public Item[] getDrops(Item item) {
        if (this.getId() < 0 || this.getId() > list.length) {
            return new Item[0];
        }

        if (this.canHarvestWithHand() || this.canHarvest(item)) {
            return new Item[]{this.toItem()};
        }
        return new Item[0];
    }

    private static double toolBreakTimeBonus0(int toolType, int toolTier, int blockId) {
        if (toolType == ItemTool.TYPE_SWORD) return blockId == Block.COBWEB ? 15.0 : 1.0;
        if (toolType == ItemTool.TYPE_SHEARS) {
            if (blockId == Block.WOOL || blockId == LEAVES || blockId == LEAVES2) {
                return 5.0;
            } else if (blockId == COBWEB) {
                return 15.0;
            }
            return 1.0;
        }
        if (toolType == ItemTool.TYPE_NONE) return 1.0;
        switch (toolTier) {
            case ItemTool.TIER_WOODEN:
                return 2.0;
            case ItemTool.TIER_STONE:
                return 4.0;
            case ItemTool.TIER_IRON:
                return 6.0;
            case ItemTool.TIER_DIAMOND:
                return 8.0;
            case ItemTool.TIER_NETHERITE:
                return 9.0;
            case ItemTool.TIER_GOLD:
                return 12.0;
            default:
                return 1.0;
        }
    }

    private static double speedBonusByEfficiencyLore0(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0) return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private static double speedRateByHasteLore0(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

    private static int toolType0(Item item) {
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isHoe()) return ItemTool.TYPE_HOE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    private static boolean correctTool0(int blockToolType, Item item, int blockId) {
        if (item.isShears() && (blockId == COBWEB || blockId == LEAVES || blockId == LEAVES2)) {
            return true;
        }

        if ((blockId == LEAVES && item.isHoe()) ||
                (blockId == LEAVES2 && item.isHoe())) {
            return (blockToolType == ItemTool.TYPE_SHEARS && item.isHoe());
        }

        return correctTool(blockToolType, item);
    }

    private static boolean correctTool(int blockToolType, Item item) {
        return (blockToolType == ItemTool.TYPE_SWORD && item.isSword()) ||
                (blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                (blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                (blockToolType == ItemTool.TYPE_AXE && item.isAxe()) ||
                (blockToolType == ItemTool.TYPE_HOE && item.isHoe()) ||
                (blockToolType == ItemTool.TYPE_SHEARS && item.isShears()) ||
                blockToolType == ItemTool.TYPE_NONE;
    }

    //http://minecraft.gamepedia.com/Breaking
    private static double breakTime0(double blockHardness, boolean correctTool, boolean canHarvestWithHand,
                                     int blockId, int toolType, int toolTier, int efficiencyLoreLevel, int hasteEffectLevel,
                                     boolean insideOfWaterWithoutAquaAffinity, boolean outOfWaterButNotOnGround) {
        double baseTime = ((correctTool || canHarvestWithHand) ? 1.5 : 5.0) * blockHardness;
        double speed = 1.0 / baseTime;
        if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, blockId);
        speed += correctTool ? speedBonusByEfficiencyLore0(efficiencyLoreLevel) : 0;
        speed *= speedRateByHasteLore0(hasteEffectLevel);
        if (insideOfWaterWithoutAquaAffinity) speed *= 0.2;
        if (outOfWaterButNotOnGround) speed *= 0.2;
        return 1.0 / speed;
    }

    public double getBreakTime(Item item, Player player) {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null");
        Objects.requireNonNull(player, "getBreakTime: Player can not be null");
        double blockHardness = getHardness();

        if (blockHardness == 0) {
            return 0;
        }

        int blockId = getId();
        boolean correctTool = correctTool0(getToolType(), item, blockId)
                || item.isShears() && (blockId == COBWEB || blockId == LEAVES || blockId == LEAVES2);
        boolean canHarvestWithHand = canHarvestWithHand();
        int itemToolType = toolType0(item);
        int itemTier = item.getTier();
        int efficiencyLoreLevel = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                .map(Enchantment::getLevel).orElse(0);
        int hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                .map(Effect::getAmplifier).orElse(0);
        boolean insideOfWaterWithoutAquaAffinity = player.isInsideOfWater() &&
                Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                        .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
        boolean outOfWaterButNotOnGround = (!player.isInsideOfWater()) && (!player.isOnGround());
        return breakTime0(blockHardness, correctTool, canHarvestWithHand, blockId, itemToolType, itemTier,
                efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround);
    }

    /**
     * Deprecated: This function lacks the Player and is not accurate enough, use getBreakTime(Item, Player) instead
     * @param item item used to break the block
     * @return break time in seconds
     */
    @Deprecated
    public double getBreakTime(Item item) {
        double base = this.getHardness() * 1.5;
        if (this.canBeBrokenWith(item)) {
            if (this.getToolType() == ItemTool.TYPE_SHEARS && item.isShears()) {
                base /= 15;
            } else if (
                    (this.getToolType() == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                            (this.getToolType() == ItemTool.TYPE_AXE && item.isAxe()) ||
                            (this.getToolType() == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                            (this.getToolType() == ItemTool.TYPE_HOE && item.isHoe())
            ) {
                switch (item.getTier()) {
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
                    case ItemTool.TIER_NETHERITE:
                        base /= 9;
                        break;
                    case ItemTool.TIER_GOLD:
                        base /= 12;
                        break;
                }
            }
        } else {
            base *= 3.33;
        }

        if (item.isSword()) {
            base *= 0.5;
        }

        return base;
    }

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
    }

    public Block getSide(BlockFace face) {
        return this.getSide(this.layer, face, 1);
    }

    public Block getSide(BlockFace face, int step) {
        return this.getSide(this.layer, face, step);
    }

    public Block getSide(BlockLayer layer, BlockFace face) {
        return this.getSide(layer, face, 1);
    }

    public Block getSide(BlockLayer layer, BlockFace face, int step) {
        if (this.isValid()) {
            return this.getLevel().getBlock(super.getSide(face, step), layer, true);
        }
        return Block.get(AIR, 0, Position.fromObject(this.getSideVec(face, step)), layer);
    }

    protected Block getSideIfLoaded(BlockFace face) {
        if (this.isValid()) {
            return this.level.getBlock(null,
                    (int) this.x + face.getXOffset(), (int) this.y + face.getYOffset(), (int) this.z + face.getZOffset(),
                    BlockLayer.NORMAL, false);
        }
        return Block.get(AIR, 0, Position.fromObject(this.getSideVec(face, 1)), BlockLayer.NORMAL);
    }

    protected Block getSideIfLoadedOrNull(BlockFace face) {
        if (this.isValid()) {
            int cx = ((int) this.x + face.getXOffset()) >> 4;
            int cz = ((int) this.z + face.getZOffset()) >> 4;

            FullChunk chunk = this.level.getChunkIfLoaded(cx, cz);
            if (chunk == null) {
                return null;
            }

            return this.level.getBlock(chunk,
                    (int) this.x + face.getXOffset(), (int) this.y + face.getYOffset(), (int) this.z + face.getZOffset(),
                    BlockLayer.NORMAL, false);
        }

        return Block.get(AIR, 0, Position.fromObject(this.getSideVec(face, 1)), BlockLayer.NORMAL);
    }

    public Block up() {
        return up(1);
    }

    public Block up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Block up(int step, BlockLayer layer) {
        return this.getSide(layer, BlockFace.UP, step);
    }

    public Block down() {
        return down(1);
    }

    public Block down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Block down(int step, BlockLayer layer) {
        return this.getSide(layer, BlockFace.DOWN, step);
    }

    public Block north() {
        return north(1);
    }

    public Block north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Block north(int step, BlockLayer layer) {
        return this.getSide(layer, BlockFace.NORTH, step);
    }

    public Block south() {
        return south(1);
    }

    public Block south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Block south(int step, BlockLayer layer) {
        return this.getSide(layer, BlockFace.SOUTH, step);
    }

    public Block east() {
        return east(1);
    }

    public Block east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Block east(int step, BlockLayer layer) {
        return this.getSide(layer, BlockFace.EAST, step);
    }

    public Block west() {
        return west(1);
    }

    public Block west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public Block west(int step, BlockLayer layer) {
        return this.getSide(layer, BlockFace.WEST, step);
    }

    @Override
    public String toString() {
        return "Block[" + this.getName() + '|' + this.layer + "] (" + this.getId() + ':' + this.getDamage() + ')';
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

    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this.getBoundingBox();
    }

    @Override
    public double getMinX() {
        return this.x;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z;
    }

    @Override
    public double getMaxX() {
        return this.x + 1;
    }

    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1;
    }

    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        AxisAlignedBB bb = this.getBoundingBox();
        if (bb == null) {
            return null;
        }

        Vector3 v1 = pos1.getIntermediateWithXValue(pos2, bb.getMinX());
        Vector3 v2 = pos1.getIntermediateWithXValue(pos2, bb.getMaxX());
        Vector3 v3 = pos1.getIntermediateWithYValue(pos2, bb.getMinY());
        Vector3 v4 = pos1.getIntermediateWithYValue(pos2, bb.getMaxY());
        Vector3 v5 = pos1.getIntermediateWithZValue(pos2, bb.getMinZ());
        Vector3 v6 = pos1.getIntermediateWithZValue(pos2, bb.getMaxZ());

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

        Vector3 vector = v1;

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

        return MovingObjectPosition.fromBlock((int) this.x, (int) this.y, (int) this.z, f, vector.add(this.x, this.y, this.z));
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

    public Block clone() {
        return (Block) super.clone();
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
        return this.getFloorX() + ":" + this.getFloorY() + ':' + this.getFloorZ();
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

    public static boolean equals(Block b1, Block b2, boolean checkDamage) {
        return b1 != null && b2 != null && b1.getId() == b2.getId() && (!checkDamage || b1.getDamage() == b2.getDamage());
    }

    public Item toItem() {
        return new ItemBlock(this, this.getDamage(), 1);
    }

    public boolean canSilkTouch() {
        return false;
    }

    public void setLayer(BlockLayer layer) {
        this.layer = layer;
    }

    public BlockLayer getLayer() {
        return this.layer;
    }

    public static boolean canConnectToFullSolid(Block down) {
        if (down.isTransparent()) {
            switch (down.getId()) {
                case BEACON:
                case ICE:
                case GLASS:
                case STAINED_GLASS:
                case BARRIER:
                case GLOWSTONE:
                case SEA_LANTERN:
                case MANGROVE_ROOTS:
                case MUDDY_MANGROVE_ROOTS:
                case MONSTER_SPAWNER:
                    return true;
            }
            return false;
        }
        return true;
    }

    protected static boolean canStayOnFullSolid(Block down) {
        if (canConnectToFullSolid(down)) {
            return true;
        }
        switch (down.getId()) {
            case SCAFFOLDING:
            case HOPPER_BLOCK:
                return true;
        }
        if (down instanceof BlockSlab) {
            return ((BlockSlab) down).hasTopBit();
        }
        return down instanceof BlockTrapdoor && ((BlockTrapdoor) down).isTop() && !((BlockTrapdoor) down).isOpen();
    }

    protected static boolean canStayOnFullNonSolid(Block down) {
        if (canStayOnFullSolid(down)) {
            return true;
        }
        switch (down.getId()) {
            case COMPOSTER:
            case CAULDRON_BLOCK:
            case LAVA_CAULDRON:
                return true;
        }
        return false;
    }

    protected boolean isNarrowSurface() {
        return this instanceof BlockGlassPane || this instanceof BlockFence || this instanceof BlockWall || this instanceof BlockChain || this instanceof BlockIronBars;
    }

    public boolean alwaysDropsOnExplosion() {
        return false;
    }

    /**
     * Returns true for WATER and STILL_WATER, false for others
     */
    public static boolean isWater(int id) {
        return id == WATER || id == STILL_WATER;
    }

    public Block setUpdatePos(Vector3 pos) {
        return this; // Only need to save this for observers
    }

    public PersistentDataContainer getPersistentDataContainer() {
        if (!this.isValid()) {
            throw new IllegalStateException("Block does not have valid level");
        }
        return this.level.getPersistentDataContainer(this);
    }

    @SuppressWarnings("unused")
    public boolean hasPersistentDataContainer() {
        if (!this.isValid()) {
            throw new IllegalStateException("Block does not have valid level");
        }
        return this.level.hasPersistentDataContainer(this);
    }
}
