package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {
    public EntityCreature(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    // Armor stands, when implemented, should also check this.
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            return applyNameTag(player, item);
        }
        return false;
    }

    // Structured like this so I can override nametags in player and dragon classes
    // without overriding onInteract.
    protected boolean applyNameTag(Player player, Item item) {
        if (item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);
            return true; // onInteract: true = decrease count
        }
        return false;
    }
}
