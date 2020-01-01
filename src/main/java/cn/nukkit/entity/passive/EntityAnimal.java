package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;

import static cn.nukkit.entity.data.EntityFlag.BABY;
import static cn.nukkit.item.ItemIds.NAME_TAG;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityAnimal extends EntityCreature implements EntityAgeable {
    public EntityAnimal(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getFlag(BABY);
    }

    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemIds.WHEAT; //default
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
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
