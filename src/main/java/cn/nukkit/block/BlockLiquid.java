package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockLiquid extends BlockTransparentMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty LIQUID_DEPTH = new IntBlockProperty("liquid_depth", false, 15);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(LIQUID_DEPTH);

    private static final byte CAN_FLOW_DOWN = 1;
    private static final byte CAN_FLOW = 0;
    private static final byte BLOCKED = -1;
    public int adjacentSources = 0;
    protected Vector3 flowVector = null;
    private final Long2ByteMap flowCostVisited = new Long2ByteOpenHashMap();

    protected BlockLiquid(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - getFluidHeightPercent();
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    public boolean usesWaterLogging() {
        return false;
    }

    public float getFluidHeightPercent() {
        float d = getLiquidDepth();
        if (d >= 8) {
            d = 0;
        }

        return (d + 1) / 9f;
    }

    protected int getFlowDecay(Block block) {
        if (block.getId() != this.getId()) {
            Block layer1 = block.getLevelBlockAtLayer(1);
            if (layer1.getId() != this.getId()) {
                return -1;
            } else {
                return ((BlockLiquid)layer1).getLiquidDepth();
            }
        }
        return ((BlockLiquid)block).getLiquidDepth();
    }

    protected int getEffectiveFlowDecay(Block block) {
        if (block.getId() != this.getId()) {
            block = block.getLevelBlockAtLayer(1);
            if (block.getId() != this.getId()) {
                return -1;
            }
        }
        int decay = ((BlockLiquid)block).getLiquidDepth();
        if (decay >= 8) {
            decay = 0;
        }
        return decay;
    }

    public void clearCaches() {
        this.flowVector = null;
        this.flowCostVisited.clear();
    }

    public Vector3 getFlowVector() {
        if (this.flowVector != null) {
            return this.flowVector;
        }
        Vector3 vector = new Vector3(0, 0, 0);
        int decay = this.getEffectiveFlowDecay(this);
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;
            switch (j) {
                case 0:
                    --x;
                    break;
                case 1:
                    x++;
                    break;
                case 2:
                    z--;
                    break;
                default:
                    z++;
            }
            Block sideBlock = this.level.getBlock(x, y, z);
            int blockDecay = this.getEffectiveFlowDecay(sideBlock);
            if (blockDecay < 0) {
                if (!sideBlock.canBeFlowedInto()) {
                    continue;
                }
                blockDecay = this.getEffectiveFlowDecay(this.level.getBlock(x, y - 1, z));
                if (blockDecay >= 0) {
                    int realDecay = blockDecay - (decay - 8);
                    vector.x += (sideBlock.x - this.x) * realDecay;
                    vector.y += (sideBlock.y - this.y) * realDecay;
                    vector.z += (sideBlock.z - this.z) * realDecay;
                }
            } else {
                int realDecay = blockDecay - decay;
                vector.x += (sideBlock.x - this.x) * realDecay;
                vector.y += (sideBlock.y - this.y) * realDecay;
                vector.z += (sideBlock.z - this.z) * realDecay;
            }
        }
        if (getLiquidDepth() >= 8) {
            if (!this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z - 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z + 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y + 1, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y + 1, (int) this.z))) {
                vector = vector.normalize().add(0, -6, 0);
            }
        }
        return this.flowVector = vector.normalize();
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (entity.canBeMovedByCurrents()) {
            Vector3 flow = this.getFlowVector();
            vector.x += flow.x;
            vector.y += flow.y;
            vector.z += flow.z;
        }
    }

    public int getFlowDecayPerBlock() {
        return 1;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            this.checkForHarden();
            if (usesWaterLogging() && layer > 0) {
                Block layer0 = this.level.getBlock(this, 0);
                if (layer0.getId() == 0) {
                    this.level.setBlock(this, 1, Block.get(BlockID.AIR), false, false);
                    this.level.setBlock(this, 0, this, false, false);
                } else if (layer0.getWaterloggingLevel() <= 0 || layer0.getWaterloggingLevel() == 1 && getLiquidDepth() > 0) {
                    this.level.setBlock(this, 1, Block.get(BlockID.AIR), true, true);
                }
            }
            this.level.scheduleUpdate(this, this.tickRate());
            return 0;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int decay = this.getFlowDecay(this);
            int multiplier = this.getFlowDecayPerBlock();
            if (decay > 0) {
                int smallestFlowDecay = -100;
                this.adjacentSources = 0;
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), smallestFlowDecay);
                int newDecay = smallestFlowDecay + multiplier;
                if (newDecay >= 8 || smallestFlowDecay < 0) {
                    newDecay = -1;
                }
                int topFlowDecay = this.getFlowDecay(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z));
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay | 0x08;
                }
                if (this.adjacentSources >= 2 && this instanceof BlockWater) {
                    Block bottomBlock = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                    if (bottomBlock.isSolid()) {
                        newDecay = 0;
                    } else if (bottomBlock instanceof BlockWater && ((BlockWater)bottomBlock).getLiquidDepth() == 0) {
                        newDecay = 0;
                    } else {
                        bottomBlock = bottomBlock.getLevelBlockAtLayer(1);
                        if (bottomBlock instanceof BlockWater && ((BlockWater)bottomBlock).getLiquidDepth() == 0) {
                            newDecay = 0;
                        }
                    }
                }
                if (newDecay != decay) {
                    decay = newDecay;
                    boolean decayed = decay < 0;
                    Block to;
                    if (decayed) {
                        to = Block.get(BlockID.AIR);
                    } else {
                        to = getBlock(decay);
                    }
                    BlockFromToEvent event = new BlockFromToEvent(this, to);
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.level.setBlock(this, layer, event.getTo(), true, true);
                        if (!decayed) {
                            this.level.scheduleUpdate(this, this.tickRate());
                        }
                    }
                }
            }
            if (decay >= 0) {
                Block bottomBlock = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                this.flowIntoBlock(bottomBlock, decay | 0x08);
                if (decay == 0 || !(usesWaterLogging()? bottomBlock.canWaterloggingFlowInto(): bottomBlock.canBeFlowedInto())) {
                    int adjacentDecay;
                    if (decay >= 8) {
                        adjacentDecay = 1;
                    } else {
                        adjacentDecay = decay + multiplier;
                    }
                    if (adjacentDecay < 8) {
                        boolean[] flags = this.getOptimalFlowDirections();
                        if (flags[0]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if (flags[1]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if (flags[2]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), adjacentDecay);
                        }
                        if (flags[3]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), adjacentDecay);
                        }
                    }
                }
                this.checkForHarden();
            }
        }
        return 0;
    }

    protected void flowIntoBlock(Block block, int newFlowDecay) {
        if (this.canFlowInto(block) && !(block instanceof BlockLiquid)) {
            if (usesWaterLogging()) {
                Block layer1 = block.getLevelBlockAtLayer(1);
                if (layer1 instanceof BlockLiquid) {
                    return;
                }

                if (block.getWaterloggingLevel() > 1) {
                    block = layer1;
                }
            }

            LiquidFlowEvent event = new LiquidFlowEvent(block, this, newFlowDecay);
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (block.layer == 0 && block.getId() > 0) {
                    this.level.useBreakOn(block);
                }
                this.level.setBlock(block, block.layer, getBlock(newFlowDecay), true, true);
                this.level.scheduleUpdate(block, this.tickRate());
            }
        }
    }

    private int calculateFlowCost(int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite) {
        int cost = 1000;
        for (int j = 0; j < 4; ++j) {
            if (j == originOpposite || j == lastOpposite) {
                continue;
            }
            int x = blockX;
            int y = blockY;
            int z = blockZ;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else if (j == 3) {
                ++z;
            }
            long hash = Level.blockHash(x, y, z);
            if (!this.flowCostVisited.containsKey(hash)) {
                Block blockSide = this.level.getBlock(x, y, z);
                if (!this.canFlowInto(blockSide)) {
                    this.flowCostVisited.put(hash, BLOCKED);
                } else if (usesWaterLogging()?
                        this.level.getBlock(x, y - 1, z).canWaterloggingFlowInto() :
                        this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                    this.flowCostVisited.put(hash, CAN_FLOW_DOWN);
                } else {
                    this.flowCostVisited.put(hash, CAN_FLOW);
                }
            }
            byte status = this.flowCostVisited.get(hash);
            if (status == BLOCKED) {
                continue;
            } else if (status == CAN_FLOW_DOWN) {
                return accumulatedCost;
            }
            if (accumulatedCost >= maxCost) {
                continue;
            }
            int realCost = this.calculateFlowCost(x, y, z, accumulatedCost + 1, maxCost, originOpposite, j ^ 0x01);
            if (realCost < cost) {
                cost = realCost;
            }
        }
        return cost;
    }

    @Override
    public double getHardness() {
        return 100d;
    }

    @Override
    public double getResistance() {
        return 500;
    }

    private boolean[] getOptimalFlowDirections() {
        int[] flowCost = new int[]{
                1000,
                1000,
                1000,
                1000
        };
        int maxCost = 4 / this.getFlowDecayPerBlock();
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else {
                ++z;
            }
            Block block = this.level.getBlock(x, y, z);
            if (!this.canFlowInto(block)) {
                this.flowCostVisited.put(Level.blockHash(x, y, z), BLOCKED);
            } else if (usesWaterLogging()?
                    this.level.getBlock(x, y - 1, z).canWaterloggingFlowInto():
                    this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                this.flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW_DOWN);
                flowCost[j] = maxCost = 0;
            } else if (maxCost > 0) {
                this.flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW);
                flowCost[j] = this.calculateFlowCost(x, y, z, 1, maxCost, j ^ 0x01, j ^ 0x01);
                maxCost = Math.min(maxCost, flowCost[j]);
            }
        }
        this.flowCostVisited.clear();
        double minCost = Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            double d = flowCost[i];
            if (d < minCost) {
                minCost = d;
            }
        }
        boolean[] isOptimalFlowDirection = new boolean[4];
        for (int i = 0; i < 4; ++i) {
            isOptimalFlowDirection[i] = (flowCost[i] == minCost);
        }
        return isOptimalFlowDirection;
    }

    private int getSmallestFlowDecay(Block block, int decay) {
        int blockDecay = this.getFlowDecay(block);
        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay == 0) {
            ++this.adjacentSources;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }
        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }

    protected void checkForHarden() {
    }

    protected void triggerLavaMixEffects(Vector3 pos) {
        this.getLevel().addSound(pos.add(0.5, 0.5, 0.5), Sound.RANDOM_FIZZ, 1, 2.6F + (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            this.getLevel().addParticle(new SmokeParticle(pos.add(Math.random(), 1.2, Math.random())));
        }
    }

    public abstract BlockLiquid getBlock(int meta);

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    protected boolean liquidCollide(Block cause, Block result) {
        BlockFromToEvent event = new BlockFromToEvent(this, result);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.level.setBlock(this, event.getTo(), true, true);
        this.level.setBlock(this, 1, Block.get(BlockID.AIR), true, true);
        this.getLevel().addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_FIZZ);
        return true;
    }

    protected boolean canFlowInto(Block block) {
        if (usesWaterLogging()) {
            if (block.canWaterloggingFlowInto()) {
                Block blockLayer1 = block.getLevelBlockAtLayer(1);
                return !(block instanceof BlockLiquid && ((BlockLiquid)block).getLiquidDepth() == 0) && !(blockLayer1 instanceof BlockLiquid && ((BlockLiquid)blockLayer1).getLiquidDepth() == 0);
            }
        }
        return block.canBeFlowedInto() && !(block instanceof BlockLiquid && ((BlockLiquid)block).getLiquidDepth() == 0);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getLiquidDepth() {
        return getPropertyValue(LIQUID_DEPTH);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setLiquidDepth(int liquidDepth) {
        setPropertyValue(LIQUID_DEPTH, liquidDepth);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSource() {
        return getLiquidDepth() == 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getDepthOnTop() {
        int liquidDepth = getLiquidDepth();
        if (liquidDepth > 8) {
            liquidDepth -= 8;
        }
        return liquidDepth;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isFlowingDown() {
        return getLiquidDepth() >= 8;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSourceOrFlowingDown() {
        int liquidDepth = getLiquidDepth();
        return liquidDepth == 0 || liquidDepth == 8;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getLightFilter() {
        return 2;
    }
}
