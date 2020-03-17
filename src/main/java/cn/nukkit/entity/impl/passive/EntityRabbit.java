package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Rabbit;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntityRabbit extends Animal implements Rabbit {

    public EntityRabbit(EntityType<Rabbit> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.2f;
        }
        return 0.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        }
        return 0.5f;
    }

    @Override
    public String getName() {
        return "Rabbit";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.RABBIT), Item.get(ItemIds.RABBIT_HIDE), Item.get(ItemIds.RABBIT_FOOT)};
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(10);
    }
}
