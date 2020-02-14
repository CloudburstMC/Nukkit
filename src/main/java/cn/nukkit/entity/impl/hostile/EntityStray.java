package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.Stray;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityStray extends EntityHostile implements Stray, Smiteable {

    public EntityStray(EntityType<Stray> type, Location location) {
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
        return 1.99f;
    }

    @Override
    public String getName() {
        return "Stray";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.BONE), Item.get(ItemIds.ARROW)};
    }

    @Override
    public boolean isUndead() {
        return true;
    }

}
