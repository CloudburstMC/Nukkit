package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Mule;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityMule extends Animal implements Mule {

    public EntityMule(EntityType<Mule> type, Location location) {
        super(type, location);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.LEATHER)};
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
}
