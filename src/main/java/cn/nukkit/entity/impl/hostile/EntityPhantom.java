package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.Phantom;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;

/**
 * Created by PetteriM1
 */
public class EntityPhantom extends EntityHostile implements Phantom, Smiteable {

    public EntityPhantom(EntityType<Phantom> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public String getName() {
        return "Phantom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(470)};
    }
}
