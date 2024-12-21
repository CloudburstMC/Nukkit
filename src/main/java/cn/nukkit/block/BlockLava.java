package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockLava extends BlockLiquid {

    public BlockLava() {
        this(0);
    }

    public BlockLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAVA;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Lava";
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.highestPosition -= (entity.highestPosition - entity.y) * 0.5;

        if (!entity.fireProof || !entity.isOnFire() || !(entity instanceof BaseEntity)) { // Improve performance
            if (!entity.fireProof || !entity.isOnFire()) {

                EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()
                        // Making sure the entity is actually alive and not invulnerable
                        && entity.isAlive()
                        && entity.noDamageTicks == 0) {
                    entity.setOnFire(ev.getDuration());
                }
            }

            if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
                entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
            }
        }

        super.onEntityCollide(entity);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public int onUpdate(int type) {
        int result = super.onUpdate(type);

        if (type == Level.BLOCK_UPDATE_RANDOM && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            int i = ThreadLocalRandom.current().nextInt(3);

            if (i > 0) {
                for (int k = 0; k < i; ++k) {
                    Vector3 v = this.add(ThreadLocalRandom.current().nextInt(3) - 1, 1, ThreadLocalRandom.current().nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.getId() == AIR) {
                        if (this.isSurroundingBlockFlammable(block)) {
                            BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA);
                            this.level.getServer().getPluginManager().callEvent(e);

                            if (!e.isCancelled()) {
                                Block fire = Block.get(BlockID.FIRE);
                                this.getLevel().setBlock(v, fire, true);
                                this.getLevel().scheduleUpdate(fire, fire.tickRate());
                                return Level.BLOCK_UPDATE_RANDOM;
                            }

                            return 0;
                        }
                    } else if (block.isSolid()) {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    Vector3 v = this.add(ThreadLocalRandom.current().nextInt(3) - 1, 0, ThreadLocalRandom.current().nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.up().getId() == AIR && block.getBurnChance() > 0) {
                        BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA);
                        this.level.getServer().getPluginManager().callEvent(e);

                        if (!e.isCancelled()) {
                            Block fire = Block.get(BlockID.FIRE);
                            this.getLevel().setBlock(v, fire, true);
                            this.getLevel().scheduleUpdate(fire, fire.tickRate());
                        }
                    }
                }
            }
        }

        return result;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if (block.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LAVA_BLOCK_COLOR;
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(LAVA, meta);
    }
    
    @Override
    public int tickRate() {
        if (this.level.getDimension() == Level.DIMENSION_NETHER) {
            return 10;
        }
        return 30;
    }

    @Override
    public int getFlowDecayPerBlock() {
        if (this.level.getDimension() == Level.DIMENSION_NETHER) {
            return 1;
        }
        return 2;
    }

    @Override
    protected void checkForHarden() { 
        Block colliding = null;
        for (int side = 1; side < 6; ++side) { //don't check downwards side
            Block blockSide = this.getSide(BlockFace.fromIndex(side));
            if (blockSide instanceof BlockWater || blockSide.getLevelBlock(BlockLayer.WATERLOGGED) instanceof BlockWater) {
                colliding = blockSide;
                break;
            }
            if (blockSide instanceof BlockBlueIce) {
                if (down() instanceof BlockSoulSoil) {
                    liquidCollide(this, Block.get(BlockID.BASALT));
                    return;
                }
            }
        }
        if (colliding != null) {
            if (this.getDamage() == 0) {
                this.liquidCollide(colliding, Block.get(OBSIDIAN));
            } else if (this.getDamage() <= 4) {
                this.liquidCollide(colliding, Block.get(COBBLESTONE));
            }
        }
    }

    @Override
    protected void flowIntoBlock(Block block, int newFlowDecay) {
        if (block instanceof BlockWater) {
            ((BlockLiquid) block).liquidCollide(this, Block.get(STONE));
        } else {
            super.flowIntoBlock(block, newFlowDecay);
        }
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (!(entity instanceof EntityPrimedTNT)) {
            super.addVelocityToEntity(entity, vector);
        }
    }

    @Override
    protected boolean[] getOptimalFlowDirections() {
        int[] flowCost = {
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
                this.flowCostVisited.put(Level.blockHash(x, y, z, this.level.getDimensionData()), BLOCKED);
            } else if (this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                this.flowCostVisited.put(Level.blockHash(x, y, z, this.level.getDimensionData()), CAN_FLOW_DOWN);
                flowCost[j] = maxCost = 0;
            } else if (maxCost > 0) {
                this.flowCostVisited.put(Level.blockHash(x, y, z, this.level.getDimensionData()), CAN_FLOW);
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
}
