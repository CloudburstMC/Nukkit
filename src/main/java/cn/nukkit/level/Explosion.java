package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.PrimedTNT;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.ExplodePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Explosion {

    private int rays = 16; //Rays
    private Level level;
    private Position source;
    private float size;

    private HashMap<String, Block> affectedBlocks = new HashMap<String, Block>();
    private float stepLen = 0.3F;

    private Object what;

    public Explosion(Position center, float size, Entity what) {
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
        if(this.size < 0.1) {
            return false;
        }

        Vector3 vector = new Vector3(0, 0, 0);
        Vector3 vBlock = new Vector3(0, 0, 0);

        int mRays = this.rays - 1;
        for(int i = 0; i < this.rays; ++i) {
            for(int j = 0; j < this.rays; ++j) {
                for(int k = 0; k < this.rays; ++k) {
                    if(i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents(i / mRays * 2 - 1, j / mRays * 2 -1, k / mRays * 2 - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * this.stepLen, (vector.y / len) * this.stepLen, (vector.z / len) * this.stepLen);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for(float blastForce = this.size * ((700 + (int)(Math.random() * 400) + 1) / 1000); blastForce > 0; blastForce -= this.stepLen * 0.75) {
                            int x = (int)pointerX;
                            int y = (int)pointerY;
                            int z = (int)pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if(vBlock.y < 0 || vBlock.y > 127) {
                                break;
                            }
                            Block block = this.level.getBlock(vBlock);

                            if(block.getId() != 0) {
                                blastForce -= (block.getResistance() / 5 + 0.3) * this.stepLen;
                                if(blastForce > 0) {
                                    String index = Level.blockHash((int)block.x, (int)block.y, (int)block.z);
                                    if(!this.affectedBlocks.containsKey(index)) {
                                        this.affectedBlocks.put(index, block);
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

        HashMap<String, Boolean> updateBlocks = new HashMap<String, Boolean>();
        List<Vector3> send = new ArrayList<Vector3>();

        Vector3 source = (new Vector3(this.source.x, this.source.y, this.source.z)).floor();
        float yield = (1 / this.size) * 100;

        if(this.what instanceof Entity) {
            EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, yield);
            this.level.getServer().getPluginManager().callEvent(ev);
            if(ev.isCancelled()) {
                return false;
            }
            else {
                yield = ev.getYield();
                this.affectedBlocks = ev.getBlockList();
            }
        }

        float explonsionSize = this.size * 2;
        float minX = NukkitMath.floorFloat((float) (this.source.x - explonsionSize - 1));
        float maxX = NukkitMath.ceilFloat((float) (this.source.x + explonsionSize + 1));
        float minY = NukkitMath.floorFloat((float) (this.source.y - explonsionSize - 1));
        float maxY = NukkitMath.ceilFloat((float) (this.source.y + explonsionSize + 1));
        float minZ = NukkitMath.floorFloat((float) (this.source.z - explonsionSize - 1));
        float maxZ = NukkitMath.ceilFloat((float) (this.source.z + explonsionSize + 1));

        AxisAlignedBB explosionBB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

        Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof  Entity ? (Entity) this.what : null).clone();
        for(Entity entity : list) {
            double distance = entity.distance(this.source) / explonsionSize;

            if(distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                int exposure = 1;
                double impact = (1 - distance) * exposure;
                int damage = (int) (((impact * impact + impact) / 2) * 8 * explonsionSize + 1);

                if(this.what instanceof  Entity) {
                    EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent((Entity) this.what, entity, EntityDamageEvent.CAUSE_ENTITY_EXPLOSION, damage);
                    entity.attack(ev.getFinalDamage(), ev);
                }
                else if(this.what instanceof Block) {
                    EntityDamageByBlockEvent ev = new EntityDamageByBlockEvent((Block) this.what, entity, EntityDamageEvent.CAUSE_BLOCK_EXPLOSION, damage);
                    entity.attack(ev.getFinalDamage(), ev);
                }
                else {
                    EntityDamageEvent ev = new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_BLOCK_EXPLOSION, damage);
                    entity.attack(ev.getFinalDamage(), ev);
                }

                entity.setMotion(motion.multiply(impact));
            }
        }

        Item air = Item.get(Item.AIR);

        Iterator iter = this.affectedBlocks.entrySet().iterator();
        while(iter.hasNext()) {
            Block block = (Block) ((HashMap.Entry) iter.next()).getValue();
            if(block.getId() == Block.TNT){
                float mot = (float) (Math.random() * Math.PI * 2);
                PrimedTNT tnt = (PrimedTNT) Entity.createEntity("PrimedTNT", this.level.getChunk((int) block.x >> 4, (int) block.z >> 4),
                        new CompoundTag()
                                .putList(new ListTag<DoubleTag>("Pos")
                                        .add(new DoubleTag("", block.x + 0.5))
                                        .add(new DoubleTag("", block.y))
                                        .add(new DoubleTag("", block.z + 0.5)))
                                .putList(new ListTag<DoubleTag>("Motion")
                                        .add(new DoubleTag("", - Math.sin(mot) * 0.02))
                                        .add(new DoubleTag("", 0.2))
                                        .add(new DoubleTag("", - Math.cos(mot) * 0.02)))
                                .putList(new ListTag<FloatTag>("Rotation")
                                        .add(new FloatTag("", 0))
                                        .add(new FloatTag("", 0)))
                                .put("Fuse", new ByteTag("", (byte) (10 + (Math.random() * 30) + 1))
                        ));
                tnt.spawnToAll();
            }
            else if(Math.random() * 100 < yield) {
                /*for(int[][] drop : block.getDrops(block.add(0.5, 0.5, 0.5), Item.get())) {

                }*/
                //todo: Drop item
            }

            this.level.setBlockIdAt((int) block.x, (int) block.y, (int) block.z, 0);

            Vector3 pos = new Vector3(block.x, block.y, block.z);

            for(int side = 0; side < 5; side++) {
                Vector3 sideBlock = pos.getSide(side);
                String index = Level.blockHash((int) sideBlock.x, (int) sideBlock.y, (int) sideBlock.z);
                if(!this.affectedBlocks.containsKey(index) && !updateBlocks.containsKey(index)){
                    BlockUpdateEvent ev = new BlockUpdateEvent(this.level.getBlock(sideBlock));
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if(!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }
                    updateBlocks.put(index, true);
                }
            }
            send.add(new Vector3(block.x - source.x, block.y - source.y, block.z - source.z));
        }

        ExplodePacket pk = new ExplodePacket();
        pk.x = (float) this.source.x;
        pk.y = (float) this.source.y;
        pk.z = (float) this.source.z;
        pk.radius = this.size;
        pk.records = send.stream().toArray(Vector3[]::new);

        this.level.addChunkPacket((int) source.x >> 4, (int) source.z >> 4, pk);

        return true;
    }

}
