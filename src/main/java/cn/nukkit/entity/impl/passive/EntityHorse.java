package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Horse;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;

import static cn.nukkit.item.ItemIds.LEATHER;

/**
 * @author PikyCZ
 */
public class EntityHorse extends Animal implements Horse {

    public EntityHorse(EntityType<Horse> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.6982f;
        }
        return 1.3965f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.8f;
        }
        return 1.6f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(15);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(LEATHER)};
    }
}
