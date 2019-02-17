package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Colorable;
import com.nukkitx.api.entity.component.Shearable;
import com.nukkitx.api.entity.passive.Sheep;
import com.nukkitx.api.metadata.data.DyeColor;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.entity.component.ColorableComponent;
import com.nukkitx.server.entity.component.ShearableComponent;
import com.nukkitx.server.level.NukkitLevel;

public class SheepEntity extends LivingEntity implements Sheep {

    public SheepEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SHEEP, position, level, server, 10);

        registerComponent(Colorable.class, new ColorableComponent(DyeColor.WHITE));
        registerComponent(Shearable.class, new ShearableComponent());
    }
/*
    @Override
    public boolean onInteract(PlayerOld player, Item item) {
        if (item.getId() == Item.DYE) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());
            return true;
        }

        return item.getId() == Item.SHEARS && shear();
    }

    public boolean shear() {
        if (sheared) {
            return false;
        }

        this.sheared = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, true);

        this.level.drop(this, Item.get(Item.WOOL, getColor(), this.level.rand.nextInt(2) + 1));
        return true;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(Item.WOOL, getColor(), 1)};
        }
        return new Item[0];
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData();
        }

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }*/
}
