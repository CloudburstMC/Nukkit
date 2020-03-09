package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.Vindicator;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;

import static cn.nukkit.item.ItemIds.IRON_AXE;

/**
 * @author PikyCZ
 */
public class EntityVindicator extends EntityHostile implements Vindicator {

    public EntityVindicator(EntityType<Vindicator> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(24);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "Vindicator";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(IRON_AXE)};
    }
}
