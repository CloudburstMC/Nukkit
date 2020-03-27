package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Chicken;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Identifier;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntityChicken extends Animal implements Chicken {

    public EntityChicken(EntityType<Chicken> type, Location location) {
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
            return 0.35f;
        }
        return 0.7f;
    }

    @Override
    public String getName() {
        return "Chicken";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.CHICKEN), Item.get(ItemIds.FEATHER)};
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(4);
    }

    @Override
    public boolean isBreedingItem(Item item) {
        Identifier id = item.getId();

        return id == ItemIds.WHEAT_SEEDS || id == ItemIds.MELON_SEEDS || id == ItemIds.PUMPKIN_SEEDS ||
                id == ItemIds.BEETROOT_SEEDS;
    }
}
