package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHanging;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.ItemPainting;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPaintingPacket;
import cn.nukkit.network.protocol.DataPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityPainting extends EntityHanging {

    public static final int NETWORK_ID = 83;

    public final static Motive[] motives = Motive.values();
    private Motive motive;

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
        addPainting.title = this.namedTag.getString("Motive");
        return addPainting;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                if (damager instanceof Player && ((Player) damager).isSurvival() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                    this.level.dropItem(this, new ItemPainting());
                }
            }
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
        return Motive.BY_NAME.get(namedTag.getString("Motive"));
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
        FLAMING_SKULL("Flaming Skull", 4, 4);

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
}
