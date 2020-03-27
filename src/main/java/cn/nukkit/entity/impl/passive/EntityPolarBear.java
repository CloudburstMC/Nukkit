package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.PolarBear;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;

import static cn.nukkit.item.ItemIds.FISH;
import static cn.nukkit.item.ItemIds.SALMON;

/**
 * @author PikyCZ
 */
public class EntityPolarBear extends Animal implements PolarBear {

    public EntityPolarBear(EntityType<PolarBear> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(30);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(FISH), Item.get(SALMON)};
    }
}
