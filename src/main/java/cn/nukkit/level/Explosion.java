package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.ExplodePacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Explosion {

    private int rays = 16; //Rays
    private Level level;
    private Position source;
    private double size;

    private List<Block> affectedBlocks = new ArrayList<>();
    private double stepLen = 0.3d;

    private Object what;

    public Explosion(Position center, double size, Entity what) {
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
        if (explodeA()) {
            return explodeB();
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

                        for (double blastForce = this.size * (ThreadLocalRandom.current().nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= this.stepLen * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (vBlock.y < 0 || vBlock.y > 127) {
                                break;
                            }
                            Block block = this.level.getBlock(vBlock);

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

        List<Vector3> updateBlocks = new ArrayList<>();
        List<Vector3> send = new ArrayList<>();

        Vector3 source = (new Vector3(this.source.x, this.source.y, this.source.z)).floor();
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
        }

        double explosionSize = this.size * 2d;
        double minX = NukkitMath.floorDouble(this.source.x - explosionSize - 1);
        double maxX = NukkitMath.ceilDouble(this.source.x + explosionSize + 1);
        double minY = NukkitMath.floorDouble(this.source.y - explosionSize - 1);
        double maxY = NukkitMath.ceilDouble(this.source.y + explosionSize + 1);
        double minZ = NukkitMath.floorDouble(this.source.z - explosionSize - 1);
        double maxZ = NukkitMath.ceilDouble(this.source.z + explosionSize + 1);

        AxisAlignedBB explosionBB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

        Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof Entity ? (Entity) this.what : null);
        for (Entity entity : list) {
            double distance = entity.distance(this.source) / explosionSize;

            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                int exposure = 1;
                double impact = (1 - distance) * exposure;
                int damage = (int) (((impact * impact + impact) / 2) * 8 * explosionSize + 1);

                if (this.what instanceof Entity) {
                    EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent((Entity) this.what, entity, EntityDamageEvent.CAUSE_ENTITY_EXPLOSION, damage);
                    entity.attack(ev);
                } else if (this.what instanceof Block) {
                    EntityDamageByBlockEvent ev = new EntityDamageByBlockEvent((Block) this.what, entity, EntityDamageEvent.CAUSE_BLOCK_EXPLOSION, damage);
                    entity.attack(ev);
                } else {
                    EntityDamageEvent ev = new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_BLOCK_EXPLOSION, damage);
                    entity.attack(ev);
                }

                entity.setMotion(motion.multiply(impact));
            }
        }

        ItemBlock air = new ItemBlock(new BlockAir());

        Iterator iter = this.affectedBlocks.iterator();
        while (iter.hasNext()) {
            Block block = (Block) iter.next();
            if (block.getId() == Block.TNT) {
                double mot = Math.random() * Math.PI * 2;
                EntityPrimedTNT tnt = new EntityPrimedTNT(this.level.getChunk((int) block.x >> 4, (int) block.z >> 4),
                        new CompoundTag()
                                .putList(new ListTag<DoubleTag>("Pos")
                                        .add(new DoubleTag("", block.x + 0.5))
                                        .add(new DoubleTag("", block.y))
                                        .add(new DoubleTag("", block.z + 0.5)))
                                .putList(new ListTag<DoubleTag>("Motion")
                                        .add(new DoubleTag("", -Math.sin(mot) * 0.02))
                                        .add(new DoubleTag("", 0.2))
                                        .add(new DoubleTag("", -Math.cos(mot) * 0.02)))
                                .putList(new ListTag<FloatTag>("Rotation")
                                        .add(new FloatTag("", 0))
                                        .add(new FloatTag("", 0)))
                                .put("Fuse", new ByteTag("", (byte) (10 + (Math.random() * 30) + 1))
                                ));
                tnt.spawnToAll();
            } else if (Math.random() * 100 < yield) {
                for (int[] drop : block.getDrops(air)) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), Item.get(drop[0], drop[1], drop[2]));
                }
            }

            this.level.setBlockIdAt((int) block.x, (int) block.y, (int) block.z, 0);

            for (int side = 0; side < 5; side++) {
                Block sideBlock = block.getSide(side);
                if (!this.affectedBlocks.contains(sideBlock) && !updateBlocks.contains(sideBlock)) {
                    BlockUpdateEvent ev = new BlockUpdateEvent(this.level.getBlock(sideBlock));
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }
                    updateBlocks.add(sideBlock);
                }
            }
            send.add(new Vector3(block.x - source.x, block.y - source.y, block.z - source.z));
        }

        ExplodePacket pk = new ExplodePacket();
        pk.x = (float) this.source.x;
        pk.y = (float) this.source.y;
        pk.z = (float) this.source.z;
        pk.radius = (float) this.size;
        pk.records = send.stream().toArray(Vector3[]::new);

        this.level.addChunkPacket((int) source.x >> 4, (int) source.z >> 4, pk);

        return true;
    }

}
