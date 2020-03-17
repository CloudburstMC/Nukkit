package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.Creeper;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.entity.CreeperPowerEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

import static com.nukkitx.protocol.bedrock.data.EntityFlag.POWERED;

/**
 * @author Box.
 */
public class EntityCreeper extends EntityHostile implements Creeper {

    public EntityCreeper(EntityType<Creeper> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForBoolean("powered", this::setPowered);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.booleanTag("powered", this.isPowered());
    }

    public boolean isPowered() {
        return this.data.getFlag(POWERED);
    }

    public void setPowered(LightningBolt lightningBolt) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, lightningBolt, CreeperPowerEvent.PowerCause.LIGHTNING);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.data.setFlag(POWERED, true);
        }
    }

    public void setPowered(boolean powered) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, powered ? CreeperPowerEvent.PowerCause.SET_ON : CreeperPowerEvent.PowerCause.SET_OFF);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.data.setFlag(POWERED, powered);
        }
    }

    @Override
    public void onStruckByLightning(LightningBolt lightningBolt) {
        this.setPowered(true);
    }

    @Override
    public String getName() {
        return "Creeper";
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(ItemIds.GUNPOWDER, ThreadLocalRandom.current().nextInt(2) + 1)};
        }
        return new Item[0];
    }
}
