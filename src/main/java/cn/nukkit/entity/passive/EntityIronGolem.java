package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.mob.EntityWalkingMob;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EntityIronGolem extends EntityWalkingMob {

    public static final int NETWORK_ID = 20;

    public EntityIronGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(100);
        super.initEntity();
        this.noFallDamage = true;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        for (int i = 0; i < Utils.rand(3, 5); i++) {
            drops.add(Item.get(Item.IRON_INGOT, 0, 1));
        }

        for (int i = 0; i < Utils.rand(0, 2); i++) {
            drops.add(Item.get(Item.POPPY, 0, 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Iron Golem";
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        this.sendHealth();
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);

        this.sendHealth();
    }

    private void sendHealth() {
        if (this.isAlive()) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            int max = this.getMaxHealth();
            pk.entries = new Attribute[]{Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(max).setValue(this.health < max ? this.health : max)};
            pk.entityId = this.id;
            Server.broadcastPacket(this.getViewers().values(), pk);
        }
    }
}
