package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.math.*;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Explosion {

    private final int rays = 16; //Rays

    private final Level level;

    private final Position source;

    private final double size;

    private final double stepLen = 0.3d;

    private final Object what;

    private List<Block> affectedBlocks = new ArrayList<>();

    public Explosion(final Position center, final double size, final Entity what) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    /**
     * @return bool
     * @deprecated
     */
    public boolean explode() {
        if (this.explodeA()) {
            return this.explodeB();
        }
        return false;
    }

    /**
     * @return bool
     */
    public boolean explodeA() {
        if (this.size < 0.1) {
            return false;
        }

        final Vector3 vector = new Vector3(0, 0, 0);
        final Vector3 vBlock = new Vector3(0, 0, 0);

        final int mRays = this.rays - 1;
        for (int i = 0; i < this.rays; ++i) {
            for (int j = 0; j < this.rays; ++j) {
                for (int k = 0; k < this.rays; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        final double len = vector.length();
                        vector.setComponents(vector.x / len * this.stepLen, vector.y / len * this.stepLen, vector.z / len * this.stepLen);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * ThreadLocalRandom.current().nextInt(700, 1301) / 1000d; blastForce > 0; blastForce -= this.stepLen * 0.75d) {
                            final int x = (int) pointerX;
                            final int y = (int) pointerY;
                            final int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (vBlock.y < 0 || vBlock.y > 255) {
                                break;
                            }
                            final Block block = this.level.getBlock(vBlock);

                            if (block.getId() != 0) {
                                blastForce -= (block.getResistance() / 5 + 0.3d) * this.stepLen;
                                if (blastForce > 0) {
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

        final LongArraySet updateBlocks = new LongArraySet();
        final List<Vector3> send = new ArrayList<>();

        final Vector3 source = new Vector3(this.source.x, this.source.y, this.source.z).floor();
        double yield = 1d / this.size * 100d;

        if (this.what instanceof Entity) {
            final EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, yield);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                this.affectedBlocks = ev.getBlockList();
            }
        }

        final double explosionSize = this.size * 2d;
        final double minX = NukkitMath.floorDouble(this.source.x - explosionSize - 1);
        final double maxX = NukkitMath.ceilDouble(this.source.x + explosionSize + 1);
        final double minY = NukkitMath.floorDouble(this.source.y - explosionSize - 1);
        final double maxY = NukkitMath.ceilDouble(this.source.y + explosionSize + 1);
        final double minZ = NukkitMath.floorDouble(this.source.z - explosionSize - 1);
        final double maxZ = NukkitMath.ceilDouble(this.source.z + explosionSize + 1);

        final AxisAlignedBB explosionBB = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

        final Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof Entity ? (Entity) this.what : null);
        for (final Entity entity : list) {
            final double distance = entity.distance(this.source) / explosionSize;

            if (distance <= 1) {
                final Vector3 motion = entity.subtract(this.source).normalize();
                final int exposure = 1;
                final double impact = (1 - distance) * exposure;
                final int damage = (int) (((impact * impact + impact) / 2) * 8 * explosionSize + 1);

                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, damage));
                }

                if (!(entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
                    entity.setMotion(motion.multiply(impact));
                }
            }
        }

        final ItemBlock air = new ItemBlock(Block.get(BlockID.AIR));
        BlockEntity container;

        //Iterator iter = this.affectedBlocks.entrySet().iterator();
        for (final Block block : this.affectedBlocks) {
            //Block block = (Block) ((HashMap.Entry) iter.next()).getValue();
            if (block.getId() == BlockID.TNT) {
                ((BlockTNT) block).prime(new NukkitRandom().nextRange(10, 30), this.what instanceof Entity ? (Entity) this.what : null);
            } else if ((container = block.getLevel().getBlockEntity(block)) instanceof InventoryHolder) {
                if (container instanceof BlockEntityShulkerBox) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), block.toItem());
                    ((InventoryHolder) container).getInventory().clearAll();
                } else {
                    for (final Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                        this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (Math.random() * 100 < yield) {
                for (final Item drop : block.getDrops(air)) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                }
            }

            this.level.setBlockAt((int) block.x, (int) block.y, (int) block.z, BlockID.AIR);

            final Vector3 pos = new Vector3(block.x, block.y, block.z);

            for (final BlockFace side : BlockFace.values()) {
                final Vector3 sideBlock = pos.getSide(side);
                final long index = Hash.hashBlock((int) sideBlock.x, (int) sideBlock.y, (int) sideBlock.z);
                if (!this.affectedBlocks.contains(sideBlock) && !updateBlocks.contains(index)) {
                    final BlockUpdateEvent ev = new BlockUpdateEvent(this.level.getBlock(sideBlock));
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }
                    updateBlocks.add(index);
                }
            }
            send.add(new Vector3(block.x - source.x, block.y - source.y, block.z - source.z));
        }

        this.level.addParticle(new HugeExplodeSeedParticle(this.source));
        this.level.addLevelSoundEvent(source, LevelSoundEventPacket.SOUND_EXPLODE);

        return true;
    }

}
