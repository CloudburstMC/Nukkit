package cn.nukkit.level;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.event.block.BlockExplodeEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.math.*;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongArraySet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class Explosion {

    private final int rays = 16; //Rays
    private final Level level;
    private final Position source;
    private final double size;

    private double fireChance;
    private Set<Block> affectedBlocks;
    private Set<Block> fireIgnitions;
    private final double stepLen = 0.3d;

    private final Object what;
    private boolean doesDamage = true;

    public Explosion(Position center, double size, Entity what) {
        this(center, size, (Object) what);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Explosion(Position center, double size, Block what) {
        this(center, size, (Object) what);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Explosion(Position center, double size, Object what) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setFireChance(double fireChance) {
        this.fireChance = fireChance;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double getFireChance() {
        return fireChance;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isIncendiary() {
        return fireChance > 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setIncendiary(boolean incendiary) {
        if (!incendiary) {
            fireChance = 0;
        } else if (fireChance <= 0) {
            fireChance = 1.0/3.0;
        }
    }

    /**
     * @return bool
     * @deprecated
     */
    public boolean explode() {
        if (explodeA()) {
            return explodeB();
        }
        return false;
    }

    /**
     * @return bool
     */
    public boolean explodeA() {
        if (what instanceof EntityExplosive) {
            Entity entity = (Entity) what;
            int block = level.getBlockIdAt(entity.getFloorX(), entity.getFloorY(), entity.getFloorZ());
            if (block == BlockID.WATER || block == BlockID.STILL_WATER
                    || (block = level.getBlockIdAt(entity.getFloorX(), entity.getFloorY(), entity.getFloorZ(), 1)) == BlockID.WATER
                    || block == BlockID.STILL_WATER
            ) {
                this.doesDamage = false;
                return true;
            }
        }

        if (this.size < 0.1) {
            return false;
        }
        
        if (affectedBlocks == null) {
            affectedBlocks = new LinkedHashSet<>();
        }
        
        boolean incendiary = fireChance > 0;
        if (incendiary && fireIgnitions == null) {
            fireIgnitions = new LinkedHashSet<>();
        }
        
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Vector3 vector = new Vector3(0, 0, 0);
        Vector3 vBlock = new Vector3(0, 0, 0);

        int mRays = this.rays - 1;
        for (int i = 0; i < this.rays; ++i) {
            for (int j = 0; j < this.rays; ++j) {
                for (int k = 0; k < this.rays; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * this.stepLen, (vector.y / len) * this.stepLen, (vector.z / len) * this.stepLen);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * (random.nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= this.stepLen * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (vBlock.y < 0 || vBlock.y > 255) {
                                break;
                            }
                            Block block = this.level.getBlock(vBlock);

                            if (block.getId() != 0) {
                                Block layer1 = block.getLevelBlockAtLayer(1);
                                double resistance = Math.max(block.getResistance(), layer1.getResistance());
                                blastForce -= (resistance / 5 + 0.3d) * this.stepLen;
                                if (blastForce > 0) {
                                    if (this.affectedBlocks.add(block)) {
                                        if (incendiary && random.nextDouble() <= fireChance) {
                                            this.fireIgnitions.add(block);
                                        }
                                        if (layer1.getId() != BlockID.AIR) {
                                            this.affectedBlocks.add(layer1);
                                        }
                                    }
                                }
                            }
                            pointerX += vector.x;
                            pointerY += vector.y;
                            pointerZ += vector.z;
                        }
                    }
                }
            }
        }

        return true;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    public boolean explodeB() {

        LongArraySet updateBlocks = new LongArraySet();
        List<Vector3> send = new ArrayList<>();

        Vector3 source = (new Vector3(this.source.x, this.source.y, this.source.z)).floor();
        double yield = (1d / this.size) * 100d;


        if (this.what instanceof Entity) {
            List<Block> affectedBlocksList = new ArrayList<>(this.affectedBlocks);
            EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, affectedBlocksList, yield);
            ev.setIgnitions(fireIgnitions == null? new LinkedHashSet<>(0) : fireIgnitions);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                affectedBlocks.clear();
                affectedBlocks.addAll(ev.getBlockList());
                fireIgnitions = ev.getIgnitions();
            }
        } else if (this.what instanceof Block) {
            BlockExplodeEvent ev = new BlockExplodeEvent((Block) this.what, this.source, this.affectedBlocks, 
                    fireIgnitions == null? new LinkedHashSet<>(0) : fireIgnitions, yield, this.fireChance);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                affectedBlocks = ev.getAffectedBlocks();
                fireIgnitions = ev.getIgnitions();
            }
        }

        double explosionSize = this.size * 2d;
        double minX = NukkitMath.floorDouble(this.source.x - explosionSize - 1);
        double maxX = NukkitMath.ceilDouble(this.source.x + explosionSize + 1);
        double minY = NukkitMath.floorDouble(this.source.y - explosionSize - 1);
        double maxY = NukkitMath.ceilDouble(this.source.y + explosionSize + 1);
        double minZ = NukkitMath.floorDouble(this.source.z - explosionSize - 1);
        double maxZ = NukkitMath.ceilDouble(this.source.z + explosionSize + 1);

        AxisAlignedBB explosionBB = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof Entity ? (Entity) this.what : null);
        for (Entity entity : list) {
            double distance = entity.distance(this.source) / explosionSize;

            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                int exposure = 1;
                double impact = (1 - distance) * exposure;

                int damage = this.doesDamage ? (int) (((impact * impact + impact) / 2) * 8 * explosionSize + 1) : 0;

                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage));
                }

                if (!(entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
                    entity.setMotion(motion.multiply(impact));
                }
            }
        }

        ItemBlock air = new ItemBlock(Block.get(BlockID.AIR));
        BlockEntity container;

        for (Block block : this.affectedBlocks) {
            if (block.getId() == BlockID.TNT) {
                ((BlockTNT) block).prime(new NukkitRandom().nextRange(10, 30), this.what instanceof Entity ? (Entity) this.what : null);
            } else if ((container = block.getLevel().getBlockEntity(block)) instanceof InventoryHolder) {
                if (container instanceof BlockEntityShulkerBox) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), block.toItem());
                    ((InventoryHolder) container).getInventory().clearAll();
                } else {
                    for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                        this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (Math.random() * 100 < yield) {
                for (Item drop : block.getDrops(air)) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                }
            }

            this.level.setBlockAtLayer((int) block.x, (int) block.y, (int) block.z, block.layer, BlockID.AIR);

            if (block.layer != 0) {
                continue;
            }

            Vector3 pos = new Vector3(block.x, block.y, block.z);

            for (BlockFace side : BlockFace.values()) {
                Vector3 sideBlock = pos.getSide(side);
                long index = Hash.hashBlock((int) sideBlock.x, (int) sideBlock.y, (int) sideBlock.z);
                if (!this.affectedBlocks.contains(sideBlock) && !updateBlocks.contains(index)) {
                    BlockUpdateEvent ev = new BlockUpdateEvent(this.level.getBlock(sideBlock));
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }
                    Block layer1 = this.level.getBlock(sideBlock, 1);
                    if (layer1.getId() != BlockID.AIR) {
                        ev = new BlockUpdateEvent(layer1);
                        this.level.getServer().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                        }
                    }
                    updateBlocks.add(index);
                }   
            }
            send.add(new Vector3(block.x - source.x, block.y - source.y, block.z - source.z));
        }

        for (Vector3 remainingPos : fireIgnitions) {
            Block toIgnite = level.getBlock(remainingPos);
            if (toIgnite.getId() == BlockID.AIR && toIgnite.down().isSolid(BlockFace.UP)) {
                level.setBlock(toIgnite, Block.get(BlockID.FIRE));
            }
        }

        this.level.addParticle(new HugeExplodeSeedParticle(this.source));
        this.level.addSound(source, Sound.RANDOM_EXPLODE);

        return true;
    }

}
