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

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityPainting extends EntityHanging {

    public static final int NETWORK_ID = 83;

    public final static Motive[] motives = new Motive[]{
            new Motive("Kebab", 1, 1),
            new Motive("Aztec", 1, 1),
            new Motive("Alban", 1, 1),
            new Motive("Aztec2", 1, 1),
            new Motive("Bomb", 1, 1),
            new Motive("Plant", 1, 1),
            new Motive("Wasteland", 1, 1),
            new Motive("Wanderer", 1, 2),
            new Motive("Graham", 1, 2),
            new Motive("Pool", 2, 1),
            new Motive("Courbet", 2, 1),
            new Motive("Sunset", 2, 1),
            new Motive("Sea", 2, 1),
            new Motive("Creebet", 2, 1),
            new Motive("Match", 2, 2),
            new Motive("Bust", 2, 2),
            new Motive("Stage", 2, 2),
            new Motive("Void", 2, 2),
            new Motive("SkullAndRoses", 2, 2),
            new Motive("Wither", 2, 2),
            new Motive("Fighters", 4, 2),
            new Motive("Skeleton", 4, 3),
            new Motive("DonkeyKong", 4, 3),
            new Motive("Pointer", 4, 4),
            new Motive("Pigscene", 4, 4),
            new Motive("Flaming Skull", 4, 4)
    };
    private Motive motive;

    public EntityPainting(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public static Motive getMotive(String name) {
        for (Motive motive : motives) {
            if (motive.title.equals(name)) {
                return motive;
            }
        }

        return motives[0];
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
        addPainting.x = (int) this.x;
        addPainting.y = (int) this.y;
        addPainting.z = (int) this.z;
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

    public static class Motive {
        public final String title;
        public final int width;
        public final int height;

        protected Motive(String title, int width, int height) {
            this.title = title;
            this.width = width;
            this.height = height;
        }
    }
}
