package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.event.block.BlockExplodeEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.math.*;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.longs.LongArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class Explosion {

    private static final int rays = 16;
    private final Level level;
    private final Position source;
    private final double size;

    private List<Block> affectedBlocks = new ArrayList<>();
    private static final double stepLen = 0.3d;

    private final Object what;
    private boolean doesDamage = true;
    private double minHeight = Integer.MIN_VALUE;
    private double fireSpawnChance;

    public Explosion(Position center, double size, Entity what) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    public Explosion(Position center, double size, Object what) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    /**
     * @return bool
     */
    @Deprecated
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
        if (what instanceof EntityExplosive && ((Entity) what).isInsideOfWater()) {
            this.doesDamage = false;
            return true;
        }
        if (this.size < 0.1) return false;

        Vector3 vector = new Vector3(0, 0, 0);
        Vector3 vBlock = new Vector3(0, 0, 0);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int mRays = 15;
        for (int i = 0; i < rays; ++i) {
            for (int j = 0; j < rays; ++j) {
                for (int k = 0; k < rays; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * stepLen, (vector.y / len) * stepLen, (vector.z / len) * stepLen);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * (random.nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= stepLen * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (vBlock.y < this.level.getMinBlockY() || vBlock.y > this.level.getMaxBlockY()) {
                                break;
                            }

                            Block block = this.level.getBlock(vBlock);
                            if (block.getId() != Block.AIR) {
                                blastForce -= (block.getResistance() / 5 + 0.3d) * stepLen;
                                if (blastForce > 0 && block.y >= this.minHeight) {
                                    if (!this.affectedBlocks.contains(block)) {
                                        this.affectedBlocks.add(block);
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

    public boolean explodeB() {
        double yield = (1d / this.size) * 100d;

        if (this.what instanceof Entity) {
            EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, yield);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                this.affectedBlocks = ev.getBlockList();
            }
        } else if (this.what instanceof Block) {
            BlockExplodeEvent ev = new BlockExplodeEvent((Block) this.what, this.source, this.affectedBlocks, yield);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                this.affectedBlocks = ev.getBlockList();
            }
        }

        LongArraySet updateBlocks = new LongArraySet();
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
                double exposure = this.getSeenPercent(this.source, entity);
                double impact = (1 - distance) * exposure;

                int damage = this.doesDamage ? Math.max((int) (((impact * impact + impact) / 2) * 8 * explosionSize + 1), 0) : 0;

                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage));
                }

                if (!(entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
                    if (entity instanceof Player) {
                        int netheritePieces = 0;
                        for (Item armor : ((Player) entity).getInventory().getArmorContents()) {
                            if (armor.getTier() == ItemArmor.TIER_NETHERITE) {
                                netheritePieces++;
                            }
                        }
                        if (netheritePieces > 0) {
                            impact *= 1 - 0.1 * netheritePieces;
                        }
                    }
                    entity.setMotion(entity.getMotion().add(motion.multiply(impact)));
                }
            }
        }

        Item air = Item.get(Item.AIR);
        BlockEntity container;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Block block : this.affectedBlocks) {
            if (block.getId() == Block.TNT) {
                ((BlockTNT) block).prime(Utils.rand(10, 30), this.what instanceof Entity ? (Entity) this.what : null);
            } else if (block.getId() == Block.BED_BLOCK && (block.getDamage() & 0x08) == 0x08) {
                this.level.setBlockAt((int) block.x, (int) block.y, (int) block.z, Block.AIR);
                continue; // We don't want drops from both bed parts
            } else {
                Item shulkerDrop = null;

                // 100% drop chance for container contents
                if ((container = this.level.getBlockEntity(block)) instanceof InventoryHolder && !container.closed) {
                    if (this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        Inventory inv = ((InventoryHolder) container).getInventory();
                        if (inv != null) {
                            inv.getViewers().clear();
                        }

                        if (container instanceof BlockEntityShulkerBox) {
                            shulkerDrop = block.toItem();
                        } else {
                            container.onBreak();
                        }
                    }

                    container.close();

                    if (shulkerDrop != null) {
                        this.level.dropItem(block.add(0.5, 0.5, 0.5), shulkerDrop);
                    }
                }

                if (shulkerDrop == null && (block.alwaysDropsOnExplosion() || random.nextDouble() * 100 < yield)) {
                    if (this.level.getBlockIdAt((int) block.x, (int) block.y, (int) block.z) == Block.AIR) {
                        continue; // Block broken by another explosion (end crystals)
                    }
                    for (Item drop : block.getDrops(air)) { // Might want to check if getDrops of any block expects block entity to still exist
                        this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                    }
                }
            }

            if (this.fireSpawnChance > 0 && random.nextDouble() < this.fireSpawnChance) {
                this.level.setBlockAt((int) block.x, (int) block.y, (int) block.z, BlockID.FIRE);
            } else {
                this.level.setBlockAt((int) block.x, (int) block.y, (int) block.z, BlockID.AIR);
            }

            Vector3 pos = new Vector3(block.x, block.y, block.z);

            sideBlocks:
            for (BlockFace side : BlockFace.values()) {
                Vector3 sideBlock = pos.getSide(side);
                long index = Hash.hashBlock((int) sideBlock.x, (int) sideBlock.y, (int) sideBlock.z);
                if (!updateBlocks.contains(index)) {
                    for (Block affected : this.affectedBlocks) {
                        if (affected.x == sideBlock.x && affected.y == sideBlock.y && affected.z == sideBlock.z) {
                            continue sideBlocks;
                        }
                    }
                    BlockUpdateEvent ev = new BlockUpdateEvent(this.level.getBlock(sideBlock));
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }
                    updateBlocks.add(index);
                }
            }
        }

        this.level.addParticle(new HugeExplodeSeedParticle(this.source));
        this.level.addLevelSoundEvent(source, LevelSoundEventPacket.SOUND_EXPLODE);
        return true;
    }

    public void explodeEntity() {
        if (this.what instanceof Entity) {
            EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, 0);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
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
            if (!(entity instanceof EntityLiving) || (entity instanceof EntityEnderDragon)) {
                continue;
            }

            double distance = entity.distance(this.source) / explosionSize;

            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                double exposure = this.getSeenPercent(this.source, entity);
                double impact = (1 - distance) * exposure;

                int damage = this.doesDamage ? Math.max((int) (((impact * impact + impact) / 2) * 5 * this.size + 1), 0) : 0; // * 8 * explosionSize

                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage));
                }

                if (entity instanceof Player) {
                    int netheritePieces = 0;
                    for (Item armor : ((Player) entity).getInventory().getArmorContents()) {
                        if (armor.getTier() == ItemArmor.TIER_NETHERITE) {
                            netheritePieces++;
                        }
                    }
                    if (netheritePieces > 0) {
                        impact *= 1 - 0.1 * netheritePieces;
                    }
                }
                entity.setMotion(entity.getMotion().add(motion.multiply(impact)));
            }
        }
    }

    private double getSeenPercent(Vector3 source, Entity entity) {
        AxisAlignedBB bb = entity.getBoundingBox();

        if (bb.isVectorInside(source)) {
            return 1;
        }

        double x = 1 / ((bb.getMaxX() - bb.getMinX()) * 2 + 1);
        double y = 1 / ((bb.getMaxY() - bb.getMinY()) * 2 + 1);
        double z = 1 / ((bb.getMaxZ() - bb.getMinZ()) * 2 + 1);

        double xOffset = (1 - Math.floor(1 / x) * x) / 2;
        double yOffset = (1 - Math.floor(1 / y) * y) / 2;
        double zOffset = (1 - Math.floor(1 / z) * z) / 2;

        int misses = 0;
        int total = 0;

        for (double i = 0; i <= 1; i += x) {
            for (double j = 0; j <= 1; j += y) {
                for (double k = 0; k <= 1; k += z) {
                    Vector3 target = new Vector3(
                            bb.getMinX() + i * (bb.getMaxX() - bb.getMinX()) + xOffset,
                            bb.getMinY() + j * (bb.getMaxY() - bb.getMinY()) + yOffset,
                            bb.getMinZ() + k * (bb.getMaxZ() - bb.getMinZ()) + zOffset
                    );

                    if (!this.raycastHit(source, target)) {
                        ++misses;
                    }

                    total++;
                }
            }
        }

        return total != 0 ? (double) misses / (double) total : 0;
    }

    private boolean raycastHit(Vector3 start, Vector3 end) {
        Vector3 current = new Vector3(start.x, start.y, start.z);
        Vector3 direction = end.subtract(start).normalize();

        double stepX = sign(direction.getX());
        double stepY = sign(direction.getY());
        double stepZ = sign(direction.getZ());

        double tMaxX = boundary(start.getX(), direction.getX());
        double tMaxY = boundary(start.getY(), direction.getY());
        double tMaxZ = boundary(start.getZ(), direction.getZ());

        double tDeltaX = direction.getX() == 0 ? 0 : stepX / direction.getX();
        double tDeltaY = direction.getY() == 0 ? 0 : stepY / direction.getY();
        double tDeltaZ = direction.getZ() == 0 ? 0 : stepZ / direction.getZ();

        double radius = start.distance(end);

        while (true) {
            Block block = level.getBlock(current);

            if (block.isSolid() && block.calculateIntercept(current, end) != null) {
                return true;
            }

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                if (tMaxX > radius) {
                    break;
                }

                current.x += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY < tMaxZ) {
                if (tMaxY > radius) {
                    break;
                }

                current.y += stepY;
                tMaxY += tDeltaY;
            } else {
                if (tMaxZ > radius) {
                    break;
                }

                current.z += stepZ;
                tMaxZ += tDeltaZ;
            }
        }

        return false;
    }

    private static double sign(double d) {
        if (d > 0) {
            return 1;
        }

        if (d < 0) {
            return -1;
        }

        return 0;
    }

    private static double boundary(double start, double distance) {
        if (distance == 0) {
            return Double.POSITIVE_INFINITY;
        }

        if (distance < 0) {
            start = -start;
            distance = -distance;

            if (Math.floor(start) == start) {
                return 0;
            }
        }

        return (1 - (start - Math.floor(start))) / distance;
    }

    /**
     * Set minimum height at which the explosion can break blocks
     * @param minHeight min y coordinate
     */
    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * Set chance for fire to be spawned
     * @param fireSpawnChance 0.0 - 1.0
     */
    public void setFireSpawnChance(double fireSpawnChance) {
        this.fireSpawnChance = fireSpawnChance;
    }
}
