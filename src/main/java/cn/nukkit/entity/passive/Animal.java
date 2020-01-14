package cn.nukkit.entity.passive;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;

import static cn.nukkit.entity.data.EntityFlag.BABY;
import static cn.nukkit.item.ItemIds.NAME_TAG;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Animal<T extends Entity> extends Creature<T> implements EntityAgeable {
    public Animal(EntityType<T> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getFlag(BABY);
    }

    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemIds.WHEAT; //default
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3f clickedPos) {
        if (item.getId() == NAME_TAG) {
            if (item.hasCustomName()) {
                this.setNameTag(item.getCustomName());
                this.setNameTagVisible(true);
                player.getInventory().removeItem(item);
                return true;
            }
        }
        return false;
    }
}
