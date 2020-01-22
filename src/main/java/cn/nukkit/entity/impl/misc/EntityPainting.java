package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.HangingEntity;
import cn.nukkit.entity.misc.Painting;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPaintingPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityPainting extends HangingEntity implements Painting {

    public final static Motive[] motives = Motive.values();
    private Motive motive;

    public EntityPainting(EntityType<Painting> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    public static Motive getMotive(String name) {
        return Motive.from(name, Motive.KEBAB);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.motive = getMotive(this.namedTag.getString("Motive"));
    }

    @Override
    public DataPacket createAddEntityPacket() {
        AddPaintingPacket addPainting = new AddPaintingPacket();
        addPainting.entityUniqueId = this.getUniqueId();
        addPainting.entityRuntimeId = this.getUniqueId();
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
                if (damager instanceof Player && ((Player) damager).isSurvival() && this.level.getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
                    this.level.dropItem(this, Item.get(ItemIds.PAINTING));
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

    public Motive getMotive() {
        return Motive.from(namedTag.getString("Motive"));
    }
}
