package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHanging;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPaintingPacket;
import cn.nukkit.network.protocol.DataPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EntityPainting extends EntityHanging {

    public static final int NETWORK_ID = 83;

    public final static Motive[] motives = Motive.values();

    private Motive motive;
    private SimpleAxisAlignedBB cachedBoundingBox;

    public EntityPainting(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public static Motive getMotive(String name) {
        return Motive.BY_NAME.getOrDefault(name, Motive.KEBAB);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.motive = getMotive(this.namedTag.getString("Motive"));
    }

    @Override
    public DataPacket createAddEntityPacket() {
        AddPaintingPacket addPainting = new AddPaintingPacket();
        addPainting.entityUniqueId = this.getId();
        addPainting.entityRuntimeId = this.getId();
        addPainting.x = (float) this.x;
        addPainting.y = (float) this.y;
        addPainting.z = (float) this.z;
        addPainting.direction = this.getDirection().getHorizontalIndex();
        addPainting.title = this.motive.title;
        return addPainting;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                if (damager instanceof Player && ((Player) damager).isSurvival()) {
                    this.dropItem();
                }
            }
            this.level.addParticle(new DestroyBlockParticle(this, Block.get(Block.WOODEN_PLANKS)));
            this.close();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putString("Motive", this.motive.title);
    }

    public Motive getArt() {
        return getMotive();
    }

    public Motive getMotive() {
        return this.motive;
    }

    public enum Motive {
        KEBAB("Kebab", 1, 1),
        AZTEC("Aztec", 1, 1),
        ALBAN("Alban", 1, 1),
        AZTEC2("Aztec2", 1, 1),
        BOMB("Bomb", 1, 1),
        PLANT("Plant", 1, 1),
        WASTELAND("Wasteland", 1, 1),
        WANDERER("Wanderer", 1, 2),
        GRAHAM("Graham", 1, 2),
        POOL("Pool", 2, 1),
        COURBET("Courbet", 2, 1),
        SUNSET("Sunset", 2, 1),
        SEA("Sea", 2, 1),
        CREEBET("Creebet", 2, 1),
        MATCH("Match", 2, 2),
        BUST("Bust", 2, 2),
        STAGE("Stage", 2, 2),
        VOID("Void", 2, 2),
        SKULL_AND_ROSES("SkullAndRoses", 2, 2),
        WITHER("Wither", 2, 2),
        FIGHTERS("Fighters", 4, 2),
        SKELETON("Skeleton", 4, 3),
        DONKEY_KONG("DonkeyKong", 4, 3),
        POINTER("Pointer", 4, 4),
        PIG_SCENE("Pigscene", 4, 4),
        BURNING_SKULL("BurningSkull", 4, 4),
        MEDITATIVE("meditative", 1, 1),
        PRAIRIE_RIDE("prairie_ride", 1, 2),
        BAROQUE("baroque", 2, 2),
        HUMBLE("humble", 2, 2),
        UNPACKED("unpacked", 4, 4),
        BOUQUET("bouquet", 3, 3),
        CAVEBIRD("cavebird", 3, 3),
        COTAN("cotan", 3, 3),
        ENDBOSS("endboss", 3, 3),
        FERN("fern", 3, 3),
        OWLEMONS("owlemons", 3, 3),
        SUNFLOWERS("sunflowers", 3, 3),
        TIDES("tides", 3, 3),
        BACKYARD("backyard", 3, 4),
        POND("pond", 3, 4),
        CHANGING("changing", 4, 2),
        FINDING("finding", 4, 2),
        LOWMIST("lowmist", 4, 2),
        PASSAGE("passage", 4, 2),
        ORB("orb", 4, 4);

        public final String title;
        public final int width;
        public final int height;

        private static final Map<String, Motive> BY_NAME = new HashMap<>();

        static {
            for (Motive motive : values()) {
                BY_NAME.put(motive.title, motive);
            }
        }

        Motive(String title, int width, int height) {
            this.title = title;
            this.width = width;
            this.height = height;
        }
    }

    @Override
    protected boolean isSurfaceValid() {
        if (this.cachedBoundingBox == null) {
            this.cachedBoundingBox = new SimpleAxisAlignedBB(this.x - 0.1, this.y, this.z - 0.1, this.x + 0.1, this.y + 0.1, this.z + 0.1);
        }
        return this.level.hasCollisionBlocks(this, this.cachedBoundingBox);
    }

    @Override
    protected void dropItem() {
        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.level.dropItem(this, Item.get(Item.PAINTING));
        }
    }

    @Override
    public boolean ignoredAsSaveReason() {
        return true;
    }
}
